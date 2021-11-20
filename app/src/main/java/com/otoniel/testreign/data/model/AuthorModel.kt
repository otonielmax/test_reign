package com.otoniel.testreign.data.model

data class AuthorModel(
    val value: String,
    val matchLevel: String,
    val matchedWords: List<String>,
)