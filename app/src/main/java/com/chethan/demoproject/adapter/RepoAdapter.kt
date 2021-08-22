package com.chethan.demoproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chethan.demoproject.R
import com.chethan.demoproject.model.Repo
/**
 *Created by Bhagavan Byreddy on 21/08/21.
 */
class RepoAdapter(
    val context: Context,
    val repos: MutableList<Repo>,
    val listener:ItemClickListener
) : RecyclerView.Adapter<RepoAdapter.RepoHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RepoAdapter.RepoHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.repo_item_layout,
            viewGroup,
            false
        )
        return RepoHolder(view)
    }

    override fun onBindViewHolder(holder: RepoAdapter.RepoHolder, position: Int) {
        val repo = repos.get(position)
        holder.repoNameTv.text = repo.fullName
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    inner class RepoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repoNameTv = itemView.findViewById<TextView>(R.id.repoNameTv)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}