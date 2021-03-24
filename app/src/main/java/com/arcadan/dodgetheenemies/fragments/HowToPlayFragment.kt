package com.arcadan.dodgetheenemies.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.databinding.FragmentHowtoplayBinding
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.HowToPlayViewModel

class HowToPlayFragment : Fragment() {

    private lateinit var viewModel: HowToPlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HowToPlayViewModel::class.java)

        return FragmentHowtoplayBinding.inflate(inflater).apply {
            howToPlayViewModel = viewModel
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }
}
