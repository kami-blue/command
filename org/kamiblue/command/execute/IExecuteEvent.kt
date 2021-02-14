package org.kamiblue.command.execute

import org.kamiblue.command.AbstractCommandManager
import org.kamiblue.command.Command
import org.kamiblue.command.args.AbstractArg
import org.kamiblue.command.args.ArgIdentifier

/**
 * Event being used for executing the [Command]
 */
interface IExecuteEvent {

    val commandManager: AbstractCommandManager<*>

    /**
     * Parsed arguments
     */
    val args: Array<String>

    /**
     * Maps argument for the [argTree]
     */
    suspend fun mapArgs(argTree: List<AbstractArg<*>>)

    /**
     * Gets mapped value for an [ArgIdentifier]
     *
     * @throws NullPointerException If this [ArgIdentifier] isn't mapped
     */
    val <T : Any> ArgIdentifier<T>.value: T

}
