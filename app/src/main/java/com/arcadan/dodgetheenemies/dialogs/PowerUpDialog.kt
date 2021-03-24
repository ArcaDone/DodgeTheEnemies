package com.arcadan.dodgetheenemies.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.util.TAG
import java.lang.Exception

class PowerUpDialog : DialogFragment() {

    // Use this instance of the interface to deliver action events
    internal lateinit var listener: PowerUpDialogListener
    internal var currentLevel: Int = 0

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface PowerUpDialogListener {
        fun onDialogPlayClick(dialog: DialogFragment)
        fun onDialogDiscardClick(dialog: DialogFragment)
        fun onDialogSlurpRedClick(dialog: DialogFragment)
        fun onDialogSpeedUpClick(dialog: DialogFragment)
        fun onDialogMegaJumpClick(dialog: DialogFragment)
        fun onDialogDoubleCoinsClick(dialog: DialogFragment)
    }

    companion object {
        fun newInstance(listener: PowerUpDialogListener, levelID: Int): PowerUpDialog {
            return PowerUpDialog().apply {
                this.listener = listener
                this.currentLevel = levelID
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_power_up, container, false)

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        val levelID = currentLevel + 1 // show level id text
        val levelSelected = view.findViewById<TextView>(R.id.level_selected)
        levelSelected.text = Dodgers.context!!.getString(R.string.selected_level_title, levelID)

        val playButton = view.findViewById<Button>(R.id.play_button_dialog)
        playButton.setOnClickListener {
            try {
                listener.onDialogPlayClick(this)
                dialog?.dismiss()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        val discardButton = view.findViewById<Button>(R.id.discard_button)
        discardButton.setOnClickListener {
            try {
                listener.onDialogDiscardClick(this)
                dialog?.dismiss()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        val slurpRedButton = view.findViewById<ImageView>(R.id.slurp_red_item)
        slurpRedButton.setOnClickListener {
            if (DataManager.instance.user.value!!.consumables[PowerUp.SLURP_RED.ordinal].quantity >= 1) {
                if (slurpRedButton.backgroundTintList == ContextCompat.getColorStateList(
                        Dodgers.context!!,
                        R.color.colorGrey
                    )
                ) {
                    slurpRedButton.backgroundTintList = ContextCompat.getColorStateList(
                        Dodgers.context!!,
                        R.color.joystickLightBlue
                    )
                } else {
                    slurpRedButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.colorGrey)
                }
            } else {
                slurpRedButton.backgroundTintList =
                    ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickLightBlue)
            }
            try {
                listener.onDialogSlurpRedClick(this)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        val speedUpButton = view.findViewById<ImageView>(R.id.speed_up_item)
        speedUpButton.setOnClickListener {
            if (DataManager.instance.user.value!!.consumables[PowerUp.SPEED_UP.ordinal].quantity >= 1) {
                if (speedUpButton.backgroundTintList == ContextCompat.getColorStateList(
                        Dodgers.context!!,
                        R.color.colorGrey
                    )
                ) {
                    speedUpButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickPink)
                } else {
                    speedUpButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.colorGrey)
                }
            } else {
                speedUpButton.backgroundTintList =
                    ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickPink)
            }
            try {
                listener.onDialogSpeedUpClick(this)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        val megaJumpButton = view.findViewById<ImageView>(R.id.mega_jump_item)
        megaJumpButton.setOnClickListener {
            if (DataManager.instance.user.value!!.consumables[PowerUp.MEGA_JUMP.ordinal].quantity >= 1) {
                if (megaJumpButton.backgroundTintList == ContextCompat.getColorStateList(
                        Dodgers.context!!,
                        R.color.colorGrey
                    )
                ) {
                    megaJumpButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickYellow)
                } else {
                    megaJumpButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.colorGrey)
                }
            } else {
                megaJumpButton.backgroundTintList =
                    ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickYellow)
            }
            try {
                listener.onDialogMegaJumpClick(this)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        val doubleCoinsButton = view.findViewById<ImageView>(R.id.double_coin_item)
        doubleCoinsButton.setOnClickListener {
            if (DataManager.instance.user.value!!.consumables[PowerUp.DOUBLE_COINS.ordinal].quantity >= 1) {
                if (doubleCoinsButton.backgroundTintList == ContextCompat.getColorStateList(
                        Dodgers.context!!,
                        R.color.colorGrey
                    )
                ) {
                    doubleCoinsButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickGreen)
                } else {
                    doubleCoinsButton.backgroundTintList =
                        ContextCompat.getColorStateList(Dodgers.context!!, R.color.colorGrey)
                }
            } else {
                doubleCoinsButton.backgroundTintList =
                    ContextCompat.getColorStateList(Dodgers.context!!, R.color.joystickGreen)
            }
            try {
                listener.onDialogDoubleCoinsClick(this)
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }

        return view
    }
}
