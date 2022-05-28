package dev.vrba.wordlebot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
    runApplication<WordleBotApplication>(*args)
}
