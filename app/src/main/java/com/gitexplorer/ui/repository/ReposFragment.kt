package com.gitexplorer.ui.repository

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.gitexplorer.R
import com.gitexplorer.databinding.FragmentRepositoryBinding
import com.gitexplorer.ui.repository.ReposViewModel.Companion.DEFAULT_QUERY
import com.gitexplorer.ui.repository.adapter.ReposAdapter
import com.gitexplorer.ui.repository.adapter.ReposLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReposFragment : Fragment(R.layout.fragment_repository) {

    private val viewModel: ReposViewModel by viewModels()

    private var _binding: FragmentRepositoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        _binding = FragmentRepositoryBinding.bind(view)

        val adapter = ReposAdapter()

        binding.apply {

            recycler.apply {
                setHasFixedSize(true)
                itemAnimator = null
                this.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = ReposLoadStateAdapter { adapter.retry() },
                    footer = ReposLoadStateAdapter { adapter.retry() }
                )
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }

            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }



        viewModel.repos.observe(viewLifecycleOwner) {

            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progress.isVisible = loadState.source.refresh is LoadState.Loading
                recycler.isVisible = loadState.source.refresh is LoadState.NotLoading
                savedRepository.isVisible =
                    editTextSearch.text.isNullOrEmpty() && loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                error.isVisible = loadState.source.refresh is LoadState.Error

                // no results found
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    savedRepository.isVisible = false
                    recycler.isVisible = false
                    emptyView.isVisible = true
                } else {
                    emptyView.isVisible = false
                }

                editTextSearch.doAfterTextChanged {
                    it?.let {
                        if (it.length > 2 || it.isEmpty()) {
                            search()
                        }
                    }

                }
                editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                    when {
                        (actionId == EditorInfo.IME_ACTION_DONE) -> {
                            val queryString = binding.editTextSearch.text.toString()
                            if (queryString.length > 2 || queryString.isEmpty()) {
                                search()
                            }
                            false
                        }
                        else -> false
                    }
                }
            }
        }


        setHasOptionsMenu(true)
    }

    private fun search() {
        val queryString = binding.editTextSearch.text.toString()
        binding.recycler.scrollToPosition(0)
        if (queryString.isNotEmpty()) {
            viewModel.searchRepos(String.format(getString(R.string.search_query), queryString))
        } else {
            viewModel.searchRepos(DEFAULT_QUERY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}