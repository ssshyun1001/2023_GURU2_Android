package com.example.guru2_dsjouju_app

import android.content.Context
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class SosManager(private val context: Context, private val sosButton: ImageButton, private val loginID: String) {

    private var isSosRunning = false
    private var sosCountdownTimer: CountDownTimer? = null
    private var stopCountdownTimer: CountDownTimer? = null
    private var periodicMessageTimer: CountDownTimer? = null
    private val sharedPreferences = context.getSharedPreferences("SirenPrefs", Context.MODE_PRIVATE)

    private val sendMessage = SendMessage(context, loginID)

    fun showSosDialog() {
        if (isSosRunning) {
            Toast.makeText(context, "SOS 도움 요청 실행, Off를 원하시면 길게 눌러주세요", Toast.LENGTH_LONG).show()
            return
        }

        val builder = AlertDialog.Builder(context)

        builder.setMessage("SOS 도움 요청을 시작하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("네") { _, _ ->
                startSos()
                sosCountdownTimer?.cancel()
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
                sosCountdownTimer?.cancel()
            }

        val alert = builder.create()
        alert.show()

        sosCountdownTimer = object : CountDownTimer(3500, 1000) {
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
        isSosRunning = true
        sosButton.setImageResource(R.drawable.app_icon_sos_o) // 아이콘 변경 (적절한 아이콘으로 대체)
        Toast.makeText(context, "SOS 도움 요청이 시작되었습니다", Toast.LENGTH_LONG).show()

        startPeriodicMessage()
    }

    private fun stopSos() {
        isSosRunning = false
        sosButton.setImageResource(R.drawable.app_icon_sos_x)
        Toast.makeText(context, "SOS 도움 요청이 종료되었습니다", Toast.LENGTH_LONG).show()

        periodicMessageTimer?.cancel()
    }

    fun setSosUpdateFrequency(frequencyMillis: Long) {
        if (isSosRunning) {
            periodicMessageTimer?.cancel()
            periodicMessageTimer = object : CountDownTimer(Long.MAX_VALUE, frequencyMillis) {
                override fun onTick(millisUntilFinished: Long) {
                    sendMessage.sendLocationSMS()
                }

                override fun onFinish() {}
            }.start()
        }
    }

    fun handleLongPress() {
        if (isSosRunning) {
            startStopCountdown()
        }
    }

    private fun startPeriodicMessage() {
        val frequency = sharedPreferences.getString("spinner_selection", "1분") ?: "1분"
        val minutes = frequency.replace("분", "").toIntOrNull() ?: 1
        val intervalMillis = minutes * 60 * 1000L

        setSosUpdateFrequency(intervalMillis)
    }

    private fun startStopCountdown() {
        stopCountdownTimer?.cancel()
        stopCountdownTimer = object : CountDownTimer(999, 333) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsElapsed = (999 - millisUntilFinished) / 333
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
}
