package com.example.guru2_dsjouju_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private var loginID: EditText? = null
    private var loginPW: EditText? = null
    private var btnLogin: Button? = null
    private var btnSignup: Button? = null
    private var userDAO: UserDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginID = findViewById(R.id.ID)
        loginPW = findViewById(R.id.PW)
        btnLogin = findViewById(R.id.buttonLogin)
        btnSignup = findViewById(R.id.buttonSignUp)
        userDAO = UserDAO(this)

        btnLogin?.setOnClickListener {
            val id = loginID?.text.toString().trim()
            val password = loginPW?.text.toString().trim()
            if (userDAO?.checkUser(id, password) == true) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                // 로그인 성공 시 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()  // 현재 Login 액티비티 종료
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignup?.setOnClickListener { showSignupDialog() }
    }

    @SuppressLint("InflateParams")
    private fun showSignupDialog() {
        val builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_signup, null)
        builder.setView(view)

        val etSignupId = view.findViewById<EditText>(R.id.signupID)
        val btnCheckId = view.findViewById<Button>(R.id.checkID)
        val etSignupPassword = view.findViewById<EditText>(R.id.signupPW)
        val etSignupPhone = view.findViewById<EditText>(R.id.textPhone)
        val btnSignupSubmit = view.findViewById<Button>(R.id.signUp)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)  // backButton 추가

        btnCheckId.setOnClickListener {
            val id = etSignupId.text.toString().trim()
            if (userDAO?.checkIfIdExists(id) == true) {
                Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = builder.create()

        backButton.setOnClickListener {
            // 다이얼로그를 닫고 로그인 화면으로 돌아가기
            dialog.dismiss()
        }

        btnSignupSubmit.setOnClickListener {
            val id = etSignupId.text.toString().trim()
            val password = etSignupPassword.text.toString().trim()
            val phone = etSignupPhone.text.toString().trim()
            if (id.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty()) {
                if (userDAO?.insertUser(id, password, phone) == true) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val dialog = builder.create()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
