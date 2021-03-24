package com.arcadan.dodgetheenemies.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.arcadan.dodgetheenemies.adapters.SkinAdapter
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentSkinsBinding
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.SkinsViewModel

class SkinsFragment : Fragment() {

    private lateinit var viewModel: SkinsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SkinsViewModel::class.java)

        return FragmentSkinsBinding.inflate(inflater).apply {
            skinsViewModel = viewModel
            lifecycleOwner = this@SkinsFragment

            val manager = GridLayoutManager(activity, 4)
            skinListRecycler.layoutManager = manager
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = 1
            }

            val adapter = SkinAdapter(SkinAdapter.OnClickListener {
                Persistence.instance.saveInt(Keys.SELECTED_SKIN, it.id)
            })
            skinListRecycler.adapter = adapter

            buttondown.setOnClickListener {
                skinListRecycler.scrollToPosition(adapter.itemCount - 1)
            }
            buttonTop.setOnClickListener {
                skinListRecycler.scrollToPosition(0)
            }
        }.root
    }

    @SuppressLint("SetTextI18n")
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
