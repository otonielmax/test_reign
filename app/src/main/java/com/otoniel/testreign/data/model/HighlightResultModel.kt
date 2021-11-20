package com.otoniel.testreign.data.model

data class HighlightResultModel(
    val author: AuthorModel,
    val comment_text: CommentModel,
    val story_title: StoryModel,
    val story_url: StoryURLModel,
)
