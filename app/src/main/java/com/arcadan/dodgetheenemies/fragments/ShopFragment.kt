package com.arcadan.dodgetheenemies.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.arcadan.dodgetheenemies.BuildConfig
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.adapters.ShopAdapter
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentShopBinding
import com.arcadan.dodgetheenemies.enums.AnimationImage
import com.arcadan.dodgetheenemies.enums.Gem
import com.arcadan.dodgetheenemies.enums.Heart
import com.arcadan.dodgetheenemies.enums.PriceUnit
import com.arcadan.dodgetheenemies.enums.Reward
import com.arcadan.dodgetheenemies.enums.SkinEnum
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.scaling
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.shower
import com.arcadan.dodgetheenemies.models.Shop
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.pauseMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.ShopViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_shop.coin_anim
import kotlinx.android.synthetic.main.fragment_shop.gems_anim
import kotlinx.android.synthetic.main.fragment_shop.hearts_anim
import kotlinx.android.synthetic.main.fragment_shop.number_animation
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

class ShopFragment : Fragment(), PurchasesUpdatedListener {

    private lateinit var viewModel: ShopViewModel
    private lateinit var rewardedAd: RewardedAd
    private lateinit var billingClient: BillingClient

    private var skuDetailsList: MutableList<SkuDetails> = mutableListOf()
    private var areAdsLoaded: Boolean = true
    private val skuList = listOf(
        Gem.LITTLE_GEMS.sku,
        Gem.MEDIUM_GEMS.sku,
        Gem.LARGE_GEMS.sku,
        Gem.LITTLE_CHEST_GEMS.sku,
        Gem.MEDIUM_CHEST_GEMS.sku,
        Gem.LARGE_CHEST_GEMS.sku
    )

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ShopViewModel::class.java)

        return FragmentShopBinding.inflate(inflater).apply {
            shopViewModel = viewModel
            lifecycleOwner = this@ShopFragment

            val manager = GridLayoutManager(activity, 4, GridLayoutManager.HORIZONTAL, false)
            shopListRecycler.layoutManager = manager
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = 4
            }

            val adapter = ShopAdapter(ShopAdapter.OnClickListener {
                if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
                    shopElementClicked(it)
                } else {
                    Snackbar.make(
                        requireView(),
                        Dodgers.context!!.getString(R.string.check_internet_connection),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
            shopListRecycler.adapter = adapter
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

        setupBillingClient()

        // RewardedAd
        rewardedAd = RewardedAd(
            requireContext(),
            BuildConfig.rewardVideoAds
        )

        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                LogHelper.log(TAG, "onRewardedAdLoaded", Log.VERBOSE)
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                LogHelper.log(TAG, "onRewardedAdFailedToLoad", Log.VERBOSE)
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)

        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Memory leak
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                acknowledgePurchase(purchase, purchase.sku)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e("billing", "error")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            if (purchases != null) {
                for (purchase in purchases) {
                    consumeItemPurchased(purchase)
                }
            }
        } else {
            // Handle any other error codes.
            Log.e("billing", "error")
        }
    }

    private fun startAdsAction(reward: Reward) {

        if (rewardedAd.isLoaded) {
            pauseMusic()
            val adCallback = object : RewardedAdCallback() {
                override fun onRewardedAdOpened() {
                    LogHelper.log(TAG, "onRewardedAdOpened", Log.VERBOSE)
                }

                override fun onRewardedAdClosed() {
                    giveTheReward(reward)
                }

                override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                    LogHelper.log(TAG, "onUserEarnedReward", Log.VERBOSE)
                }

                override fun onRewardedAdFailedToShow(errorCode: Int) {
                    LogHelper.log(TAG, "onRewardedAdFailedToShow", Log.VERBOSE)
                }
            }
            rewardedAd.show(requireActivity(), adCallback)
        } else {
            LogHelper.log(TAG, "not loaded", Log.VERBOSE)
            areAdsLoaded = false
            Toast.makeText(
                requireContext(),
                "Wait a moment, we are loading your video :-)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun giveTheReward(reward: Reward) {
        LogHelper.log(TAG, "onRewardedAdClosed", Log.VERBOSE)
        resumeMusic()
        when (reward) {
            Reward.COINS -> {
                addNewCoins()
            }
            Reward.HEARTS -> {
                addNewHearts()
            }
            Reward.POWER_UP -> Toast.makeText(
                Dodgers.context!!,
                "NOt Implemented",
                Toast.LENGTH_SHORT
            ).show()
            Reward.NONE -> Toast.makeText(Dodgers.context!!, "NOt Implemented", Toast.LENGTH_SHORT)
                .show()
        }
        areAdsLoaded = true
        doAnimation(reward)
    }

    private fun addNewCoins() {
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            DataManager.instance.user.value!!.coins += GameParameters.instance.oneHundredCoinsReward
            coins_value.text = DataManager.instance.user.value!!.coins.toString()
        }
    }

    private fun addNewHearts() {
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            if (DataManager.instance.user.value!!.heartsCount.counter == 0) {
                DataManager.instance.user.value!!.heartsCount.daysCount =
                    System.currentTimeMillis() / 1000L
            }
            DataManager.instance.user.value!!.hearts += GameParameters.instance.twentyHeartsReward
            DataManager.instance.user.value!!.heartsCount.counter += 1

            heart_value.text = DataManager.instance.user.value!!.hearts.toString()
        }
    }

    private fun shopElementClicked(shop: Shop) {
        when (shop.name) {
            Heart.TEN.name -> watchVideo()
            Gem.LITTLE_GEMS.name,
            Gem.MEDIUM_GEMS.name,
            Gem.LARGE_GEMS.name,
            Gem.LITTLE_CHEST_GEMS.name,
            Gem.MEDIUM_CHEST_GEMS.name,
            Gem.LARGE_CHEST_GEMS.name -> {
                startBillingAction(shop)
            }
            SkinEnum.SKIN_FIRST.name -> Snackbar.make(
                requireView(),
                getString(R.string.coming_soon),
                Snackbar.LENGTH_SHORT
            ).show()
            SkinEnum.SKIN_SECOND.name -> Snackbar.make(
                requireView(),
                getString(R.string.coming_soon),
                Snackbar.LENGTH_SHORT
            ).show()
            else -> buyShopItem(shop)
        }
    }

    private fun startBillingAction(shop: Shop) {
        if (skuDetailsList.isNotEmpty()) {
            val skuDetails = skuDetailsList.first { it.sku == shop.sku }
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
        } else {
            showSnackBar(PriceUnit.NONE)
        }
    }

    private fun watchVideo() {
        if (DataManager.instance.user.value != null && DataManager.instance.user.value!!.heartsCount.counter < 25) {
            startAdsAction(Reward.HEARTS)
        } else {
            Snackbar.make(requireView(), R.string.day_video_limits, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun buyShopItem(shop: Shop) {
        if (checkAndUpdateResources(shop.price, shop.unit)) {
            updateRewards(shop.reward, shop.rewardType, shop.name)
            updateValueTopBar()
            doAnimation(shop.rewardType)
        } else {
            showSnackBar(shop.unit)
        }
    }

    private fun checkAndUpdateResources(price: Int, unit: PriceUnit): Boolean {
        return when (unit) {
            PriceUnit.GEMS -> {
                if (DataManager.instance.user.value!!.gems >= price) {
                    DataManager.instance.user.value!!.gems -= price
                    true
                } else false
            }
            PriceUnit.COINS -> {
                val coinsVal = DataManager.instance.user.value!!.coins
                if (coinsVal >= price) {
                    DataManager.instance.user.value!!.coins -= price
                    true
                } else false
            }
            PriceUnit.MONEY -> false
            else -> false
        }
    }

    private fun updateRewards(reward: Int, rewardType: Reward, name: String) {
        when (rewardType) {
            Reward.COINS -> DataManager.instance.user.value!!.coins += reward
            Reward.HEARTS -> DataManager.instance.user.value!!.hearts += reward
            Reward.POWER_UP -> DataManager.instance.user.value!!.consumables.find { it.name == name }!!.quantity += 1
            Reward.NONE -> DataManager.instance.user.value!!.coins += 0 // not implemented cause crash
            Reward.GEMS -> DataManager.instance.user.value!!.coins += 0 // not implemented cause crash
        }
    }

    private fun updateValueTopBar() {
        heart_value.text = DataManager.instance.user.value!!.hearts.toString()
        gem_value.text = DataManager.instance.user.value!!.gems.toString()
        coins_value.text = DataManager.instance.user.value!!.coins.toString()
    }

    private fun doAnimation(rewardType: Reward) {
        when (rewardType) {
            Reward.HEARTS -> shower(hearts_anim, AnimationImage.HEARTS)
            Reward.COINS -> shower(coin_anim, AnimationImage.COIN)
            Reward.GEMS -> shower(gems_anim, AnimationImage.GEMS)
            Reward.POWER_UP -> scaling(
                number_animation,
                actionFromDisabledToEnabled = true,
                actionFromHideToVisible = true
            )
            else -> shower(gems_anim, AnimationImage.GEMS)
        }
    }

    private fun showSnackBar(unit: PriceUnit) {
        val bodyText = when (unit) {
            PriceUnit.COINS -> R.string.not_enough_coins
            PriceUnit.GEMS -> R.string.not_enough_gems
            PriceUnit.MONEY -> R.string.error
            else -> R.string.try_later
        }

        Snackbar.make(requireView(), bodyText, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(Dodgers.context!!)
            .enablePendingPurchases()
            .setListener(this@ShopFragment)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is setup successfully
                    loadAllSKUs()
                } else {
                    skuDetailsList = mutableListOf()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("billing", "error")
            }
        })
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                this.skuDetailsList = skuDetailsList
            } else this.skuDetailsList = mutableListOf()
        }
    } else {
        println("Billing Client not ready")
    }

    private fun acknowledgePurchase(purchase: Purchase, sku: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage

            CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
                val gemItem = Gem.values().find { it.sku == sku }
                DataManager.instance.user.value!!.gems += gemItem!!.reward
                gem_value.text = DataManager.instance.user.value!!.gems.toString()
            }

            consumeItemPurchased(purchase)
        }
    }

    private fun consumeItemPurchased(purchase: Purchase) {
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
                LogHelper.log(TAG, "Item Consumed!!!")
            }
        }
    }
}
