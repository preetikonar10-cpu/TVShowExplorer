package com.example.tvshowexplorer.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("show") val show: TvShow
)

data class TvShow(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("rating") val rating: Rating?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("image") val image: ImageUrl?,
    @SerializedName("officialSite") val officialSite: String?,
    @SerializedName("url") val url: String?
)

data class Rating(
    @SerializedName("average") val average: Double?
)

data class ImageUrl(
    @SerializedName("medium") val medium: String?,
    @SerializedName("original") val original: String?
)