package dev.vrba.wordlebot.service

import club.minnced.discord.webhook.WebhookClient
import dev.vrba.wordlebot.configuration.BotConfiguration
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.markers.None
import org.springframework.stereotype.Service

@Service
class DiscordService(configuration: BotConfiguration) {

    private val clients = configuration.webhook
        .split(";")
        .map { WebhookClient.withUrl(it) }

    fun postWordleSolution(header: String, solution: String, distributions: List<List<Int>>, words: List<Int>) {
        val content = "$header\n\n$solution"

        val pruning = createPruningChart(words)
        val entropy = createDistributionChart(distributions)

        clients.forEach {
            it.send(content)
            it.send(pruning, "pruning.png")
            it.send(entropy, "entropy.png")
        }
    }

    private fun createPruningChart(words: List<Int>): ByteArray {
        val chart = XYChartBuilder()
            .xAxisTitle("Iteration")
            .yAxisTitle("Number of acceptable words")
            .width(600)
            .height(400)
            .theme(Styler.ChartTheme.GGPlot2)
            .build()

        chart.addSeries("Wordlist size", words).apply {
            marker = None()
            isSmooth = false
            xySeriesRenderStyle = XYSeries.XYSeriesRenderStyle.StepArea
        }

        return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG)
    }

    private fun createDistributionChart(distributions: List<List<Int>>): ByteArray {
        val chart = XYChartBuilder()
            .title("Entropy distribution after each iteration")
            .xAxisTitle("Number of patterns")
            .yAxisTitle("Number of occurrences")
            .width(600)
            .height(400)
            .theme(Styler.ChartTheme.GGPlot2)
            .build()

        distributions.forEachIndexed { index, distribution ->
            val max = distribution.maxOrNull()?.toDouble() ?: 1.0
            val values = distribution.map { it / max }

            chart.addSeries("Iteration #${index + 1}", values).apply {
                marker = None()
                isSmooth = true
                xySeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
            }
        }

        return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG)
    }
}