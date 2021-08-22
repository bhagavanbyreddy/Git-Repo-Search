package com.chethan.demoproject.model


import com.google.gson.annotations.SerializedName

data class RepoData(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean? = null,
    @SerializedName("items")
    val items: List<Repo>? = null,
    @SerializedName("total_count")
    val totalCount: Int? = null
)