package com.example.guru2_dsjouju_app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.telephony.SmsManager
import android.util.Log

// SMS 전송 클래스
class SendMessage(private val context: Context, private val loginID: String) {

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
        val dao = ContactsDAO(context, loginID)
        val contacts = dao.getContactsById()

        // SharedPreferences에서 커스텀 메시지를 불러오기
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        val savedMessage = sharedPreferences.getString("sos_message", "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!") ?: "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!"

        val message = "현재 위치: https://maps.google.com/?q=${location.latitude},${location.longitude}"

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val smsManager = SmsManager.getDefault()
                for (contact in contacts) {
                    Log.d("SendMessage", "Sending SMS to: ${contact.phone}, message: $message")
                    smsManager.sendTextMessage(contact.phone, null, savedMessage, null, null)
                    smsManager.sendTextMessage(contact.phone, null, message, null, null)
                }
                Toast.makeText(context, "메시지 전송 < $savedMessage >", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "메시지 전송 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "SMS 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
