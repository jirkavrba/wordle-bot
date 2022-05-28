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
            .yAxisTitle("Number of available words")
            .width(1200)
            .height(600)
            .theme(Styler.ChartTheme.Matlab)
            .build()

        chart.addSeries("Number of available words", words).apply {
            marker = None()
            xySeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
        }

        return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG)
    }

    private fun createDistributionChart(distributions: List<List<Int>>): ByteArray {
        val chart = XYChartBuilder()
            .title("Entropy distribution after each iteration")
            .xAxisTitle("Number of patterns")
            .yAxisTitle("Number of occurrences")
            .width(1200)
            .height(600)
            .theme(Styler.ChartTheme.Matlab)
            .build()

        chart.styler.isYAxisLogarithmic = true
        distributions.forEachIndexed { index, distribution ->
            chart.addSeries("Iteration #${index + 1}", distribution).apply {
                marker = None()
                xySeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
            }
        }

        return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG)
    }
}