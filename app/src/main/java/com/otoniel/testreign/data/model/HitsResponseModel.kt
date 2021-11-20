package com.otoniel.testreign.data.model

data class HitsResponseModel(
    val hits: List<HitsModel>,
    val nbHits: Int,
    val page: Int,
    val nbPages: Int,
    val hitsPerPage: Int,
    val exhaustiveNbHits: Boolean,
    val exhaustiveTypo: Boolean,
    val query: String,
    val params: String,
    val renderingContent: Any,
    val processingTimeMS: Int,
)
