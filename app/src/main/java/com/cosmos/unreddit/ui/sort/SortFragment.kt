package com.cosmos.unreddit.ui.sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.cosmos.unreddit.data.model.Sorting
import com.cosmos.unreddit.data.remote.api.reddit.RedditApi
import com.cosmos.unreddit.databinding.FragmentSortBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class SortFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSortBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChoices()
    }

    private fun initChoices() {
        val type = arguments?.getSerializable(BUNDLE_KEY_TYPE) as? SortType ?: SortType.GENERAL
        binding.type = type

        val sorting = arguments?.getParcelable(BUNDLE_KEY_SORTING) as? Sorting ?: return
        with(sorting) {
            when (generalSorting) {
                RedditApi.Sort.HOT -> binding.chipHot.isChecked = true
                RedditApi.Sort.NEW -> binding.chipNew.isChecked = true
                RedditApi.Sort.TOP -> {
                    binding.chipTop.isChecked = true
                    if (type != SortType.POST) {
                        showTimeGroup()
                    }
                }
                RedditApi.Sort.RISING -> binding.chipRising.isChecked = true
                RedditApi.Sort.CONTROVERSIAL -> {
                    binding.chipControversial.isChecked = true
                    if (type != SortType.POST) {
                        showTimeGroup()
                    }
                }
                RedditApi.Sort.RELEVANCE -> {
                    binding.chipRelevance.isChecked = true
                    showTimeGroup()
                }
                RedditApi.Sort.COMMENTS -> {
                    binding.chipComments.isChecked = true
                    showTimeGroup()
                }
                RedditApi.Sort.BEST -> binding.chipBest.isChecked = true
                RedditApi.Sort.OLD -> binding.chipOld.isChecked = true
                RedditApi.Sort.QA -> binding.chipQa.isChecked = true
            }

            when (timeSorting) {
                RedditApi.TimeSorting.HOUR -> binding.chipHour.isChecked = true
                RedditApi.TimeSorting.DAY -> binding.chipDay.isChecked = true
                RedditApi.TimeSorting.WEEK -> binding.chipWeek.isChecked = true
                RedditApi.TimeSorting.MONTH -> binding.chipMonth.isChecked = true
                RedditApi.TimeSorting.YEAR -> binding.chipYear.isChecked = true
                RedditApi.TimeSorting.ALL -> binding.chipAll.isChecked = true
            }
        }

        binding.groupGeneral.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.chipHot.id, binding.chipNew.id, binding.chipRising.id, binding.chipBest.id,
                binding.chipOld.id, binding.chipQa.id -> setChoice(false)
                binding.chipTop.id, binding.chipControversial.id, binding.chipRelevance.id,
                binding.chipComments.id -> {
                    if (type != SortType.POST) {
                        binding.groupTime.clearCheck()
                        showTimeGroup()
                    } else {
                        setChoice(false)
                    }
                }
            }
        }

        binding.groupTime.setOnCheckedChangeListener { group, checkedId ->
            if (group.findViewById<Chip?>(checkedId)?.isChecked == true) {
                setChoice(true)
            }
        }
    }

    private fun getGeneralChoice(): RedditApi.Sort? {
        return when (binding.groupGeneral.checkedChipId) {
            binding.chipHot.id -> RedditApi.Sort.HOT
            binding.chipNew.id -> RedditApi.Sort.NEW
            binding.chipTop.id -> RedditApi.Sort.TOP
            binding.chipRising.id -> RedditApi.Sort.RISING
            binding.chipControversial.id -> RedditApi.Sort.CONTROVERSIAL
            binding.chipRelevance.id -> RedditApi.Sort.RELEVANCE
            binding.chipComments.id -> RedditApi.Sort.COMMENTS
            binding.chipBest.id -> RedditApi.Sort.BEST
            binding.chipOld.id -> RedditApi.Sort.OLD
            binding.chipQa.id -> RedditApi.Sort.QA
            else -> null
        }
    }

    private fun getTimeChoice(): RedditApi.TimeSorting? {
        return when (binding.groupTime.checkedChipId) {
            binding.chipHour.id -> RedditApi.TimeSorting.HOUR
            binding.chipDay.id -> RedditApi.TimeSorting.DAY
            binding.chipWeek.id -> RedditApi.TimeSorting.WEEK
            binding.chipMonth.id -> RedditApi.TimeSorting.MONTH
            binding.chipYear.id -> RedditApi.TimeSorting.YEAR
            binding.chipAll.id -> RedditApi.TimeSorting.ALL
            else -> null
        }
    }

    private fun showTimeGroup() {
        val transition = Slide().apply {
            duration = 250
            addTarget(binding.textTimeLabel)
            addTarget(binding.groupTime)
        }
        TransitionManager.beginDelayedTransition(binding.layoutRoot, transition)
        binding.groupViewTime.visibility = View.VISIBLE
        binding.textTimeLabel.visibility = View.VISIBLE
        binding.groupTime.visibility = View.VISIBLE
    }

    private fun setChoice(withTime: Boolean) {
        val sort = getGeneralChoice() ?: return
        val timeSorting = if (withTime) getTimeChoice() else null

        setFragmentResult(
            REQUEST_KEY_SORTING,
            bundleOf(BUNDLE_KEY_SORTING to Sorting(sort, timeSorting))
        )

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class SortType {
        GENERAL, SEARCH, POST
    }

    companion object {
        private const val TAG = "SortFragment"

        const val REQUEST_KEY_SORTING = "REQUEST_KEY_SORTING"

        const val BUNDLE_KEY_SORTING = "BUNDLE_KEY_SORTING"
        const val BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE"

        fun show(
            fragmentManager: FragmentManager,
            sorting: Sorting,
            type: SortType = SortType.GENERAL
        ) {
            SortFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_SORTING to sorting,
                    BUNDLE_KEY_TYPE to type
                )
            }.show(fragmentManager, TAG)
        }
    }
}
