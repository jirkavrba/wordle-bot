package dev.vrba.wordlebot.domain

enum class LetterEvaluation(val emoji: String, val symbol: String) {
    Correct("\uD83D\uDFE9", "C"),
    Present("\uD83D\uDFE8", "P"),
    Absent("\uD83D\uDFE5", "A")
}