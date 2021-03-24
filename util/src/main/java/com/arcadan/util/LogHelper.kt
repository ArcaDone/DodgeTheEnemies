package com.arcadan.util

import android.util.Log
import androidx.annotation.IntDef

class LogHelper {
    companion object {
        private const val PREFIX : String = "Dodge"

        fun log(tag: String, message: String?, @LogLevels level: Int = Log.DEBUG, forceLogInRelease: Boolean = false) {
            if(BuildConfig.DEBUG || forceLogInRelease) {
                when(level) {
                    Log.ERROR -> {
                        Log.e("${PREFIX}__${tag}", message!!)
                    }
                    Log.WARN -> {
                        Log.w("${PREFIX}__${tag}", message!!)
                    }
                    Log.DEBUG -> {
                        Log.d("${PREFIX}__${tag}", message!!)
                    }
                    Log.INFO -> {
                        Log.i("${PREFIX}__${tag}", message!!)
                    }
                    Log.VERBOSE -> {
                        Log.v("${PREFIX}__${tag}", message!!)
                    }
                }
            }
        }
    }
}

@IntDef(Log.ERROR, Log.WARN, Log.DEBUG, Log.INFO, Log.VERBOSE)
@Retention(AnnotationRetention.SOURCE)
annotation class LogLevels