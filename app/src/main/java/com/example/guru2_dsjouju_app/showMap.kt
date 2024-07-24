package com.example.myapp

import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson

class MapManager(private val context: Context, private val webView: WebView) {

    init {
        // WebView 설정
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                println("Page loaded: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                println("Error occurred: $description")
            }
        }
    }

    fun loadMap(policeStations: List<PoliceStation>) {
        val jsonStations = Gson().toJson(policeStations)
        val htmlContent = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <title>SOP JavaScript : Map Simple</title>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script type="text/javascript" src="https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=42f58d7206ab4e40b424"></script>
                <script type="text/javascript" src="https://sgisapi.kostat.go.kr/OpenAPI3/js/sop.map.js"></script>
                <style>
                    body { margin: 0; padding: 0; }
                    #map { width: 100%; height: 100vh; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script type="text/javascript">
                    var map = sop.map('map');
                    map.setView(sop.utmk(953820, 1953437), 9);

                    const policeStations = $jsonStations;
                    var blueMarkerIcon = sop.icon({
                        iconUrl: 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png',
                        iconSize: [32, 32],
                        iconAnchor: [16, 32]
                    });

                    policeStations.forEach(function(station) {
                        var marker = sop.marker([station.latitude, station.longitude], { icon: blueMarkerIcon }).addTo(map);
                        marker.bindPopup(station.name);
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}

data class PoliceStation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
