package dev.vrba.wordlebot.service

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import dev.vrba.wordlebot.configuration.BotConfiguration
import org.springframework.stereotype.Service

@Service
class DiscordService(private val configuration: BotConfiguration) {

    private val client: WebhookClient = WebhookClient.withUrl(configuration.webhook)

    fun postWordleSolution(header: String, solution: String) {
        val embed = WebhookEmbedBuilder()
            .setDescription(solution)
            .setTitle(WebhookEmbed.EmbedTitle(header, "https://nytimes.com/wordle"))
            .setFooter(WebhookEmbed.EmbedFooter("Nerd shit like entropy distribution charts coming soon!", null))
            .build()

        client.send(embed)
    }
}