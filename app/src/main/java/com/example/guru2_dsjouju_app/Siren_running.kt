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
    private lateinit var sosIsRunning: SosIsRunning

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

        // 로그인 ID 및 SharedPreferences 초기화
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE)

        // UI 요소 초기화
        sosButton = findViewById(R.id.sosButton)
        sosManager = SosManager(this, sosButton, loginID)
        sosIsRunning = application as SosIsRunning

        // SOS 버튼 이벤트 리스너 설정
        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            sosIsRunning.isSosRunning = false
            true
        }

        // 오디오 매니저 초기화
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // 사이렌 다이얼로그 표시 및 SOS 버튼 이미지 업데이트
        showSirenDialog()
        sosManager.updateSosButtonImage()

        // 사이렌 시작 버튼 초기화
        val toggleSirenButton: ImageButton = findViewById(R.id.toggleSirenButton)
        toggleSirenButton.setOnClickListener {
            if (mediaPlayer == null) {
                startSiren()
            } else {
                Toast.makeText(this, "사이렌 소리가 이미 시작되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 사이렌 시작 다이얼로그 표시
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

        // 3초 후 사이렌 자동 시작
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

    // 사이렌 시작
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

        // 오디오 포커스 요청 및 사이렌 소리 재생
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

        // 사이렌 정지 버튼 이벤트 리스너 설정
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

    // 사이렌 정지 및 이전 화면으로 복귀
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

