package org.kamiblue.command

interface Invokable<T : ExecuteEvent> {
    suspend fun invoke(event: T)
}