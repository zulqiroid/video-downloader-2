package com.all.video.downloader.fast.hd.secure.video.downloader.data.billing

import android.app.Activity
import android.content.Context
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumPlanType
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumProduct
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumBillingRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumBillingResult
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.premium.PremiumEntitlementRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppPremiumRemoteConfig
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GooglePlayPremiumBillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val premiumEntitlementRepository: PremiumEntitlementRepository
) : PremiumBillingRepository {

    private val cachedProducts = mutableMapOf<String, CachedProduct>()

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .enablePendingPurchases(
                PendingPurchasesParams
                    .newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .setListener { billingResult, purchases ->
                handlePurchasesUpdated(
                    billingResult = billingResult,
                    purchases = purchases.orEmpty()
                )
            }
            .build()
    }

    override suspend fun loadProducts(
        config: AppPremiumRemoteConfig
    ): List<PremiumProduct> {
        ensureConnected()

        cachedProducts.clear()

        val subscriptions = buildList {
            if (config.showWeeklyPlan) {
                config.weeklyProductId?.let { productId ->
                    add(productId to PremiumPlanType.WEEKLY)
                }
            }

            if (config.showMonthlyPlan) {
                config.monthlyProductId?.let { productId ->
                    add(productId to PremiumPlanType.MONTHLY)
                }
            }

            if (config.showYearlyPlan) {
                config.yearlyProductId?.let { productId ->
                    add(productId to PremiumPlanType.YEARLY)
                }
            }
        }

        val inApps = buildList {
            if (config.showLifetimePlan) {
                config.lifetimeProductId?.let { productId ->
                    add(productId to PremiumPlanType.LIFETIME)
                }
            }
        }

        val subscriptionProducts = queryProducts(
            products = subscriptions,
            productType = ProductType.SUBS
        )

        val inAppProducts = queryProducts(
            products = inApps,
            productType = ProductType.INAPP
        )

        restorePurchases()

        return subscriptionProducts + inAppProducts
    }

    override suspend fun launchPurchase(
        activity: Activity,
        productId: String
    ): PremiumBillingResult {
        ensureConnected()

        val cachedProduct = cachedProducts[productId]
            ?: return PremiumBillingResult.Failure("Product is not available right now.")

        val productDetailsParamsBuilder =
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
                .setProductDetails(cachedProduct.productDetails)

        cachedProduct.offerToken?.let { offerToken ->
            productDetailsParamsBuilder.setOfferToken(offerToken)
        }

        val flowParams = BillingFlowParams
            .newBuilder()
            .setProductDetailsParamsList(
                listOf(productDetailsParamsBuilder.build())
            )
            .build()

        val result = billingClient.launchBillingFlow(
            activity,
            flowParams
        )

        return if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            PremiumBillingResult.FlowStarted
        } else {
            PremiumBillingResult.Failure(
                result.debugMessage.ifBlank { "Unable to start purchase." }
            )
        }
    }

    override suspend fun restorePurchases(): PremiumBillingResult {
        ensureConnected()

        val subPurchases = queryPurchases(ProductType.SUBS)
        val inAppPurchases = queryPurchases(ProductType.INAPP)

        val allPurchases = subPurchases + inAppPurchases

        if (allPurchases.isEmpty()) {
            premiumEntitlementRepository.clearPremiumEntitlement()
            return PremiumBillingResult.Success
        }

        allPurchases.forEach { purchase ->
            grantEntitlementIfPurchased(purchase)
        }

        return PremiumBillingResult.Success
    }

    private suspend fun ensureConnected(): BillingResult {
        if (billingClient.isReady) {
            return BillingResult
                .newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .build()
        }

        return suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        Timber.tag(TAG).w("Billing service disconnected.")
                    }

                    override fun onBillingSetupFinished(
                        billingResult: BillingResult
                    ) {
                        continuation.resume(billingResult)
                    }
                }
            )
        }
    }

    private suspend fun queryProducts(
        products: List<Pair<String, PremiumPlanType>>,
        productType: String
    ): List<PremiumProduct> {
        if (products.isEmpty()) return emptyList()

        val productList = products.map { (productId, _) ->
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        }

        val params = QueryProductDetailsParams
            .newBuilder()
            .setProductList(productList)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    Timber.tag(TAG).w(
                        "Product query failed: ${billingResult.debugMessage}"
                    )
                    continuation.resume(emptyList())
                    return@queryProductDetailsAsync
                }

                val detailsList = productDetailsResult.productDetailsList.orEmpty()

                val mapped = detailsList.mapNotNull { productDetails ->
                    val planType = products.firstOrNull { (productId, _) ->
                        productId == productDetails.productId
                    }?.second ?: return@mapNotNull null

                    val cachedProduct = productDetails.toCachedProduct(
                        planType = planType,
                        productType = productType
                    )

                    cachedProducts[productDetails.productId] = cachedProduct

                    PremiumProduct(
                        type = planType,
                        productId = productDetails.productId,
                        title = planType.title(),
                        price = cachedProduct.price,
                        periodLabel = planType.periodLabel(),
                        isSubscription = productType == ProductType.SUBS
                    )
                }

                continuation.resume(mapped)
            }
        }
    }

    private suspend fun queryPurchases(
        productType: String
    ): List<Purchase> {
        val params = QueryPurchasesParams
            .newBuilder()
            .setProductType(productType)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(purchases)
                } else {
                    continuation.resume(emptyList())
                }
            }
        }
    }

    private fun handlePurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>
    ) {
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Timber.tag(TAG).w("Purchase update failed: ${billingResult.debugMessage}")
            return
        }

        purchases.forEach { purchase ->
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                grantEntitlementIfPurchased(purchase)
            }
        }
    }

    private suspend fun grantEntitlementIfPurchased(
        purchase: Purchase
    ) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        val purchasedProductId = purchase.products.firstOrNull()
            ?: return

        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            suspendCancellableCoroutine<Unit> { continuation ->
                billingClient.acknowledgePurchase(params) {
                    continuation.resume(Unit)
                }
            }
        }

        val isLifetime = cachedProducts[purchasedProductId]?.planType == PremiumPlanType.LIFETIME ||
                purchasedProductId.contains("lifetime", ignoreCase = true) ||
                purchasedProductId.contains("remove_ads", ignoreCase = true)

        premiumEntitlementRepository.setPremiumEntitlement(
            productId = purchasedProductId,
            hasLifetimePurchase = isLifetime
        )
    }

    private fun ProductDetails.toCachedProduct(
        planType: PremiumPlanType,
        productType: String
    ): CachedProduct {
        val subscriptionOffer = subscriptionOfferDetails
            ?.firstOrNull()

        val price = if (productType == ProductType.SUBS) {
            subscriptionOffer
                ?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice
                ?: "-"
        } else {
            oneTimePurchaseOfferDetails?.formattedPrice ?: "-"
        }

        return CachedProduct(
            planType = planType,
            productDetails = this,
            offerToken = subscriptionOffer?.offerToken,
            price = price
        )
    }

    private fun PremiumPlanType.title(): String {
        return when (this) {
            PremiumPlanType.WEEKLY -> "WEEKLY PRO"
            PremiumPlanType.MONTHLY -> "MONTHLY PLUS"
            PremiumPlanType.YEARLY -> "YEARLY PREMIUM"
            PremiumPlanType.LIFETIME -> "LIFETIME ELITE"
        }
    }

    private fun PremiumPlanType.periodLabel(): String {
        return when (this) {
            PremiumPlanType.WEEKLY -> "/week"
            PremiumPlanType.MONTHLY -> "/month"
            PremiumPlanType.YEARLY -> "/year"
            PremiumPlanType.LIFETIME -> "/lifetime"
        }
    }

    private data class CachedProduct(
        val planType: PremiumPlanType,
        val productDetails: ProductDetails,
        val offerToken: String?,
        val price: String
    )

    private companion object {
        private const val TAG = "PremiumBilling"
    }
}