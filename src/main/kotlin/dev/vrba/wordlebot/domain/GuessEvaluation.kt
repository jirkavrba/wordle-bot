package dev.vrba.wordlebot.domain

import kotlin.math.abs

data class GuessEvaluation(
    val guess: String,
    val evaluation: List<LetterEvaluation>
) {
    override fun toString(): String {
        return evaluation.joinToString("") { it.emoji }
    }

    /**
     * Provides a letter mask that can be quickly applied to check if the guess evaluation matches the evaluation or not,
     * for example, the mask for "GUESS", scored as 🟨🟥🟥🟥🟩 will result in a mask containing:
     * All letters except the letter 'G' for the first position
     * All letters for positions from 2 up to 4
     * Only the letter "S" for position 5
     */
    fun letterMask(): List<Set<Char>> {
        val alphabet = ('a' .. 'z').toSet()
        val zipped = guess.toList().zip(evaluation)

        // Letters that cannot appear inside the word
        val absent = zipped
            .filter { it.second == LetterEvaluation.Absent }
            .map { it.first }
            .toSet()

        return zipped.map {  (letter, evaluation) ->
            when (evaluation) {
                LetterEvaluation.Correct -> setOf(letter)
                LetterEvaluation.Present -> alphabet - absent - letter
                LetterEvaluation.Absent -> alphabet - absent
            }
        }
    }
}

fun matchesEvaluations(word: String, evaluations: List<GuessEvaluation>): Boolean {
    return evaluations.all {
        // For each evaluation, check, that the letter mask can be matched with this word
        return it.letterMask().zip(word.toList()).all { (letters, letter) -> letter in letters }
    }
}

fun evaluateGuess(guess: String, solution: String): GuessEvaluation {
    val zipped = guess.zip(solution)
    val letters = zipped
        .filter { (g, s) -> g != s }
        .map { (_, s) -> s }

    val initial = letters to emptyList<LetterEvaluation>()
    val (_, evaluation) = zipped.fold(initial) { (letters, evaluation), (guess, solution) ->
        when (guess) {
            solution -> letters to evaluation + LetterEvaluation.Correct
            in letters -> (letters - guess) to evaluation + LetterEvaluation.Present
            else -> letters to evaluation + LetterEvaluation.Absent
        }
    }

    return GuessEvaluation(guess, evaluation)
}