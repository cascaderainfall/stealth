package com.cosmos.unreddit.ui.preferences

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.model.preferences.ContentPreferences.PreferencesKeys
import com.cosmos.unreddit.data.model.preferences.DataPreferences
import com.cosmos.unreddit.data.model.preferences.DataPreferences.RedditSource.REDDIT
import com.cosmos.unreddit.data.model.preferences.DataPreferences.RedditSource.TEDDIT
import com.cosmos.unreddit.data.model.preferences.UiPreferences
import com.cosmos.unreddit.databinding.LayoutPreferenceListBinding
import com.cosmos.unreddit.ui.redditsource.RedditSourceDialogFragment
import com.cosmos.unreddit.util.extension.applyWindowInsets
import com.cosmos.unreddit.util.extension.getNavOptions
import com.cosmos.unreddit.util.extension.latest
import com.cosmos.unreddit.util.extension.launchRepeat
import com.cosmos.unreddit.util.extension.restart
import com.cosmos.unreddit.util.extension.serializable
import com.cosmos.unreddit.util.extension.unredditApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private var _binding: LayoutPreferenceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PreferencesViewModel by activityViewModels()

    private var nightModePreference: Preference? = null
    private var leftHandedModePreference: SwitchPreferenceCompat? = null
    private var showNsfwPreference: SwitchPreferenceCompat? = null
    private var showNsfwPreviewPreference: SwitchPreferenceCompat? = null
    private var showSpoilerPreviewPreference: SwitchPreferenceCompat? = null
    private var backupPreference: Preference? = null
    private var sourcePreference: Preference? = null
    private var privacyEnhancerPreference: Preference? = null
    private var aboutPreference: Preference? = null

    private val navOptions: NavOptions by lazy { getNavOptions() }

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initPreferences()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.applyWindowInsets(bottom = false)

        _binding = LayoutPreferenceListBinding.bind(view)

        // Retrieve the preference list to configure the RecyclerView
        val list = binding.listContainer.children.find { it is RecyclerView } as RecyclerView?
        list?.apply {
            applyWindowInsets(left = false, top = false, right = false)
            isVerticalScrollBarEnabled = false
            clipToPadding = false
        }

        initResultListener()
        bindViewModel()
    }

    private fun initPreferences() {
        nightModePreference = findPreference<Preference>(
            UiPreferences.PreferencesKeys.NIGHT_MODE.name
        )?.apply {
            setOnPreferenceClickListener {
                viewModel.nightMode.latest?.let { mode ->
                    UiPreferences.NightMode.asIndex(mode)?.let { index ->
                        showNightModeDialog(index)
                    }
                }
                true
            }
        }

        leftHandedModePreference = findPreference<SwitchPreferenceCompat>(
            UiPreferences.PreferencesKeys.LEFT_HANDED_MODE.name
        )?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setLeftHandedMode(newValue as Boolean)
                true
            }
        }

        showNsfwPreference = findPreference<SwitchPreferenceCompat>(
            PreferencesKeys.SHOW_NSFW.name
        )?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setShowNsfw(newValue as Boolean)
                true
            }
        }

        showNsfwPreviewPreference = findPreference<SwitchPreferenceCompat>(
            PreferencesKeys.SHOW_NSFW_PREVIEW.name
        )?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setShowNsfwPreview(newValue as Boolean)
                true
            }
        }

        showSpoilerPreviewPreference = findPreference<SwitchPreferenceCompat>(
            PreferencesKeys.SHOW_SPOILER_PREVIEW.name
        )?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                viewModel.setShowSpoilerPreview(newValue as Boolean)
                true
            }
        }

        backupPreference = findPreference<Preference>("backup")?.apply {
            setOnPreferenceClickListener {
                openBackup()
                true
            }
        }

        sourcePreference = findPreference<Preference?>(
            DataPreferences.PreferencesKeys.REDDIT_SOURCE.name
        )?.apply {
            setOnPreferenceClickListener {
                viewModel.redditSource.latest?.let { source ->
                    showRedditSourceDialog(source.first, source.second)
                }
                true
            }
        }

        privacyEnhancerPreference = findPreference<Preference?>(
            DataPreferences.PreferencesKeys.PRIVACY_ENHANCER.name
        )?.apply {
            setOnPreferenceClickListener {
                openPrivacyEnhancer()
                true
            }
        }

        aboutPreference = findPreference<Preference>("about")?.apply {
            setOnPreferenceClickListener {
                openAbout()
                true
            }
        }
    }

    private fun initResultListener() {
        childFragmentManager.setFragmentResultListener(
            RedditSourceDialogFragment.REQUEST_KEY_SOURCE,
            viewLifecycleOwner
        ) { _, bundle ->
            val source = bundle.serializable(RedditSourceDialogFragment.KEY_SOURCE)
                ?: REDDIT
            val instance = bundle.serializable<String>(RedditSourceDialogFragment.KEY_INSTANCE)

            when (source) {
                REDDIT -> {
                    // Update value without asking for confirmation
                    updateRedditSource(source.value)
                }
                TEDDIT -> {
                    // Show disclaimer to user
                    showRedditSourceDisclaimer(source, instance)
                }
            }
        }
    }

    private fun bindViewModel() {
        launchRepeat(Lifecycle.State.STARTED) {
            launch {
                viewModel.nightMode.collect {
                    UiPreferences.NightMode.asIndex(it)?.let { index ->
                        val nightModeArray =
                            resources.getStringArray(R.array.pref_night_mode_labels)
                        nightModePreference?.summary = nightModeArray.getOrNull(index)
                    }
                }
            }

            launch {
                viewModel.leftHandedMode.collect { leftHandedMode ->
                    leftHandedModePreference?.isChecked = !leftHandedMode
                }
            }

            launch {
                viewModel.showNsfw.collect { showNsfw ->
                    showNsfwPreference?.isChecked = showNsfw
                    showNsfwPreviewPreference?.isEnabled = showNsfw
                }
            }

            launch {
                viewModel.showNsfwPreview.collect { showNsfwPreview ->
                    showNsfwPreviewPreference?.isChecked = showNsfwPreview
                }
            }

            launch {
                viewModel.showSpoilerPreview.collect { showSpoilerPreview ->
                    showSpoilerPreviewPreference?.isChecked = showSpoilerPreview
                }
            }

            launch {
                viewModel.redditSource.collect { value ->
                    DataPreferences.RedditSource.fromValue(value.first).let {
                        val summary = when (it) {
                            REDDIT -> getString(R.string.preference_reddit_source_reddit)
                            TEDDIT -> {
                                String.format(
                                    "%s - %s",
                                    getString(R.string.preference_reddit_source_teddit),
                                    value.second
                                )
                            }
                        }
                        sourcePreference?.summary = summary
                    }
                }
            }

            launch {
                viewModel.privacyEnhancerEnabled.collect { enabled ->
                    privacyEnhancerPreference?.summary = if (enabled) {
                        getString(R.string.preference_privacy_enhancer_enabled)
                    } else {
                        getString(R.string.preference_privacy_enhancer_disabled)
                    }
                }
            }
        }
    }

    private fun showNightModeDialog(checkedItem: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_night_mode_title)
            .setSingleChoiceItems(R.array.pref_night_mode_labels, checkedItem) { dialog, which ->
                UiPreferences.NightMode.asMode(which)?.let { mode ->
                    updateNightMode(mode)
                    dialog.dismiss()
                }
            }
            .show()
    }

    private fun updateNightMode(mode: Int) {
        unredditApplication?.appTheme = mode
        activity?.recreate() // Recreate activity to force the change between dark and amoled
        viewModel.setNightMode(mode)
    }

    private fun showRedditSourceDialog(source: Int, instance: String) {
        RedditSourceDialogFragment.show(
            childFragmentManager,
            DataPreferences.RedditSource.fromValue(source),
            instance,
            viewModel.tedditInstances
        )
    }

    private fun showRedditSourceDisclaimer(
        source: DataPreferences.RedditSource,
        instance: String?
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_reddit_source_disclaimer_title)
            .setMessage(R.string.dialog_reddit_source_disclaimer_body)
            .setPositiveButton(R.string.ok) { _, _ ->
                // Ignore
            }
            .setNeutralButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
            .apply {
                getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    updateRedditSource(source.value, instance)
                    showRestartRequiredDialog()
                    dismiss()
                }
            }
    }

    private fun showRestartRequiredDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_restart_required_title)
            .setMessage(R.string.dialog_restart_required_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                // Ignore
            }
            .setNeutralButton(R.string.dialog_restart_required_later) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
            .apply {
                getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    requireContext().restart()
                    dismiss()
                }
            }
    }

    private fun updateRedditSource(source: Int, instance: String? = null) {
        viewModel.setRedditSource(source, instance)
    }

    private fun openBackup() {
        findNavController().navigate(PreferencesFragmentDirections.openBackup(), navOptions)
    }

    private fun openAbout() {
        findNavController().navigate(PreferencesFragmentDirections.openAbout(), navOptions)
    }

    private fun openPrivacyEnhancer() {
        findNavController().navigate(
            PreferencesFragmentDirections.openPrivacyEnhancer(),
            navOptions
        )
    }

    override fun onStop() {
        super.onStop()
        childFragmentManager
            .clearFragmentResultListener(RedditSourceDialogFragment.REQUEST_KEY_SOURCE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PreferencesFragment"
    }
}
