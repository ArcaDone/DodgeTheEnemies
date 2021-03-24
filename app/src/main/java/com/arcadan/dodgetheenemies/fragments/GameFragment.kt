package com.arcadan.dodgetheenemies.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.FragmentGameBinding
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.game.GameCallBacks
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.graphics.CountDownTimer
import com.arcadan.dodgetheenemies.graphics.GameEngineContext
import com.arcadan.dodgetheenemies.graphics.ObjectsAnimation.Companion.blink
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.util.ViewUtils
import com.arcadan.dodgetheenemies.viewmodel.GameViewModel
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.gson.Gson
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.fragment_game.avoided_enemies_text
import kotlinx.android.synthetic.main.fragment_game.background_evening
import kotlinx.android.synthetic.main.fragment_game.background_work_around
import kotlinx.android.synthetic.main.fragment_game.coins_double_image
import kotlinx.android.synthetic.main.fragment_game.coins_game_text
import kotlinx.android.synthetic.main.fragment_game.frame_speed_down
import kotlinx.android.synthetic.main.fragment_game.game_view_surface
import kotlinx.android.synthetic.main.fragment_game.go_to_tome
import kotlinx.android.synthetic.main.fragment_game.level_progress_bar
import kotlinx.android.synthetic.main.fragment_game.speed_down_text
import kotlinx.android.synthetic.main.fragment_game.text_pause
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment(), GameCallBacks, CountDownTimer.OnCountDownListener,
    GameEngineContext {

    private var countDownTimer: CountDownTimer? = null
    private var progressBarStatus = 10
    private var mediaPlayer: MediaPlayer? = null
    private var pickedUpAPowerDown = false
    private var pickedUpAReverseMovement = false
    private var pickedUpASlurpBlue = false
    private var pickedUpAEvening = false
    private lateinit var progressBar: ProgressBar
    private lateinit var level: Level
    private lateinit var gameEngine: GameEngine
    private lateinit var viewModel: GameViewModel
    private lateinit var joystick: JoystickView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        level = Gson().fromJson(arguments?.getString("model") as String, Level::class.java)
        return FragmentGameBinding.inflate(inflater).apply {
            gameViewModel = viewModel
            lifecycleOwner = this@GameFragment
            goToTome.setOnClickListener {
                Persistence.instance.saveInt(Keys.SELECTED_LEVEL, 0)
                GameParameters.instance.backpack.clear()
                GameParameters.instance.jumpGravity = 1
                if (GameParameters.instance.backpack.contains(PowerUp.MEGA_JUMP)) {
                    GameParameters.instance.jumpEquationC = GameParameters.instance.megaJumpValue
                }
                GameParameters.instance.gameRunning = false
                GameParameters.instance.shouldStopSpawn = true
                requireView().findNavController().navigate(R.id.mainFragment)
            }
            iconPause.setOnClickListener {
                if (GameParameters.instance.gameRunning) {
                    pauseState()
                    text_pause.visibility = View.VISIBLE
                    go_to_tome.visibility = View.VISIBLE
                    background_work_around.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.color.transparent50
                        )
                } else {
                    resumeState()
                    text_pause.visibility = View.INVISIBLE
                    go_to_tome.visibility = View.INVISIBLE
                    background_work_around.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.color.transparent100
                        )
                }
            }
            gameViewSurface.level = level
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game_view_surface.callBacks = this
        game_view_surface.callBGE = this
        progressBar = level_progress_bar

        if (GameParameters.instance.hasWon)
            GameParameters.instance.hasWon = false

        progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)

        // Set Parameters
        progressBar.max = level.levelDuration
        progressBarStatus = level.levelDuration

        countDownTimer = CountDownTimer(
            fromSeconds = level.levelDuration.toLong(),
            onCountDownListener = this@GameFragment
        )

        countDownTimer?.start()
        LogHelper.log(TAG, "countdown  =>>>>> ${level.levelDuration.toLong()}")
        ViewUtils.performBackClick(viewLifecycleOwner, requireActivity(), requireView())

        if (Persistence.instance.getBool(Keys.MUSIC_STATE)) {
            mediaPlayer = MediaPlayer.create(
                requireContext(),
                R.raw.soundtrack
            )
            if (mediaPlayer != null) {
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.setVolume(50f, 50f)
            }
            mediaPlayer!!.start()
        }

        joystick = requireView().findViewById(R.id.jID) as JoystickView
        joystick.setOnMoveListener({ angle, strength ->
            if (GameParameters.instance.gameRunning) {
                if (strength > 5 && angle in 0..89 || angle in 271..359) {
                    LogHelper.log(TAG, "RIGHT")
                    gameEngine.movePlayerRight()
                } else if (strength > 5) {
                    LogHelper.log(TAG, "LEFT")
                    gameEngine.movePlayerLeft()
                } else {
                    LogHelper.log(TAG, "stop")
                    gameEngine.player.stopAnimation()
                }
            }
        }, 17)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null) mediaPlayer!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) mediaPlayer!!.stop()
    }

    override fun onGameOver(coins: Int, score: Int, toJson: String) {
        countDownTimer?.pause()
        countDownTimer = null
        val bundle = Bundle()
        bundle.putInt("coins", coins)
        bundle.putInt("score", score)
        bundle.putString("model", toJson)
        requireView().findNavController().navigate(R.id.gameOverFragment, bundle)
        this@GameFragment.onDestroy()
    }

    override fun onGameWon(coins: Int, score: Int, toJson: String) {
        countDownTimer?.pause()
        countDownTimer = null
        val bundle = Bundle()
        bundle.putInt("coins", coins)
        bundle.putInt("score", score)
        bundle.putInt("currentGem", DataManager.instance.user.value!!.gems)
        bundle.putString("model", toJson)
        requireView().findNavController().navigate(R.id.gameWonFragment, bundle)
        this@GameFragment.onDestroy()
    }

    override fun onCoinsChanged(coins: Int) {
        requireActivity().runOnUiThread {
            if (coins_game_text != null)
                coins_game_text.text = coins.toString()
        }
    }

    override fun onScoreChanged(score: Int) {
        requireActivity().runOnUiThread {
            avoided_enemies_text.text = score.toString()
        }
    }

    override fun onEveningHit() {
        if (!pickedUpAEvening) {
            pickedUpAEvening = true
            CoroutineScope(Dispatchers.Main).launch {
                background_evening.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.color.transparent80
                    )
                pauseState()
                speed_down_text.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_clouds
                    )
                )
                speed_down_text.text = resources.getString(R.string.evening)
                frame_speed_down.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.frame_white)
                blink(speed_down_text)
                blink(frame_speed_down)
                delay(1000)
                resumeState()
            }
        }
    }

    override fun onSpeedDownHit() {
        if (!pickedUpAPowerDown) {
            pickedUpAPowerDown = true
            CoroutineScope(Dispatchers.Main).launch {
                pauseState()
                speed_down_text.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGreen2
                    )
                )
                speed_down_text.text = resources.getString(R.string.speed_down)
                frame_speed_down.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.frame_green)
                blink(speed_down_text)
                blink(frame_speed_down)
                delay(1000)
                resumeState()
            }
        }
    }

    override fun onReverseMovementHit() {
        if (!pickedUpAReverseMovement) {
            pickedUpAReverseMovement = true
            CoroutineScope(Dispatchers.Main).launch {
                pauseState()
                speed_down_text.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    )
                )
                speed_down_text.text = resources.getString(R.string.reverse_string)
                frame_speed_down.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.frame_red)
                blink(speed_down_text)
                blink(frame_speed_down)
                delay(1000)
                resumeState()
            }
        }
    }

    override fun onSlurpBlueHit() {
        if (!pickedUpASlurpBlue) {
            pickedUpASlurpBlue = true
            CoroutineScope(Dispatchers.Main).launch {
                pauseState()
                speed_down_text.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_sun_flower
                    )
                )
                speed_down_text.text = resources.getString(R.string.slurp_blue)
                frame_speed_down.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.frame_yellow)
                blink(speed_down_text)
                blink(frame_speed_down)
                delay(1000)
                resumeState()
            }
        }
    }

    private fun resumeState() {
        GameParameters.instance.gameRunning = true
        countDownTimer?.start(true)
        countDownTimer?.runOnBackgroundThread()
    }

    private fun pauseState() {
        GameParameters.instance.gameRunning = false
        countDownTimer?.pause()
        countDownTimer?.setTimerPattern("s")
    }

    override fun setDoubleCoinImage() {
        coins_double_image.visibility = View.VISIBLE
    }

    override fun onCountDownActive(time: Int) {
        progressBarStatus = time
        LogHelper.log(TAG, "countdown tick  =>>>>> $progressBarStatus")
        if (progressBarStatus == level.levelDuration / 2)
            progressBar.progressTintList = ColorStateList.valueOf(Color.YELLOW)

        if (progressBarStatus == level.levelDuration / 4)
            progressBar.progressTintList = ColorStateList.valueOf(Color.RED)

        requireActivity().runOnUiThread {
            progressBar.progress = progressBarStatus
        }
    }

    override fun onCountDownFinished() {
        progressBar.progress = 0
        GameParameters.instance.hasWon = true
    }

    override fun gameEngineCtx(gameEngine: GameEngine) {
        this.gameEngine = gameEngine
    }
}
