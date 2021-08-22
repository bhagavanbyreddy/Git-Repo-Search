package com.chethan.demoproject


import com.chethan.demoproject.constants.API_TO_GET_CONTRIBUTORS
import com.chethan.demoproject.constants.API_TO_GET_REPOS
import com.chethan.demoproject.model.Contributor
import com.chethan.demoproject.model.RepoData
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetWorkApi{

    @GET(API_TO_GET_REPOS)
    fun getRepoData(
        @Query("q")
        searchText:String,
        @Query("page")
        pageNumber:String,
        @Query("per_page")
        pageLimit:String
    ): Call<RepoData>

    @GET(API_TO_GET_CONTRIBUTORS)
    fun getContributors(
        @Path("login")
        login:String,
        @Path("repoName")
        repoName:String
    ): Call<List<Contributor>>

}