package com.example.guru2_dsjouju_app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class Call_activity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var sirenButton: ImageButton
    private lateinit var sosManager: SosManager
    private lateinit var sosButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var sosIsRunning: SosIsRunning

    private lateinit var loginID: String

    private lateinit var timerTextView: TextView
    private val handler = Handler()
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        // 로그인 ID를 인텐트에서 추출
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        // MediaPlayer 초기화 및 에러 처리
        mediaPlayer = MediaPlayer.create(this, R.raw.calling_recording).apply {
            start()
        }

        // TextView 초기화
        timerTextView = findViewById(R.id.text_call_timer)

        // Timer 시작
        startTime = SystemClock.elapsedRealtime()
        handler.post(timerRunnable)

        // 버튼 초기화
        stopButton = findViewById(R.id.call_break_btn)
        sirenButton = findViewById(R.id.button_siren)
        sosButton = findViewById(R.id.button_sos)
        sosButton.setImageResource(R.drawable.app_icon_sos_x)

        // SosManager 인스턴스 생성 시 loginID 전달
        sosManager = SosManager(this, sosButton, loginID)

        sosManager.updateSosButtonImage()

        // Application 클래스 인스턴스 얻기
        sosIsRunning = application as SosIsRunning

        // 사이렌 버튼 클릭 리스너 설정
        sirenButton.setOnClickListener {
            // 호출 소리 중지
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                mediaPlayer = null
            }

            // 사이렌 페이지로 이동
            val intent = Intent(this, Siren_running::class.java).apply {
                putExtra("LOGIN_ID", loginID)
                finish()
            }
            startActivity(intent)
        }

        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            sosIsRunning.isSosRunning = false
            true
        }

        stopButton.setOnClickListener {
            // 호출 소리 중지
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                mediaPlayer = null
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 activity를 종료합니다.
        }
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsedMillis = SystemClock.elapsedRealtime() - startTime
            val seconds = (elapsedMillis / 1000) % 60
            val minutes = (elapsedMillis / (1000 * 60)) % 60
            val hours = (elapsedMillis / (1000 * 60 * 60)) % 24
            timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }
}