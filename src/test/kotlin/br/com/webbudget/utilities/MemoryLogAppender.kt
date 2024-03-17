package br.com.webbudget.utilities

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

lateinit var memoryLogAppender: MemoryLogAppender

fun startMemoryLogAppender(clearBeforeStart: Boolean = true) {
    memoryLogAppender = MemoryLogAppender()
    memoryLogAppender.context = LoggerFactory.getILoggerFactory() as LoggerContext

    val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    root.addAppender(memoryLogAppender)

    memoryLogAppender.start()

    if (clearBeforeStart) {
        memoryLogAppender.clear()
    }
}

fun stopMemoryLogAppender() {
    memoryLogAppender.stop()
}

class MemoryLogAppender : ListAppender<ILoggingEvent>() {

    fun size() = this.list.size

    fun clear() {
        this.list.clear()
    }

    fun countBy(message: String) = this.list
        .count { it.toString().contains(message) }

    fun find(message: String) = this.list
        .filter { it.toString().contains(message) }
        .toList()

    fun find(message: String, level: Level) = this.list
        .filter { it.level == level }
        .filter { it.toString().contains(message) }
        .toList()
}