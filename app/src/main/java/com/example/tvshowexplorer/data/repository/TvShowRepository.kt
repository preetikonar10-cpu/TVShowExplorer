package com.example.tvshowexplorer.data.repository

import com.example.tvshowexplorer.data.api.CastResponse
import com.example.tvshowexplorer.data.api.Episode
import com.example.tvshowexplorer.data.api.RetrofitClient
import com.example.tvshowexplorer.data.model.TvShow
import com.example.tvshowexplorer.ui.state.UiState
import retrofit2.HttpException
import java.io.IOException

class TvShowRepository {
    private val api = RetrofitClient.apiService

    suspend fun fetchPopularShows(): UiState<List<TvShow>> {
        return try {
            UiState.Success(api.getPopularShows())
        } catch (e: IOException) {
            UiState.Error("No Internet Connection. Check Wi-Fi or Data.")
        } catch (e: HttpException) {
            UiState.Error("Server Error (${e.code()}). Please try again later.")
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "An unexpected error occurred.")
        }
    }

    suspend fun searchShows(query: String): UiState<List<TvShow>> {
        return try {
            val response = api.searchShows(query)
            UiState.Success(response.map { it.show })
        } catch (e: IOException) {
            UiState.Error("No Internet Connection. Check Wi-Fi or Data.")
        } catch (e: HttpException) {
            UiState.Error("Server Error (${e.code()}). Please try again later.")
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "An unexpected error occurred.")
        }
    }

    suspend fun fetchShowCast(showId: Int): UiState<List<CastResponse>> {
        return try {
            UiState.Success(api.getShowCast(showId))
        } catch (e: Exception) {
            UiState.Error("Failed to load cast members.")
        }
    }

    suspend fun fetchShowEpisodes(showId: Int): UiState<List<Episode>> {
        return try {
            UiState.Success(api.getShowEpisodes(showId))
        } catch (e: Exception) {
            UiState.Error("Failed to load episodes.")
        }
    }
}