package com.example.guru2_dsjouju_app

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Siren_running : AppCompatActivity() {

    private var countdownTimer: CountDownTimer? = null

    private lateinit var sosManager: SosManager
    private lateinit var sosButton: ImageButton

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isButtonPressed = false

    private lateinit var loginID: String

    private lateinit var audioManager: AudioManager
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siren_running)

        // 로그인 ID를 인텐트에서 추출하여 초기화
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE)

        sosButton = findViewById(R.id.sosButton)

        sosManager = SosManager(this, sosButton, loginID)

        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }

        // 오디오 매니저 초기화
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        showSirenDialog()

        // 사이렌 시작 버튼 초기화
        val toggleSirenButton: ImageButton = findViewById(R.id.toggleSirenButton)
        toggleSirenButton.setOnClickListener {
            if (mediaPlayer == null) {
                // 사이렌 소리 시작
                startSiren()
            } else {
                Toast.makeText(this, "사이렌 소리가 이미 시작되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showSirenDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("사이렌을 시작하려면 확인 버튼을 누르세요.")
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                countdownTimer?.cancel()
                startSiren()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                countdownTimer?.cancel()
                stopSirenAndReturnToPrevious()
            }

        val alert = builder.create()
        alert.show()

        countdownTimer = object : CountDownTimer(3300, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                alert.setMessage("3초 후 사이렌이 실행됩니다.\n자동 시작까지 ${millisUntilFinished / 1000}초 남았습니다.")
            }

            override fun onFinish() {
                alert.dismiss()
                startSiren()
            }
        }.start()
    }

    private fun startSiren() {
        val selectedSirenId = sharedPreferences.getInt("selected_siren", R.id.radio_siren1)
        val soundResId = when (selectedSirenId) {
            R.id.radio_siren1 -> R.raw.police_siren
            R.id.radio_siren2 -> R.raw.civil_defense_siren
            R.id.radio_siren3 -> R.raw.ambulance_siren
            else -> R.raw.police_siren  // 기본 사이렌으로 경찰 사이렌 사용
        }



        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = (MediaPlayer.create(this, soundResId)?.apply {
                setVolume(1.0f, 1.0f)
                isLooping = true
                start()
            } ?: run {
                Toast.makeText(this, "사이렌 소리 초기화 실패", Toast.LENGTH_SHORT).show()
            }) as MediaPlayer?
        } else {
            Toast.makeText(this, "오디오 포커스를 얻지 못했습니다.", Toast.LENGTH_SHORT).show()
        }

        val togglesirenButton: ImageButton = findViewById(R.id.toggleSirenButton)
        togglesirenButton.setOnLongClickListener {
            isButtonPressed = true
            handler.postDelayed({
                if (isButtonPressed) {
                    stopSirenAndReturnToPrevious()
                }
            }, 1000)
            true
        }

        togglesirenButton.setOnClickListener {
            isButtonPressed = false
            Toast.makeText(this, "사이렌 중지를 위해 길게 눌러주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSirenAndReturnToPrevious() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null

        // 오디오 포커스 해제
        audioManager.abandonAudioFocus(audioFocusChangeListener)

        finish()
    }
}

