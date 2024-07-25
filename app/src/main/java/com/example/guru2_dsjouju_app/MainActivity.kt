package com.example.guru2_dsjouju_app;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu

class MainActivity : AppCompatActivity() {

    lateinit var homeoptionmenubtn: Button

    // 권한 요청 결과를 처리하는 ActivityResultLauncher를 정의
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 각 권한 요청 결과를 처리
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                // 권한이 승인된 경우
                Toast.makeText(this, "$permissionName granted", Toast.LENGTH_SHORT).show()
            } else {
                // 권한이 거부된 경우
                Toast.makeText(this, "$permissionName denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeoptionmenubtn = findViewById(R.id.home_option_menu_btn)

        homeoptionmenubtn.setOnClickListener { showPopupMenu(it) }

        // 권한이 필요한 경우 요청
        checkAndRequestPermissions()
    }

    // 팝업 메뉴
    private fun showPopupMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_tutorial -> {
                    // 사용법 항목 클릭 시 MainActivity로 이동
                    //val intent = Intent(this, MainActivity::class.java)
                    //startActivity(intent)
                    true
                }
                R.id.settings -> {
                    // 설정 항목 클릭 시 Settings로 이동
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun checkAndRequestPermissions() {
        // 요청할 권한 목록
        val requiredPermissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // 요청할 권한 중 아직 승인되지 않은 권한 필터링
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // 승인되지 않은 권한이 있으면 권한 요청 실행
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
