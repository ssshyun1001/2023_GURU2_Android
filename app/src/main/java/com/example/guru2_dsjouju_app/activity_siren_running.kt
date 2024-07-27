// Siren_running.kt
package com.example.guru2_dsjouju_siren

import SoundPlayer
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.guru2_dsjouju_app.R

class Siren_running : AppCompatActivity() {
    private var isSirenOn = false
    private var isSosOn = false  // SOS 버튼의 상태를 추적하는 변수
    private lateinit var sirenStatusText: TextView
    private lateinit var toggleSirenButton: Button
    private lateinit var sosStatusText: TextView
    private lateinit var sosButton: Button

    private val handler = Handler()
    private var buttonPressStartTime: Long = 0
    private val pressDurationMs: Long = 3000 // 3초
    private lateinit var soundPlayer: SoundPlayer

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        sirenStatusText = findViewById(R.id.sirenStatusText)
        toggleSirenButton = findViewById(R.id.toggleSirenButton)
        sosButton = findViewById(R.id.sosButton)


        soundPlayer = SoundPlayer(this, R.raw.siren_sound) //사이렌 사운드 추가해야함
        // 초기 상태 설정
        updateSirenStatus()
        updateSosStatus()


        toggleSirenButton.setOnLongClickListener {
            buttonPressStartTime = System.currentTimeMillis()
            handler.postDelayed({
                if (System.currentTimeMillis() - buttonPressStartTime >= pressDurationMs) {
                    isSirenOn = !isSirenOn
                    updateSirenStatus()
                }
            }, pressDurationMs)
            true
        }

        sosButton.setOnClickListener {
            isSosOn = !isSosOn
            updateSosStatus()
        }
    }

    private fun updateSirenStatus() {
        if (isSirenOn) {
            sirenStatusText.text = "사이렌 상태: 켜짐"
            sirenStatusText.setTextColor(ContextCompat.getColor(this, R.color.red)) // 사이렌 켜짐 색상
            soundPlayer.playSound() // 사이렌 켜질 때 소리 재생
        } else {
            sirenStatusText.text = "사이렌 상태: 꺼짐"
            sirenStatusText.setTextColor(ContextCompat.getColor(this, R.color.black)) // 사이렌 꺼짐 색상
            soundPlayer.release() // 사이렌 꺼질 때 소리 정지
        }
    }

    private fun updateSosStatus() {
        if (isSosOn) {
            sosStatusText.text = "SOS 상태: 켜짐"
            sosButton.setBackgroundResource(R.drawable.app_icon_sos_o) // 활성화된 SOS 버튼 이미지
            Toast.makeText(this, "SOS 활성화됨", Toast.LENGTH_SHORT).show() // SOS 켜졌을 때 Toast 메시지
        } else {
            sosStatusText.text = "SOS 상태: 꺼짐"
            sosButton.setBackgroundResource(R.drawable.app_icon_sos_x) // 비활성화된 SOS 버튼 이미지
            Toast.makeText(this, "SOS 비활성화됨", Toast.LENGTH_SHORT).show() // SOS 꺼졌을 때 Toast 메시지
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources when the activity is destroyed
        soundPlayer.release()
    }
}
