package com.chethan.demoproject

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chethan.demoproject.model.Contributor
import com.chethan.demoproject.model.RepoData
import org.koin.standalone.KoinComponent


class RepoViewModel(val dataRepository: DataRepository) : ViewModel(), KoinComponent {

    var repoData = MutableLiveData<RepoData>()
    var contributors = MutableLiveData<List<Contributor>>()

    init {
        contributors.value = listOf()
    }

    fun getRepoData(searchText: String, pageNumber: String, pageLimit: String) {
        dataRepository.getRepoData(
            searchText,
            pageNumber,
            pageLimit,
            object : DataRepository.OnRepoData {
                override fun onSuccess(data: RepoData) {
                    repoData.value = data
                }

                override fun onFailure() {
                    //REQUEST FAILED
                }
            })
    }

    fun getContributors(login: String,repoName: String) {
        dataRepository.getContributors(login,repoName, object : DataRepository.OnContributorsData {
            override fun onSuccess(data: List<Contributor>) {
                contributors.value = data
            }

            override fun onFailure() {
                Log.e("onFailure:","true")
            }
        })
    }

}