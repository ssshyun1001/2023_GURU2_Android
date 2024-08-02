package com.example.guru2_dsjouju_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // 권한 요청 코드 상수
    private val PERMISSION_REQUEST_CODE = 100

    private lateinit var homeoptionmenubtn: ImageButton

    private lateinit var sirenButton: ImageButton
    private lateinit var sosManager: SosManager
    private lateinit var sosButton: ImageButton
    private lateinit var callButton: ImageButton

    private lateinit var loginID: String

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 로그인 ID를 인텐트에서 추출
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        homeoptionmenubtn = findViewById(R.id.home_option_menu_btn)

        sirenButton = findViewById(R.id.button_siren)
        sosButton = findViewById(R.id.button_sos)
        sosButton.setImageResource(R.drawable.app_icon_sos_x)
        callButton = findViewById(R.id.button_call)

        homeoptionmenubtn.setOnClickListener { showPopupMenu(it) }
        sirenButton.setOnClickListener { startSirenActivity() }
        sosManager = SosManager(this, sosButton, loginID)
        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }
        callButton.setOnClickListener { startCallActivity() }

        // 권한이 필요한 경우 요청
        checkAndRequestPermissions()
    }
    private fun checkAndRequestPermissions() {
        // 요청할 권한 목록
        val requiredPermissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        // 요청할 권한 중 아직 승인되지 않은 권한 필터링
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        // 승인되지 않은 권한이 있으면 권한 요청 실행
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            // 권한이 이미 승인된 경우 initializeMap 실행
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }
    }
    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    allPermissionsGranted = false
                    Toast.makeText(this, "$permission 접근 거부됨", Toast.LENGTH_SHORT).show()
                }
            }

            if (allPermissionsGranted) {
                // Initialize the map
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        initializeMap()
    }

    private fun initializeMap() {
        val seoul = LatLng(37.5665, 126.9780)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12.0f))
        addMarkers()
    }

    private fun addMarkers() {
        val policeStations = listOf(
            PoliceStation("서울특별시경찰청", "서울시 종로구 사직로8길 31", 37.575935, 126.979196),
            PoliceStation("서울강남경찰서", "서울시 강남구 테헤란로 114길 11", 37.499176, 127.027672),
            PoliceStation("서울강동경찰서", "서울시 강동구 성내로 57", 37.536693, 127.126411),
            PoliceStation("서울강북경찰서", "서울시 강북구 오패산로 406", 37.636442, 127.024597),
            PoliceStation("서울강서경찰서", "서울시 강서구 화곡로 308", 37.558691, 126.849762),
            PoliceStation("서울관악경찰서", "서울시 관악구 관악로5길 33", 37.480034, 126.955569),
            PoliceStation("서울광진경찰서", "서울시 광진구 자양로 167", 37.538774, 127.082982),
            PoliceStation("서울구로경찰서", "서울시 구로구 가마산로 235", 37.495566, 126.885061),
            PoliceStation("서울금천경찰서", "서울시 금천구 시흥대로73길 50", 37.463731, 126.905759),
            PoliceStation("서울남대문경찰서", "서울시 중구 한강대로 410", 37.554029, 126.993675),
            PoliceStation("서울노원경찰서", "서울시 노원구 노원로 283", 37.655576, 127.061574),
            PoliceStation("서울도봉경찰서", "서울시 도봉구 노해로 403", 37.652361, 127.028242),
            PoliceStation("서울동대문경찰서", "서울시 동대문구 약령시로21길 29", 37.573979, 127.020186),
            PoliceStation("서울동작경찰서", "서울시 동작구 노량진로 148", 37.511348, 126.939811),
            PoliceStation("서울마포경찰서", "서울시 마포구 마포대로 183", 37.542225, 126.944294),
            PoliceStation("서울방배경찰서", "서울시 서초구 동작대로 204", 37.487496, 126.989119),
            PoliceStation("서울서대문경찰서", "서울 서대문구 통일로 113", 37.566435, 126.936332),
            PoliceStation("서울서부경찰서", "서울시 은평구 진흥로 58", 37.613456, 126.925710),
            PoliceStation("서울서초경찰서", "서울시 서초구 반포대로 179", 37.503243, 127.013872),
            PoliceStation("서울성동경찰서", "서울시 성동구 왕십리광장로 9", 37.564389, 127.043711),
            PoliceStation("서울성북경찰서", "서울 성북구 보문로 170", 37.588762, 127.013333),
            PoliceStation("서울송파경찰서", "서울시 송파구 중대로 221", 37.511547, 127.106303),
            PoliceStation("서울수서경찰서", "서울시 강남구 개포로 617", 37.491384, 127.065143),
            PoliceStation("서울양천경찰서", "서울시 양천구 목동동로 99", 37.523062, 126.864724),
            PoliceStation("서울영등포경찰서", "서울시 영등포구 국회대로 608", 37.523062, 126.864724),
            PoliceStation("서울용산경찰서", "서울시 용산구 원효로89길 24", 37.536393, 126.966144),
            PoliceStation("서울은평경찰서", "서울시 은평구 연서로 365", 37.608931, 126.927963),
            PoliceStation("서울종로경찰서", "서울시 종로구 율곡로 46", 37.570336, 126.989484),
            PoliceStation("서울종암경찰서", "서울시 성북구 종암로 135", 37.610162, 127.020668),
            PoliceStation("서울중랑경찰서", "서울시 중랑구 신내역로3길 40-10", 37.606155, 127.092708),
            PoliceStation("서울중부경찰서", "서울시 중구 수표로 27", 37.560335, 126.997474),
            PoliceStation("서울혜화경찰서", "서울시 종로구 창경궁로 112-16", 37.576698, 126.992526)
        )

        for (station in policeStations) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(station.latitude, station.longitude))
                    .title(station.name)
                    .snippet(station.address)
            )
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                }
            }
        }
    }

    private fun createHeatmap(data: List<LatLng>) {
        val weightedLatLngs = data.map { WeightedLatLng(it) }
        val heatmapProvider = HeatmapTileProvider.Builder()
            .weightedData(weightedLatLngs)
            .radius(50)
            .build()
        mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
    }

    // 상단 옵션 메뉴
    private fun showPopupMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_tutorial -> {
                    // 사용법 항목 클릭 시 Tutorial로 이동
                    val intent = Intent(this, Tutorial::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_settings -> {
                    // Settings 액티비티로 이동하면서 LOGIN_ID를 전달합니다.
                    val intent = Intent(this, Settings::class.java)
                    intent.putExtra("LOGIN_ID", loginID)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    private fun startSirenActivity() {
        // Start Siren activity
        val intent = Intent(this, Siren_running::class.java)
        startActivity(intent)
    }

    private fun startCallActivity() {
        // Start Call activity
        val intent = Intent(this, ReceiveCall::class.java)
        startActivity(intent)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Handle marker click
        Toast.makeText(this, "${marker.title}", Toast.LENGTH_SHORT).show()
        return false
    }

    interface GooglePlacesService {
        @GET("nearbysearch/json")
        fun getNearbyPlaces(
            @Query("type") type: String,
            @Query("location") location: String,
            @Query("radius") radius: Int,
            @Query("key") key: String
        ): Call<NearbyPlacesResponse>
    }

    data class NearbyPlacesResponse(val results: List<Place>)
    data class Place(val name: String, val geometry: Geometry)
    data class Geometry(val location: LocationDetails)
    data class LocationDetails(val lat: Double, val lng: Double)
}
