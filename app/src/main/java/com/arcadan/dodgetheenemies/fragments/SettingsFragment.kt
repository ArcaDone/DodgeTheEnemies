package com.arcadan.dodgetheenemies.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentSettingsBinding
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.util.ViewUtils.Companion.showBottomSheet
import com.arcadan.dodgetheenemies.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.bottom_sheet_pp.ppLayoutBottomSheet
import kotlinx.android.synthetic.main.bottom_sheet_tos.tosLayoutBottomSheet
import kotlinx.android.synthetic.main.fragment_settings.player_id
import kotlinx.android.synthetic.main.fragment_settings.version_app

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        return FragmentSettingsBinding.inflate(inflater).apply {
            settingsViewModel = viewModel
            lifecycleOwner = this@SettingsFragment

            termsTitle.setOnClickListener {
                showBottomSheet(tosLayoutBottomSheet)
            }
            policyTitle.setOnClickListener {
                showBottomSheet(ppLayoutBottomSheet)
            }
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()

        player_id.text = getString(R.string.player_id) + " " + Persistence.instance.getInt(Keys.USER_ID)
        version_app.text = getVersionApp()

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    private fun getVersionApp(): CharSequence? {
        return "App Version: ${BuildConfig.VERSION_NAME}"
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }
}
