package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // 메인 화면의 초기화 작업 수행
        initializeUI()
    }

    private fun initializeUI() {

        // 예시: 버튼 클릭 리스너 설정
        val exampleButton: Button = findViewById(R.id.exampleButton)
        exampleButton.setOnClickListener {
            // 버튼 클릭 시 동작을 정의
        }

        // 로그아웃 버튼 초기화 및 클릭 리스너 설정
        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // 메인 화면 Activity를 종료합니다.
        }
    }
}
