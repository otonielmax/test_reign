package com.otoniel.testreign.data

import com.otoniel.testreign.data.model.HitsModel
import com.otoniel.testreign.data.model.HitsProvider
import com.otoniel.testreign.data.network.HitsService

class HitsRepository {

    private val api = HitsService()

    suspend fun getAllHits(): List<HitsModel> {
        val response = api.getAllHits()
        HitsProvider.hits = response
        return response
    }
}