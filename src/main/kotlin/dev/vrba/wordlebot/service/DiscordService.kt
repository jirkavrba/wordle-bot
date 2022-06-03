package dev.vrba.wordlebot.service

import club.minnced.discord.webhook.WebhookClient
import dev.vrba.wordlebot.configuration.BotConfiguration
import dev.vrba.wordlebot.domain.GuessEvaluation
import org.springframework.stereotype.Service
import java.io.File

@Service
class DiscordService(configuration: BotConfiguration) {

    private val clients = configuration.webhook
        .split(";")
        .map { WebhookClient.withUrl(it) }

    fun postWordleSolution(index: Int, solution: List<GuessEvaluation>) {
        val header = "Wordle $index ${solution.size}/6"
        val video = renderSolutionVideo(index, solution)

        clients.forEach {
            it.send(header)
            it.send(video, "wordle_$index.mp4")
        }

        // Delete all previously rendered videos
        Runtime.getRuntime().exec("rm -rf /app/render/out/*.mp4")
    }

    private fun renderSolutionVideo(index: Int, solution: List<GuessEvaluation>): ByteArray {
        // Build options passed to the js video renderer
        val evaluation = solution.flatMap { it.evaluation }.joinToString("") { it.symbol }
        val props = """{"index": $index, "evaluation": "$evaluation"}"""

        ProcessBuilder()
            .directory(File("/app/render"))
            .command(listOf("bash", "-l", "-c", "npm run build --props '$props'"))
            .start()
            .waitFor()

        return File("/app/render/out/video.mp4")
            .inputStream()
            .readAllBytes()
    }
}