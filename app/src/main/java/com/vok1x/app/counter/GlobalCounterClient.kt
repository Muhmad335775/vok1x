package com.vok1x.app.counter

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GlobalCounterClient(context: Context) {
    private val prefs = context.getSharedPreferences("vok1x_counter", Context.MODE_PRIVATE)
    var count by mutableIntStateOf(prefs.getInt("last_count", 100000))
        private set

    private fun httpGetInt(urlString: String): Int? {
        return try {
            val connection = URL(urlString).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                JSONObject(response).optInt("value", -1).takeIf { it >= 0 }
            } else null
        } catch (e: Exception) { null }
    }

    fun incrementAndFetch(scope: CoroutineScope) {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                httpGetInt("https://api.countapi.xyz/hit/vok1x-app/voices-sent")
            }
            if (result != null) {
                count = result
                prefs.edit().putInt("last_count", result).apply()
            }
        }
    }
}
