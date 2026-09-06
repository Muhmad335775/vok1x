package com.vok1x.app.national

import androidx.compose.ui.graphics.Color

data class Country(val name: String, val landmark: String?, val flagColors: List<Color>)

object NationalCountries {
    val all: List<Country> = listOf(
        Country("Egypt", "The Pyramids", listOf(Color.Red, Color.White, Color.Black)),
        Country("Saudi Arabia", "Kingdom Tower", listOf(Color(0xFF006C35), Color.White)),
        Country("UAE", "Burj Khalifa", listOf(Color.Red, Color(0xFF00732F), Color.White)),
        Country("Jordan", "Petra", listOf(Color.Black, Color.White, Color(0xFF007A3D))),
        Country("Morocco", "Bahia Palace", listOf(Color.Red, Color(0xFF006233))),
        Country("Iraq", "Ishtar Gate", listOf(Color.Red, Color.White, Color.Black)),
        Country("Lebanon", "Raouche Rock", listOf(Color.Red, Color.White, Color(0xFF00A651))),
        Country("Syria", "Aleppo Citadel", listOf(Color(0xFF007A3D), Color.White, Color.Black)),
        Country("France", "Eiffel Tower", listOf(Color.Blue, Color.White, Color.Red)),
        Country("Italy", "The Colosseum", listOf(Color(0xFF009246), Color.White, Color(0xFFCE2B37))),
        Country("UK", "Big Ben", listOf(Color.Blue, Color.White, Color.Red)),
        Country("Germany", "Brandenburg Gate", listOf(Color.Black, Color.Red, Color(0xFFFFCE00))),
        Country("Spain", "Royal Palace", listOf(Color.Red, Color(0xFFFFC400))),
        Country("USA", "Statue of Liberty", listOf(Color.Red, Color.White, Color.Blue)),
        Country("Greece", "The Parthenon", listOf(Color.Blue, Color.White)),
        Country("China", "The Great Wall", listOf(Color.Red, Color(0xFFFFDE00))),
        Country("Japan", "Mount Fuji", listOf(Color.White, Color.Red)),
        Country("India", "Taj Mahal", listOf(Color(0xFFFF9933), Color.White, Color(0xFF138808))),
        Country("Albania", "Kruja Castle", listOf(Color.Red, Color.Black)),
        Country("Romania", "Bran Castle", listOf(Color.Blue, Color(0xFFFFCE00), Color.Red))
    )
    fun pickRandom(): Country = all.random()
}
