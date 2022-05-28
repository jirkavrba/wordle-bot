package dev.vrba.wordlebot.service

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WordlistService {

    // Wordle started on 19th Jun 2021
    private val startDate = LocalDate.of(2021, 6, 19)

    val answers: List<String> = readResource("/wordle-answers.txt")

    val wordlist: List<String> = readResource("/wordle-wordlist.txt")

    private fun readResource(resource: String): List<String> {
        return this::class.java.getResource(resource)
            ?.readText()
            ?.lines()
            ?: throw IllegalArgumentException("Cannot read the resource [${resource}]")
    }

    /**
     * Provides the solution for today's wordle based on the wordlist, so I don't have to web scrape the site
     */
    fun getAnswerForToday(): String {
        val today = LocalDate.now()
        val days = today.toEpochDay() - startDate.toEpochDay()

        return answers[days.toInt()]
    }
}