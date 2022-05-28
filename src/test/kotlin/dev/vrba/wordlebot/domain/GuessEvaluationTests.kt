package dev.vrba.wordlebot.domain

import dev.vrba.wordlebot.domain.LetterEvaluation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GuessEvaluationTests {

    @Test
    fun `test guess evaluation`() {
        assertThat(evaluateGuess("limbo", "phono")).isEqualTo(GuessEvaluation("limbo", listOf(Absent, Absent, Absent, Absent, Correct)))
        assertThat(evaluateGuess("limbo", "hello")).isEqualTo(GuessEvaluation("limbo", listOf(Present, Absent, Absent, Absent, Correct)))
        assertThat(evaluateGuess("hello", "hello")).isEqualTo(GuessEvaluation("hello", listOf(Correct, Correct, Correct, Correct, Correct)))
        assertThat(evaluateGuess("tares", "stare")).isEqualTo(GuessEvaluation("tares", listOf(Present, Present, Present, Present, Present)))
        assertThat(evaluateGuess("limbo", "could")).isEqualTo(GuessEvaluation("limbo", listOf(Present, Absent, Absent, Absent, Present)))
        assertThat(evaluateGuess("hello", "could")).isEqualTo(GuessEvaluation("hello", listOf(Absent, Absent, Absent, Correct, Present)))
    }

}