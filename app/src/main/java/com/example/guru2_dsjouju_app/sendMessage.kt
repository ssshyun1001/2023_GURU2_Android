package com.example.guru2_dsjouju_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.telephony.SmsManager

// 통계청 기준으로 변경 필요, 기본 구글 틀로 시도해본 코드

class SendMessage(private val context: Context, private val phoneNumber: String) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun sendLocationSMS() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        sendSMS(it)
                    } ?: run {
                        Toast.makeText(context, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSMS(location: Location) {
        val message = "위치: https://maps.google.com/?q=${location.latitude},${location.longitude}"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val smsManager = SmsManager.getDefault()  //Deprecation 경고가 뜨지만, 발송 확인 완료
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(context, "SOS 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "메시지 전송 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "SMS 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}