package com.example.guru2_dsjouju_app
// 통화 수신 화면 버튼 기능 추가 (통화 받기 : 통화 중인 화면, 통화 거절 : 메인 화면으로 이동)

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class calling : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
    }
}