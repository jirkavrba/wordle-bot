package dev.vrba.wordlebot.service

import dev.vrba.wordlebot.configuration.BotConfiguration
import dev.vrba.wordlebot.domain.GuessEvaluation
import dev.vrba.wordlebot.domain.evaluateGuess
import dev.vrba.wordlebot.domain.matchesEvaluations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.math.log2

@Service
class WordleSolverService(
    private val wordlistService: WordlistService,
    private val discordService: DiscordService,
    private val configuration: BotConfiguration
) : CommandLineRunner{

    private val logger: Logger = LoggerFactory.getLogger(this::class.qualifiedName)

    @Scheduled(cron = "0 0 8 * * *")
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
        .toList()

        val header = "Wordle ${wordlistService.getCurrentWordleIndex()} ${iterations.size}/6"
        val evaluations = iterations.last().second + evaluateGuess(solution, solution)
        val result = evaluations
            .joinToString("\n") { it.toString() }
            .replace("\uD83D\uDFE5", "⬛")

        logger.info("Found solution in ${iterations.size + 1} iterations")
        discordService.postWordleSolution(header, result)
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

    override fun run(vararg args: String?) {
        if (configuration.solveOnStartup) {
            solveWordleForToday()
        }
    }
}