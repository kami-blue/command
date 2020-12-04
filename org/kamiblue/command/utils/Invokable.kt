package org.kamiblue.command.utils

import org.kamiblue.command.ExecuteEvent

interface Invokable<T : ExecuteEvent> {
    suspend fun invoke(event: T)
}