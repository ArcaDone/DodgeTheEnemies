package com.arcadan.dodgetheenemies.data

import android.util.Log
import com.arcadan.dodgetheenemies.data.models.User
import com.arcadan.util.BuildConfig
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DatabaseManager {
    companion object {
        fun setValue(key: String, value: Any) {
            val userPath = checkIfDebugUser(key)
            val ref = Firebase.database.getReference(userPath)
            ref.setValue(value)
        }

        private fun checkIfDebugUser(key: String) =
            if (BuildConfig.DEBUG)
                "debugUsers/${Persistence.instance.getInt(Keys.USER_ID)}/$key"
            else
                "users/${Persistence.instance.getInt(Keys.USER_ID)}/$key"

        fun addUser(userId: Int, user: User) {
            val ref = Firebase.database.reference
            if (BuildConfig.DEBUG)
                ref.child("debugUsers").child("$userId").setValue(user)
            else
                ref.child("users").child("$userId").setValue(user)
            DataManager.instance.setUserSilent(user)
        }

        suspend fun getUserMapValue(key: String): HashMap<String, Any?> {
            return suspendCoroutine {
                val ref = Firebase.database.getReference(key)
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var value = dataSnapshot.getValue<HashMap<String, Any?>>()
                        LogHelper.log(TAG, "Value is: $value")
                        if (value == null) {
                            value = hashMapOf()
                        }
                        it.resume(value)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        LogHelper.log(TAG, "Failed to read value $key", Log.ERROR)
                        it.resume(hashMapOf())
                    }
                })
            }
        }
    }
}
