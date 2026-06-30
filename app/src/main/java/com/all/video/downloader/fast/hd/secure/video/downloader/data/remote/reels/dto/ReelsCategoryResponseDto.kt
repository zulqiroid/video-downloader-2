package com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReelsCategoryResponseDto(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("thumbnail")
    val thumbnail: String? = null,

    @SerialName("category_index")
    val categoryIndex: Int? = null,

    @SerialName("items")
    val items: List<ReelItemResponseDto> = emptyList()
)

@Serializable
data class ReelItemResponseDto(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("category")
    val category: Int? = null,

    @SerialName("is_ad")
    val isAd: Boolean = false,

    @SerialName("is_premium")
    val isPremium: Boolean = false,

    @SerialName("url")
    val url: String? = null,

    @SerialName("is_type")
    val type: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)