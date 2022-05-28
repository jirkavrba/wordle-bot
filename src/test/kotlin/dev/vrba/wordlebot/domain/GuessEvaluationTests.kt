package dev.vrba.wordlebot.domain

import dev.vrba.wordlebot.domain.LetterEvaluation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GuessEvaluationTests {

    @Test
    fun `test guess evaluation`() {
        assertThat(evaluateGuess("limbo", "phono")).isEqualTo(
            GuessEvaluation(
                "limbo",
                listOf(Absent, Absent, Absent, Absent, Correct)
            )
        )
        assertThat(evaluateGuess("limbo", "hello")).isEqualTo(
            GuessEvaluation(
                "limbo",
                listOf(Present, Absent, Absent, Absent, Correct)
            )
        )
        assertThat(evaluateGuess("hello", "hello")).isEqualTo(
            GuessEvaluation(
                "hello",
                listOf(Correct, Correct, Correct, Correct, Correct)
            )
        )
        assertThat(evaluateGuess("tares", "stare")).isEqualTo(
            GuessEvaluation(
                "tares",
                listOf(Present, Present, Present, Present, Present)
            )
        )
        assertThat(evaluateGuess("limbo", "could")).isEqualTo(
            GuessEvaluation(
                "limbo",
                listOf(Present, Absent, Absent, Absent, Present)
            )
        )
        assertThat(evaluateGuess("hello", "could")).isEqualTo(
            GuessEvaluation(
                "hello",
                listOf(Absent, Absent, Absent, Correct, Present)
            )
        )
    }

    @Test
    fun `test letter mask construction`() {
        val alphabet = ('a'..'z').toSet()

        assertEquals(
            GuessEvaluation("limbo", listOf(Correct, Present, Absent, Absent, Present)).letterMask(),
            listOf(
                setOf('l'),
                alphabet - setOf('m', 'b', 'i'),
                alphabet - setOf('m', 'b'),
                alphabet - setOf('m', 'b'),
                alphabet - setOf('m', 'b', 'o')
            )
        )

        assertEquals(
            GuessEvaluation("prune", listOf(Correct, Correct, Absent, Absent, Absent)).letterMask(),
            listOf(
                setOf('p'),
                setOf('r'),
                alphabet - setOf('u', 'n', 'e'),
                alphabet - setOf('u', 'n', 'e'),
                alphabet - setOf('u', 'n', 'e'),
            )
        )

        assertEquals(
            GuessEvaluation("slick", listOf(Present, Present, Absent, Present, Absent)).letterMask(),
            listOf(
                alphabet - setOf('s', 'i', 'k'),
                alphabet - setOf('l', 'i', 'k'),
                alphabet - setOf('i', 'k'),
                alphabet - setOf('c', 'i', 'k'),
                alphabet - setOf('i', 'k'),
            )
        )
    }

}