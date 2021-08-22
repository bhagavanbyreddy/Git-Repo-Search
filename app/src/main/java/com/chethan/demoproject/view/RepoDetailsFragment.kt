package com.chethan.demoproject.view

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.chethan.demoproject.R
import com.chethan.demoproject.RepoViewModel
import com.chethan.demoproject.helper.loadCircularImage
import com.chethan.demoproject.model.Contributor
import com.chethan.demoproject.model.Repo
import com.chethan.demoproject.model.RepoData
import kotlinx.android.synthetic.main.repo_details_layout.*
import kotlinx.android.synthetic.main.repo_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


/**
 *Created by Bhagavan Byreddy on 22/08/21.
 */
class RepoDetailsFragment : Fragment() {

    lateinit var navController:NavController
    private val repoViewtModel: RepoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.repo_details_layout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        arguments?.let {
            if(requireArguments().containsKey("repo")){
                val repo: Repo = requireArguments().getSerializable("repo") as Repo
                bindUI(repo)

                repoViewtModel.getContributors(repo.owner!!.login!!,repo.name!!)
                repoViewtModel.contributors.observe(
                    viewLifecycleOwner,
                    Observer(function = fun(contributors: List<Contributor>?) {
                        if (!contributors.isNullOrEmpty()) {
                            bindContributorsData(contributors)
                        }
                        //progress.visibility = View.GONE
                    })
                )
            }
        }

    }

    private fun bindContributorsData(contributors: List<Contributor>) {
        if(contributors.isNotEmpty()) {
            val contributor = contributors.get(0)

            if (!contributor.avatarUrl.isNullOrEmpty()) {
                contributorIv.loadCircularImage(
                    contributor.avatarUrl, // or any object supported by Glide
                    5F, // default is 0. If you don't change it, then the image will have no border
                    Color.GRAY // optional, default is white
                )
            }

            idTv.text = "Contributor Id: ${contributor.id.toString()}"
            contributionsCountTv.text = "Contributions: ${contributor.contributions.toString()}"
        }

    }

    private fun bindUI(repo: Repo) {

        if(repo.owner != null && !repo.owner.avatarUrl.isNullOrEmpty()){
            repoIv.loadCircularImage(
                repo.owner.avatarUrl, // or any object supported by Glide
                5F, // default is 0. If you don't change it, then the image will have no border
                Color.GRAY // optional, default is white
            )
        }

        repoNameValTv.text = repo.fullName
        descriptionValTv.text = repo.description
        projectLinkValTv.text = repo.htmlUrl
        projectLinkValTv.paintFlags = projectLinkValTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        projectLinkValTv.setOnClickListener {
            val bundle = bundleOf()
            bundle.putString("projectLink",repo.htmlUrl)
            navController.navigate(R.id.action_repoDetailsFragment_to_repoProjectFragment,bundle)
        }

    }
}