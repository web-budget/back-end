package br.com.webbudget.utilities

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender

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
