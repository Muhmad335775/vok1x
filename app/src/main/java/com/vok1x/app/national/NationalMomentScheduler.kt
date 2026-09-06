package com.vok1x.app.national

import android.content.Context
import java.util.Calendar
import kotlin.random.Random

class NationalMomentScheduler(private val context: Context) {
    private val prefs = context.getSharedPreferences("vok1x_national", Context.MODE_PRIVATE)

    private fun ensureWindowsComputedForToday() {
        val today = todayKey()
        if (prefs.getString("day", null) == today) return
        val count = if (Random.nextBoolean()) 1 else 2
        val editor = prefs.edit().putString("day", today).putInt("count", count)
        repeat(count) { i -> editor.putInt("start_$i", Random.nextInt(0, 1440)) }
        editor.apply()
    }

    fun isNationalMomentNow(): Boolean {
        ensureWindowsComputedForToday()
        val cal = Calendar.getInstance()
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val count = prefs.getInt("count", 0)
        for (i in 0 until count) {
            val start = prefs.getInt("start_$i", -1)
            if (start != -1 && nowMinutes in start until (start + 10)) return true
        }
        return false
    }

    private fun todayKey(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }
}
