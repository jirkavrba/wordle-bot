package dev.vrba.wordlebot.service

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import dev.vrba.wordlebot.configuration.BotConfiguration
import org.springframework.stereotype.Service

@Service
class DiscordService(configuration: BotConfiguration) {

    private val clients = configuration.webhook
        .split(";")
        .map { WebhookClient.withUrl(it) }

    fun postWordleSolution(header: String, solution: String) {
        val embed = WebhookEmbedBuilder()
            .setDescription(solution)
            .setTitle(WebhookEmbed.EmbedTitle(header, "https://nytimes.com/wordle"))
            .build()

        clients.forEach { it.send(embed) }
    }
}