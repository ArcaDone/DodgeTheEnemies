package com.arcadan.dodgetheenemies.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arcadan.dodgetheenemies.adapters.InventoryAdapter
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.databinding.FragmentInventoryBinding
import com.arcadan.dodgetheenemies.interfaces.BottomSheetCallBack
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.InventoryViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import kotlinx.android.synthetic.main.top_bar.coins_value
import kotlinx.android.synthetic.main.top_bar.gem_value
import kotlinx.android.synthetic.main.top_bar.heart_value
import kotlinx.android.synthetic.main.top_bar.item_coins_veil_layout
import kotlinx.android.synthetic.main.top_bar.item_gem_veil_layout
import kotlinx.android.synthetic.main.top_bar.item_heart_veil_layout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryFragment : Fragment(), BottomSheetCallBack {

    private lateinit var viewModel: InventoryViewModel

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        return FragmentInventoryBinding.inflate(inflater).apply {
            invViewModel = viewModel
            lifecycleOwner = this@InventoryFragment

            inventoryRecycler.adapter = InventoryAdapter(InventoryAdapter.OnClickListener {
                viewModel.selectedItem.value = it
            })
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindMusic()
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            item_gem_veil_layout?.unVeil()
            item_coins_veil_layout?.unVeil()
            item_heart_veil_layout?.unVeil()
            gem_value?.text = DataManager.instance.user.value!!.gems.toString()
            coins_value?.text = DataManager.instance.user.value!!.coins.toString()
            heart_value?.text = DataManager.instance.user.value!!.hearts.toString()
        }
        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }

    override fun giveHeartsReward() {
        // Do nothing
    }

    override fun adsActionStarted() {
        // Do nothing
    }
}
