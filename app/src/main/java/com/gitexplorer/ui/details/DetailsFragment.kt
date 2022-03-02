package com.gitexplorer.ui.details

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.gitexplorer.R
import com.gitexplorer.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        _binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            name.text = args.repo.owner.login + " / " + args.repo.name
            language.text = args.repo.language

            avatar.apply {
                transitionName = args.repo.owner.avatar_url
                Glide.with(view)
                    .load(args.repo.owner.avatar_url)
                    .error(android.R.drawable.stat_notify_error)
                    .into(this)
            }

            starsValue.text = args.repo.stars.toString()
            forksValue.text = args.repo.forks.toString()
            issuesValue.text = args.repo.openIssues.toString()
            //versionValue.text = args.repo.ver.toString()

            ivBack.setOnClickListener {
                view.let { Navigation.findNavController(it).navigateUp() }

            }

            ViewCompat.setTransitionName(binding.avatar, "avatar_${args.repo.id}")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}