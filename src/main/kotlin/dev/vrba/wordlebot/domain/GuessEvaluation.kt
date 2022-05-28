package dev.vrba.wordlebot.domain

data class GuessEvaluation(
    val guess: String,
    val evaluation: List<LetterEvaluation>
) {
    override fun toString(): String {
        return evaluation.joinToString("") { it.emoji }
    }
}

fun evaluateGuess(guess: String, solution: String): GuessEvaluation {
    val zipped = guess.zip(solution)
    val letters = zipped
        .filter { (g, s) -> g != s }
        .map { (g, _) -> g }

    val initial = letters to emptyList<LetterEvaluation>()
    val (_, evaluation) = zipped.fold(initial) { (letters, evaluation), (guess, solution) ->
        when (guess) {
            solution -> letters to evaluation + LetterEvaluation.Correct
            in letters -> letters - guess to evaluation + LetterEvaluation.Present
            else -> letters to evaluation + LetterEvaluation.Absent
        }
    }

    return GuessEvaluation(guess, evaluation)
}