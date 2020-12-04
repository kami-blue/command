package org.kamiblue.command.utils

import org.kamiblue.command.ExecuteEvent

/**
 * Interface for class that can be invoked with an [ExecuteEvent]
 *
 * @param E Type of [ExecuteEvent], can be itself or its subtype
 */
interface Invokable<E : ExecuteEvent> {

    /**
     * Invoke this with [event]
     */
    suspend fun invoke(event: E)

}