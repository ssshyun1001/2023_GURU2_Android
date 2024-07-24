import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_dsjouju_app.R

// PoliceStation 데이터 클래스 정의
data class PoliceStation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

// DBHelper 클래스 정의
class DBHelper(context: Context) : SQLiteOpenHelper(context, "map_database.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // PoliceStations 테이블 생성
        db.execSQL(
            """CREATE TABLE PoliceStations (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                name TEXT, 
                address TEXT,
                latitude REAL,
                longitude REAL
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 테이블 업그레이드 시 기존 테이블 삭제 후 재생성
        db.execSQL("DROP TABLE IF EXISTS PoliceStations")
        onCreate(db)
    }
}

// 데이터 삽입 함수 정의
fun insertPoliceStations(db: SQLiteDatabase, stations: List<PoliceStation>) {
    for (station in stations) {
        val values = ContentValues().apply {
            put("name", station.name)
            put("address", station.address)
            put("latitude", station.latitude)
            put("longitude", station.longitude)
        }
        db.insert("PoliceStations", null, values)
    }
}

// MainActivity 클래스 정의
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

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

        // 데이터 삽입
        insertPoliceStations(db, policeStations)
    }
}
