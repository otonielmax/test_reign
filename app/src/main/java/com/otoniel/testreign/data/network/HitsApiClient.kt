package com.otoniel.testreign.data.network

import com.otoniel.testreign.data.model.HitsResponseModel
import retrofit2.Response
import retrofit2.http.GET

interface HitsApiClient {

    @GET("search_by_date?query=mobile")
    suspend fun getAllHits(): Response<HitsResponseModel>
}