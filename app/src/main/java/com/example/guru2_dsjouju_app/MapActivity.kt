package com.example.guru2_dsjouju_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ZoomControls
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

interface ConvenienceStoreService {
    @GET("getConvenienceStore")
    fun getConvenienceStores(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("type") type: String
    ): Call<ConvenienceStoreResponse>
}

data class ConvenienceStoreResponse(
    @SerializedName("response")
    val response: ConvenienceStoreResponseBody
) {
    data class ConvenienceStoreResponseBody(
        @SerializedName("body")
        val body: Body
    ) {
        data class Body(
            @SerializedName("items")
            val items: Items
        ) {
            data class Items(
                @SerializedName("item")
                val item: List<ConvenienceStore>
            )
        }
    }
}

data class ConvenienceStore(
    @SerializedName("CONV_STORE_NAME")
    val name: String,
    @SerializedName("CONV_STORE_ADDR")
    val address: String,
    @SerializedName("LAT")
    val latitude: Double,
    @SerializedName("LNG")
    val longitude: Double
)

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap

    //private lateinit var zoomControls: ZoomControls
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize location permission launcher
        locationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG)
                        .show()
                    initializeMap()
                    addPoliceStationMarkers()
                    createHeatmap(
                        LatLng(
                            37.5665,
                            126.9780
                        )
                    ) // Use default location (Seoul) for heatmap
                }
            }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
            ?: throw IllegalStateException("Map Fragment not found")

        // Request location permission
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        initializeMap() // Initialize the map to Seoul by default
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                    addPoliceStationMarkers()
                    createHeatmap(currentLatLng)
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_LONG).show()
                    initializeMap()
                    addPoliceStationMarkers()
                    createHeatmap(
                        LatLng(
                            37.5665,
                            126.9780
                        )
                    ) // Use default location (Seoul) for heatmap
                }
            }
        }
    }

    private fun initializeMap() {
        val seoul = LatLng(37.5665, 126.9780)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12.0f))
        addPoliceStationMarkers()
        loadCctvData()
        loadConvenienceStores()
        createHeatmap(seoul)

        // Zoom 컨트롤 설정
        // setupZoomControls()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val policeStation = marker.tag as? com.example.guru2_dsjouju_app.PoliceStation
        val cctv = marker.tag as? Cctv
        val convenienceStore = marker.tag as? ConvenienceStore

        policeStation?.let {
            Toast.makeText(this, "${it.name}\n${it.address}", Toast.LENGTH_SHORT).show()
        }
        cctv?.let {
            Toast.makeText(this, "${it.name}\n${it.address}", Toast.LENGTH_SHORT).show()
        }
        convenienceStore?.let {
            Toast.makeText(this, "${it.name}\n${it.address}", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    /*
private fun setupZoomControls() {
zoomControls.setOnZoomInClickListener {
mMap.animateCamera(CameraUpdateFactory.zoomIn())
}

zoomControls.setOnZoomOutClickListener {
mMap.animateCamera(CameraUpdateFactory.zoomOut())
}
}
*/

    private fun addPoliceStationMarkers() {
        // 경찰서 데이터 리스트
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

        policeStations.forEach { station ->
            val position = LatLng(station.latitude, station.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(station.name)
                .icon(BitmapDescriptorFactory.defaultMarker()) // Use default marker
            val marker = mMap.addMarker(markerOptions)
            marker?.tag = station
        }
    }

    data class Cctv(val name: String, val address: String, val latitude: Double, val longitude: Double)

    private fun addCctvMarkers(cctvs: List<Cctv>) {
        for (cctv in cctvs) {
            val position = LatLng(cctv.latitude, cctv.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(cctv.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            val marker = mMap.addMarker(markerOptions)
            marker?.tag = cctv
        }
    }

    private fun loadCctvData(): MutableList<Cctv> {
        val cctvs = mutableListOf<Cctv>()
        try {
            val inputStream: InputStream = assets.open("cctv_data.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)

            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val address = jsonObject.getString("address")
                val latitude = jsonObject.getDouble("latitude")
                val longitude = jsonObject.getDouble("longitude")

                val cctv = Cctv(name, address, latitude, longitude)
                cctvs.add(cctv)
            }

            addCctvMarkers(cctvs)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cctvs
    }

    private fun createHeatmap(currentLocation: LatLng) {
        val data = loadCctvData()
        val filteredData = data.filter { location ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                location.latitude,
                location.longitude,
                distance
            )
            distance[0] < 5000 // 5km radius
        }

        if (filteredData.isEmpty()) {
            Log.e("MapActivity", "No data found for heatmap.")
            return
        }

        val weightedLatLngs =
            filteredData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), 1.0) }
        val provider = HeatmapTileProvider.Builder()
            .weightedData(weightedLatLngs)
            .build()

        mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://safemap.go.kr/openApiService/data/") // 기본 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service by lazy {
        retrofit.create(ConvenienceStoreService::class.java)
    }
    private val serviceKey = "c8f9c9e669af4b529e96"

    private fun loadConvenienceStores() {
        service.getConvenienceStores(serviceKey, 1, 100, "json")
            .enqueue(object : Callback<ConvenienceStoreResponse> {
                override fun onResponse(
                    call: Call<ConvenienceStoreResponse>,
                    response: Response<ConvenienceStoreResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.response?.body?.items?.item?.let { stores ->
                            addConvenienceStoreMarkers(stores)
                        }
                    } else {
                        Log.e("MapActivity", "API call failed: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ConvenienceStoreResponse>, t: Throwable) {
                    Log.e("MapActivity", "API call failed: ${t.message}")
                    Toast.makeText(
                        this@MapActivity,
                        "Failed to load convenience stores",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun addConvenienceStoreMarkers(stores: List<ConvenienceStore>) {
        stores.forEach { store ->
            val position = LatLng(store.latitude, store.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(store.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) // Color for convenience stores
            mMap.addMarker(markerOptions)
        }
    }
}
class PackageAddedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // BroadcastReceiver에서 메인 스레드에서 수행할 작업 최소화
        if (intent?.action == Intent.ACTION_PACKAGE_ADDED) {
            // 긴 작업은 서비스나 워커로 이동
            context?.let {
                val serviceIntent = Intent(it, HandlePackageAddedService::class.java)
                serviceIntent.putExtra("packageName", intent.data?.encodedSchemeSpecificPart)
                it.startService(serviceIntent)
            }
        }
    }
}

class HandlePackageAddedService : IntentService("HandlePackageAddedService") {
    override fun onHandleIntent(intent: Intent?) {
        // 백그라운드에서 작업 수행
        val packageName = intent?.getStringExtra("packageName")
        // 패키지 추가 처리 로직
    }
}


        /*
    fun loadDataFromJson(): List<LatLng> {
        val data = mutableListOf<LatLng>()
        try {
            val json = assets.open("cctv_data.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            val dataArray = jsonObject.getJSONArray("DATA")

            for (i in 0 until dataArray.length()) {
                val obj = dataArray.getJSONObject(i)
                val lat = obj.getDouble("WGSXPT")
                val lng = obj.getDouble("WGSYPT")
                data.add(LatLng(lat, lng))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("MapActivity", "Loaded ${data.size} data points")
        return data
    }

    data class PoliceStation(
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    )*/