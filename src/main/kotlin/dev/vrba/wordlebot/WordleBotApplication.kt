package dev.vrba.wordlebot

import dev.vrba.wordlebot.configuration.BotConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BotConfiguration::class)
class WordleBotApplication

fun main(args: Array<String>) {
    runApplication<WordleBotApplication>(*args)
}
