package com.example.tvshowexplorer.data.api

import com.example.tvshowexplorer.data.model.ImageUrl
import com.example.tvshowexplorer.data.model.SearchResponse
import com.example.tvshowexplorer.data.model.TvShow
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Data models for Cast response
data class CastResponse(
    @SerializedName("person") val person: Person,
    @SerializedName("character") val character: Character
)

data class Person(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: ImageUrl?
)

data class Character(
    @SerializedName("name") val name: String
)

// Data model for Episode response
data class Episode(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("season") val season: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("airdate") val airdate: String?,
    @SerializedName("summary") val summary: String? // Added summary field
)

interface TvMazeApiService {

    @GET("shows")
    suspend fun getPopularShows(): List<TvShow>

    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<SearchResponse>

    @GET("shows/{id}/cast")
    suspend fun getShowCast(@Path("id") showId: Int): List<CastResponse>

    @GET("shows/{id}/episodes")
    suspend fun getShowEpisodes(@Path("id") showId: Int): List<Episode>
}