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

        val initial = Triple(wordlist, emptyList<GuessEvaluation>(), emptyList<List<Int>?>())
        val iterations = generateSequence(initial) { (wordlist, history, distributions) ->
            // This is just to save performance as the first guess is always the same
            val (guess, distribution) = if (history.isEmpty()) "tares" to null
                                        else findNextBestWord(wordlist)

            val evaluation = evaluateGuess(guess, solution)

            logger.info("Tried $guess -> $evaluation")

            val evaluations = history + evaluation
            val prunedWordlist = pruneWordlist(wordlist, evaluations) - guess

            Triple(prunedWordlist, evaluations, distributions + listOf(distribution))
        }
        .takeWhile { (wordlist, _, _) -> wordlist.isNotEmpty() }
        .toList()

        val header = "Wordle ${wordlistService.getCurrentWordleIndex()} ${iterations.size}/6"
        val result = (iterations.last().second + evaluateGuess(solution, solution))
            .joinToString("\n") { it.toString() }
            .replace("\uD83D\uDFE5", "⬛")

        // Collect entropy distributions so I can make fancy charts
        val distributions = iterations
            .flatMap { it.third }
            .filterNotNull()

        val words = iterations.map { it.first.size }

        logger.info("Solved wordle after ${words.size} iterations")

        discordService.postWordleSolution(header, result, distributions, words)
    }

    private fun pruneWordlist(wordlist: Set<String>, evaluations: List<GuessEvaluation>): Set<String> {
        return wordlist.filter { matchesEvaluations(it, evaluations) }.toSet()
    }

    private fun findNextBestWord(wordlist: Set<String>): Pair<String, List<Int>> {
        return wordlist
            .map { word ->
                val distribution = wordlist
                    .map { evaluateGuess(word, it).evaluation }
                    .groupBy { it }
                    .map { it.value.size }
                    .sortedDescending()

                word to distribution
            }
            .maxByOrNull { (_, distribution) ->
                distribution.sumOf {
                    val p = it.toDouble() / wordlist.size
                    val entropy = p * log2(1/p)

                    entropy
                }
            }
            ?: throw IllegalStateException("The provided wordlist is empty!")
    }

    override fun run(vararg args: String?) {
        if (configuration.solveOnStartup) {
            solveWordleForToday()
        }
    }
}