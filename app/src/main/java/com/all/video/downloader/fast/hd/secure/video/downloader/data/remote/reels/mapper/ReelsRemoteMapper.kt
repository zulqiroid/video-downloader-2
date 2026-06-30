package com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.mapper

import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.dto.ReelItemResponseDto
import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.dto.ReelsCategoryResponseDto
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.Reel
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.ReelCategory

fun List<ReelsCategoryResponseDto>.toDomainReelCategories(): List<ReelCategory> {
    return asSequence()
        .mapNotNull { categoryDto -> categoryDto.toDomainOrNull() }
        .sortedBy { category -> category.categoryIndex }
        .toList()
}

private fun ReelsCategoryResponseDto.toDomainOrNull(): ReelCategory? {
    val safeId = id ?: return null
    val safeName = name?.trim().orEmpty().ifBlank { "Reels" }

    val mappedReels = items
        .asSequence()
        .mapNotNull { itemDto -> itemDto.toDomainOrNull(categoryName = safeName) }
        .distinctBy { reel -> reel.id }
        .toList()

    return ReelCategory(
        id = safeId,
        name = safeName,
        thumbnail = thumbnail?.trim().orEmpty(),
        categoryIndex = categoryIndex ?: Int.MAX_VALUE,
        reels = mappedReels
    )
}

private fun ReelItemResponseDto.toDomainOrNull(
    categoryName: String
): Reel? {
    val safeId = id ?: return null
    val safeUrl = url?.trim().orEmpty()

    if (safeUrl.isBlank()) return null

    val normalizedType = type?.trim()?.lowercase().orEmpty()

    if (normalizedType.isNotBlank() && normalizedType != SUPPORTED_VIDEO_TYPE) {
        return null
    }

    return Reel(
        id = safeId.toString(),
        videoUrl = safeUrl,
        categoryId = category,
        categoryName = categoryName,
        isAd = isAd,
        isPremium = isPremium,
        type = normalizedType.ifBlank { SUPPORTED_VIDEO_TYPE },
        createdAt = createdAt
    )
}

private const val SUPPORTED_VIDEO_TYPE = "video"