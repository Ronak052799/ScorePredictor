package com.example.match_preview_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class MatchPreviewRequest(
    val team1: String,
    val team2: String
)


interface MatchPreviewService {
    @POST("/match-preview")
    fun getMatchPreview(@Body request: MatchPreviewRequest): Call<MatchPreviewResponse>
}
