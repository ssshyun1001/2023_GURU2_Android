package com.example.guru2_dsjouju_app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Call_activity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var sirenButton: ImageButton
    private lateinit var sosManager: SosManager
    private lateinit var sosButton: ImageButton
    private lateinit var stopButton: ImageButton

    private lateinit var loginID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        // 로그인 ID를 인텐트에서 추출
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        // MediaPlayer를 초기화하고 mp3 파일 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.calling_recording)
        mediaPlayer.start()

        // 버튼 초기화
        stopButton = findViewById(R.id.call_break_btn)
        sirenButton = findViewById(R.id.button_siren)
        sosButton = findViewById(R.id.button_sos)
        sosButton.setImageResource(R.drawable.app_icon_sos_x)

        // SosManager 인스턴스 생성 시 loginID 전달
        sosManager = SosManager(this, sosButton, loginID)

        sirenButton.setOnClickListener { startSirenActivity() }

        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }

        // 버튼 클릭 리스너 설정
        stopButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun startSirenActivity() {
        val intent = Intent(this, Siren_running::class.java)
        startActivity(intent)
    }
}