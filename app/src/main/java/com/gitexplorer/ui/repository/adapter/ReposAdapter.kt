package com.gitexplorer.ui.repository.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitexplorer.data.model.Repo
import com.gitexplorer.databinding.ItemRepositoryBinding

class ReposAdapter: PagingDataAdapter<Repo, ReposAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { repo ->
            with(holder) {
                itemView.tag = repo
                if (repo != null) {
                    bind(createOnClickListener(binding, repo), repo)
                }
            }
        }
    }

    private fun createOnClickListener(binding : ItemRepositoryBinding, repo: Repo): View.OnClickListener {
        return View.OnClickListener {
        }
    }

    class ViewHolder(val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, repo: Repo) {

            binding.apply {

                Glide.with(itemView)
                    .load(repo.owner.avatar_url)
                    .centerCrop()
                    .error(android.R.drawable.stat_notify_error)
                    .into(avatar)

                val str = SpannableString( repo.owner.login + " / " + repo.name)
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    repo.owner.login.length,
                    str.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                name.text = str

                description.text = repo.description

                ViewCompat.setTransitionName(this.avatar, "avatar_${repo.id}")

                root.setOnClickListener(listener)
            }

        }
    }
}