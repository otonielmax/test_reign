package com.otoniel.testreign.data.network

import com.otoniel.testreign.core.RetrofitHelper
import com.otoniel.testreign.data.model.HitsModel

class HitsService {

    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getAllHits(): List<HitsModel> {
        val response = retrofit.create(HitsApiClient::class.java).getAllHits()
        return response.body()?.hits ?: emptyList()
    }
}