package com.example.guru2_dsjouju_app
// 통화 수신 화면 버튼 기능 추가 (통화 받기 : 통화 중인 화면, 통화 거절 : 메인 화면으로 이동)

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ReceiveCall : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receivecall)

        val answerButton: ImageButton = findViewById(R.id.calling_answer)
        val hangupButton: ImageButton = findViewById(R.id.calling_hangup)

        answerButton.setOnClickListener {
            // activity_call_fix 화면으로 이동
            val intent = Intent(this, Call_activity::class.java)
            startActivity(intent)
        }

        hangupButton.setOnClickListener {
            // activity_main 화면으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 activity를 종료합니다.
        }
    }
}
