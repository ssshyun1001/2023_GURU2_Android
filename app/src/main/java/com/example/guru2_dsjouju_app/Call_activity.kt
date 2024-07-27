package com.example.guru2_dsjouju_app

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android3.R

class CallActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_fix)

        // MediaPlayer를 초기화하고 mp3 파일 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.calling_recording)
        mediaPlayer.start()

        // 전화 모양 버튼 초기화
        stopButton = findViewById(R.id.button)

        // 버튼 클릭 리스너 설정
        stopButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}