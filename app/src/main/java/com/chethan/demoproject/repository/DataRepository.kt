package com.chethan.demoproject

import android.util.Log
import com.chethan.demoproject.model.Contributor
import com.chethan.demoproject.model.RepoData
import retrofit2.Call
import retrofit2.Response

class DataRepository(val netWorkApi: NetWorkApi) {

    fun getRepoData(searchText:String,pageNumber:String,pageLimit:String,onRepoData: OnRepoData) {
        netWorkApi.getRepoData(searchText,pageNumber,pageLimit).enqueue(object : retrofit2.Callback<RepoData> {
            override fun onResponse(call: Call<RepoData>, response: Response<RepoData>) {
                if(response.body() != null) {
                    onRepoData.onSuccess((response.body() as RepoData))
                }else{
                    onRepoData.onFailure()
                }
            }

            override fun onFailure(call: Call<RepoData>, t: Throwable) {
                onRepoData.onFailure()
            }
        })
    }

    fun getContributors(login:String,repoName:String,onContributorsData: OnContributorsData) {
        netWorkApi.getContributors(login,repoName).enqueue(object : retrofit2.Callback<List<Contributor>> {
            override fun onResponse(call: Call<List<Contributor>>, response: Response<List<Contributor>>) {
                if(response.body() != null) {
                    onContributorsData.onSuccess((response.body() as  List<Contributor>))
                }else{
                    onContributorsData.onFailure()
                }
            }
            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                onContributorsData.onFailure()
                Log.e("onFailure::",t.message)
            }
        })
    }

    interface OnRepoData {
        fun onSuccess(data: RepoData)
        fun onFailure()
    }

    interface OnContributorsData {
        fun onSuccess(data: List<Contributor>)
        fun onFailure()
    }
}

