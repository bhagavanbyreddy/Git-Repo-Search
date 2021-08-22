package com.chethan.demoproject.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chethan.demoproject.R
import com.chethan.demoproject.RepoViewModel
import com.chethan.demoproject.adapter.RepoAdapter
import com.chethan.demoproject.helper.EndlessRecyclerViewScrollListener
import com.chethan.demoproject.model.Repo
import com.chethan.demoproject.model.RepoData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.repo_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


/**
 *Created by Bhagavan Byreddy on 21/08/21.
 */
class RepoListFragment : Fragment(), RepoAdapter.ItemClickListener {

    private var searchText: String? = null
    lateinit var layoutManager: LinearLayoutManager
    private val repoViewtModel: RepoViewModel by viewModel()
    lateinit var adapter: RepoAdapter
    var repos = mutableListOf<Repo>()
    private lateinit var snackbar: Snackbar
    private lateinit var recyclerViewScrollListener: EndlessRecyclerViewScrollListener
    var pageNumber: Int = 0
    val pageLimit = 10
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.repo_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        adapter = RepoAdapter(requireContext(), repos, this)
        layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        reporv.layoutManager = layoutManager
        reporv.adapter = adapter
        recyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                Log.e("page_number:", "${page}")
                if (page != 0) {
                    pageNumber = page
                    getRepoData()
                }
            }
        }
        reporv.addOnScrollListener(recyclerViewScrollListener)

        snackbar = Snackbar
            .make(reporv, "Loading...", Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchText = query
                getRepoData(true)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {
            pageNumber = 0
            repos.clear()
            adapter.notifyDataSetChanged()
            recyclerViewScrollListener.resetState()
            noDataTv.visibility = View.VISIBLE
            false
        }
    }

    private fun getRepoData(shouldReset: Boolean = false) {
        if (shouldReset) {
            pageNumber = 0
            repoViewtModel.repoData.value = RepoData()
            repos.clear()
            adapter.notifyDataSetChanged()
            recyclerViewScrollListener.resetState()
        }

        if(pageNumber == 0) {
            progressBar.visibility = View.VISIBLE
            noDataTv.visibility = View.GONE
        }else{
            if (!snackbar.isShown) {
                snackbar.show()
            }
        }
        searchText?.let {
            repoViewtModel.getRepoData(it, pageNumber.toString(), pageLimit.toString())
            repoViewtModel.repoData.observe(
                viewLifecycleOwner,
                Observer(function = fun(repoData: RepoData?) {
                    if (repoData != null && !repoData.items.isNullOrEmpty()) {
                        bindRepoData(repoData.items)
                    }
                    progressBar.visibility = View.GONE
                    Handler().postDelayed({
                        snackbar.dismiss()
                    }, 1000)
                })
            )
        }
    }

    private fun bindRepoData(items: List<Repo>) {
        repos.addAll(items)
        updateAdapter()
    }

    private fun updateAdapter() {
        reporv.post {
            adapter.notifyDataSetChanged()
        }
        if (!repos.isNullOrEmpty()) {
            reporv.visibility = View.VISIBLE
            noDataTv.visibility = View.GONE
        } else {
            reporv.visibility = View.GONE
            noDataTv.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(position: Int) {
        val repo = repos.get(position)
        val bundle = bundleOf()
        bundle.putSerializable("repo",repo)
        navController.navigate(R.id.action_repoListFragment_to_repoDetailsFragment,bundle)
    }
}