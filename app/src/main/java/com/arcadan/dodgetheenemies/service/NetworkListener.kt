package com.arcadan.dodgetheenemies.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Parcelable

object NetworkListener {

    fun create(onNetworkUp: () -> Unit, onNetworkDown: () -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                intent.extras?.getParcelable<Parcelable>("networkInfo")?.let {
                    val info = it as NetworkInfo
                    val state: NetworkInfo.State = info.state
                    if (state === NetworkInfo.State.CONNECTED) {
                        onNetworkUp()
                    } else {
                        onNetworkDown()
                    }
                }
            }
        }
    }

    fun register(context: Context, broadcastReceiver: BroadcastReceiver) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unregister(context: Context, broadcastReceiver: BroadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver)
    }
}
