package dev.vrba.wordlebot.service

import dev.vrba.wordlebot.domain.GuessEvaluation
import dev.vrba.wordlebot.domain.LetterEvaluation
import dev.vrba.wordlebot.domain.evaluateGuess
import dev.vrba.wordlebot.domain.matchesEvaluations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.math.log2

@Service
class WordleSolverService(private val wordlistService: WordlistService) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.qualifiedName)

    @Scheduled(fixedRate = 24, timeUnit = TimeUnit.HOURS)
    fun solveWordleForToday() {
        val solution = wordlistService.getAnswerForToday()
        val wordlist = wordlistService.wordlist

        logger.info("Solving wordle $solution")
        logger.info("Total available words: ${wordlist.size}")

        val initial = wordlist to emptyList<GuessEvaluation>()
        val iterations = generateSequence(initial) { (wordlist, history) ->
            // This is just to save performance as the first guess is always the same
            val guess = if (history.isEmpty()) "tares" else findNextBestWord(wordlist)
            val evaluation = evaluateGuess(guess, solution)

            val evaluations = history + evaluation
            val prunedWordlist = pruneWordlist(wordlist, evaluations) - guess

            prunedWordlist to evaluations
        }
        .takeWhile { (wordlist, _) -> wordlist.isNotEmpty() }
        .last()
        .second + evaluateGuess(solution, solution)

        val header = "Wordle ${wordlistService.getCurrentWordleIndex()} ${iterations.size}/6"
        val result = iterations
            .joinToString("\n") { it.toString() }
            .replace("\uD83D\uDFE5", "⬛")

        println(header)
        println(result)
    }

    private fun pruneWordlist(wordlist: Set<String>, evaluations: List<GuessEvaluation>): Set<String> {
        return wordlist.filter { matchesEvaluations(it, evaluations) }.toSet()
    }

    private fun findNextBestWord(wordlist: Set<String>): String {
        return wordlist.maxByOrNull { computeWordEntropy(it, wordlist) }
            ?: throw IllegalStateException("The provided wordlist is empty!")
    }

    private fun computeWordEntropy(word: String, wordlist: Set<String>): Double {
        val patterns = wordlist
            .map { evaluateGuess(word, it).evaluation }
            .groupBy { it }
            .map { it.value.size }
            .sortedDescending()

        return patterns.sumOf {
            // E(x) = p(x) * log2(p(x))
            val p = it.toDouble() / wordlist.size
            val entropy = p * log2(1/p)

            entropy
        }
    }
}