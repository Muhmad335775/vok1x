package com.vok1x.app.engine

data class ModeConfig(
    val id: Int,
    val name: String,
    val volumeReactivity: Float,
    val pitchReactivity: Float,
    val speedReactivity: Float,
    val colorTendency: Float
)

object ComedicModes {
    val all: List<ModeConfig> = listOf(
        ModeConfig(0, "Chaotic Shaker", 1.5f, 0.4f, 1.2f, 10f),
        ModeConfig(1, "Time-Traveling Elder", 0.5f, 1.6f, 0.5f, 45f),
        ModeConfig(2, "Exaggerated Crier", 1.8f, 0.6f, 0.6f, 200f),
        ModeConfig(3, "Smug Mocker", 0.6f, 0.9f, 0.4f, 280f),
        ModeConfig(4, "Terrified Trembler", 1.7f, 1.0f, 1.6f, 55f),
        ModeConfig(5, "Fast Talker", 0.7f, 0.5f, 1.9f, 150f),
        ModeConfig(6, "Exploding Angry", 1.9f, 0.3f, 0.8f, 0f),
        ModeConfig(7, "Dancing Fool", 1.0f, 0.8f, 1.3f, 320f),
        ModeConfig(8, "Dramatic Sulker", 0.4f, 1.2f, 0.3f, 260f)
    )

    fun pickRandom(): ModeConfig = all.random()
}
