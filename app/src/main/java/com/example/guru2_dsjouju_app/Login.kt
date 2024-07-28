package com.example.guru2_dsjouju_app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_dsjouju_app.R

class Login : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val loginButton: Button = findViewById(R.id.buttonLogin)
        val usernameEditText: EditText = findViewById(R.id.ID)
        val passwordEditText: EditText = findViewById(R.id.PW)
        val signUpButton: Button = findViewById(R.id.buttonSignUp)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 로그인 검증 로직을 여기에 추가합니다. (예: 사용자 이름과 비밀번호 확인)
            if (validateLogin(username, password)) {
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 로그인 Activity를 종료합니다.
            } else {
                // 로그인 실패 시 처리 로직 추가
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        signUpButton.setOnClickListener {
            showSignUpDialog()
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // 여기에 실제 로그인 검증 로직을 추가하세요.
        return username.isNotEmpty() && password.isNotEmpty()
    }

    private fun showSignUpDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_signup, null)
        val usernameEditText: EditText = dialogView.findViewById(R.id.textID)
        val checkUsernameButton: Button = dialogView.findViewById(R.id.checkID)
        val passwordEditText: EditText = dialogView.findViewById(R.id.textPW)
        val createAccountButton: Button = dialogView.findViewById(R.id.signUp)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        checkUsernameButton.setOnClickListener {
            // 아이디 중복확인 로직
            val username = usernameEditText.text.toString()
            if (username.isNotEmpty() && checkUsername(username)) {
                Toast.makeText(this, "아이디 사용 가능", Toast.LENGTH_SHORT).show()
                usernameEditText.isEnabled = false
            } else {
                Toast.makeText(this, "아이디 중복", Toast.LENGTH_SHORT).show()
            }
        }

        createAccountButton.setOnClickListener {
            // 회원가입 로직
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty() && usernameEditText.isEnabled.not()) {
                // 회원가입 성공
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                dialogBuilder.dismiss()
            } else {
                // 회원가입 실패
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.show()
    }

    private fun checkUsername(username: String): Boolean {
        // 아이디 중복확인 로직 구현
        // 실제로는 서버나 데이터베이스와 통신하여 아이디 중복 여부를 확인해야 합니다.
        // 여기서는 간단히 아이디가 "user"가 아닌 경우 사용 가능하다고 가정합니다.
        return username != "user"
    }
}
