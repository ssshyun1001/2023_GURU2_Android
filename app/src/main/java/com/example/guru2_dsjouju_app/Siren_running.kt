package com.example.guru2_dsjouju_app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Siren_running : AppCompatActivity() {

    private lateinit var sosManager: SosManager
    private lateinit var sosButton: ImageButton

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siren_running)

        sosButton = findViewById(R.id.sosButton)

        sosManager = SosManager(this, sosButton)
        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }

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
                    stopSirenAndReturnToMain()
                }
            }, 3000)
            true
        }
        togglesirenButton.setOnClickListener {
            Toast.makeText(this, "사이렌 중지를 위해 3초간 눌러주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSirenAndReturnToMain() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Toast.makeText(this, "사이렌 중지", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

