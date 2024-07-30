package com.example.guru2_dsjouju_app

import android.content.Context
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class SosManager(private val context: Context, private val sosButton: ImageButton) {

    var sosisrunning = 0
    private var countdownTimer: CountDownTimer? = null

    fun showSosDialog() {
        if (sosisrunning == 1) {
            Toast.makeText(context, "SOS 도움 요청 실행 중, Off를 원하시면 3초간 길게 눌러주세요", Toast.LENGTH_LONG).show()
            return
        }

        val builder = AlertDialog.Builder(context)

        builder.setMessage("SOS 도움 요청을 시작하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("네") { _, _ ->
                startSos()
                countdownTimer?.cancel()
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
                countdownTimer?.cancel()
            }

        val alert = builder.create()
        alert.show()

        countdownTimer = object : CountDownTimer(3500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                alert.setMessage("SOS 도움 요청을 시작하시겠습니까?\n자동 선택까지 ${millisUntilFinished / 1000}초 남았습니다.")
            }

            override fun onFinish() {
                startSos()
                alert.dismiss()
            }
        }.start()
    }

    private fun startSos() {
        sosisrunning = 1
        sosButton.setImageResource(R.drawable.app_icon_sos_o) // 아이콘 변경 (적절한 아이콘으로 대체)
        Toast.makeText(context, "SOS 도움 요청이 시작되었습니다", Toast.LENGTH_LONG).show()
    }

    fun handleLongPress() {
        if (sosisrunning == 1) {
            startStopCountdown()
        }
    }

    private fun startStopCountdown() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsElapsed = (3000 - millisUntilFinished) / 1000
                when (secondsElapsed) {
                    1L -> sosButton.setImageResource(R.drawable.app_icon_sos_1)
                    2L -> sosButton.setImageResource(R.drawable.app_icon_sos_2)
                }
            }

            override fun onFinish() {
                stopSos()
            }
        }.start()
    }


    private fun stopSos() {
        sosisrunning = 0
        sosButton.setImageResource(R.drawable.app_icon_sos_x) // 비활성화 아이콘으로 변경
        Toast.makeText(context, "SOS 도움 요청이 종료되었습니다", Toast.LENGTH_LONG).show()
    }
}