package com.otoniel.testreign.data.model

data class CommentModel(
    val value: String,
    val matchLevel: String,
    val fullyHighlighted: Boolean,
    val matchedWords: List<String>,
)
