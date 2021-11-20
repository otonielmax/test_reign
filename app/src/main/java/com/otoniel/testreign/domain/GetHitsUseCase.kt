package com.otoniel.testreign.domain

import com.otoniel.testreign.data.HitsRepository
import com.otoniel.testreign.data.model.HitsModel

class GetHitsUseCase {

    private val repository = HitsRepository()

    suspend operator fun invoke(): List<HitsModel>? = repository.getAllHits()
}