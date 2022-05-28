package dev.vrba.wordlebot.domain

enum class LetterEvaluation(val emoji: String) {
    Correct("\uD83D\uDFE9"),
    Present("\uD83D\uDFE8"),
    Absent("\u2B1B")
}