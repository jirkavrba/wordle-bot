package dev.vrba.wordlebot

import dev.vrba.wordlebot.configuration.BotConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(BotConfiguration::class)
class WordleBotApplication

fun main(args: Array<String>) {
    runApplication<WordleBotApplication>(*args)
}
