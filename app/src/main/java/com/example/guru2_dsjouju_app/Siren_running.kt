package com.example.guru2_dsjouju_app

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siren_running)

        // 로그인 ID를 인텐트에서 추출
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        sosButton = findViewById(R.id.sosButton)

        sosManager = SosManager(this, sosButton, loginID)

        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }

        showSirenDialog()
    }

    fun showSirenDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("3초 후 사이렌이 실행됩니다.")
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                startSiren()
                countdownTimer?.cancel()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                countdownTimer?.cancel()
                stopSirenAndReturnToPrevious()
            }

        val alert = builder.create()
        alert.show()

        countdownTimer = object : CountDownTimer(3500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                alert.setMessage("3초 후 사이렌이 실행됩니다.\n자동 시작까지 ${millisUntilFinished / 1000}초 남았습니다.")
            }

            override fun onFinish() {
                startSiren()
                alert.dismiss()
            }
        }.start()
    }

    private fun startSiren() {
        val sirenType = intent.getStringExtra("siren_type")
        val soundResId = when (sirenType) {
            "siren_police" -> R.raw.police_siren
            "siren_fire" -> R.raw.fire_trucks_siren
            "siren_ambulance" -> R.raw.ambulance_siren
            else -> null
        }

        soundResId?.let {
            mediaPlayer = MediaPlayer.create(this, it).apply {
                // 소리를 최대 볼륨으로 설정
                setVolume(1.0f, 1.0f)
                // 사이렌 소리가 반복되도록 설정
                isLooping = true
                start()
            }
        }

        val togglesirenButton: ImageButton = findViewById(R.id.toggleSirenButton)
        togglesirenButton.setOnLongClickListener {
            isButtonPressed = true
            handler.postDelayed({
                if (isButtonPressed) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }, 3000)
            true
        }
        togglesirenButton.setOnClickListener {
            Toast.makeText(this, "사이렌 중지를 위해 3초간 눌러주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSirenAndReturnToPrevious() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        countdownTimer?.cancel()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

