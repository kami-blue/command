package org.kamiblue.command

import org.kamiblue.command.utils.CommandNotFoundException
import org.kamiblue.commons.collections.AliasSet

/**
 * Manager for [Command] registration and execution
 *
 * @param E Type of [IExecuteEvent], can be itself or its subtype
 */
abstract class AbstractCommandManager<E : IExecuteEvent> {

    /**
     * Registered [Command] for this [AbstractCommandManager]
     */
    private val commands = AliasSet<Command<E>>()

    /**
     * Registered [CommandBuilder] to their built [Command]
     */
    private val builderCommandMap = HashMap<CommandBuilder<E>, Command<E>>()

    private val lockObject = Any()

    /**
     * Build [builder] and register it to this [AbstractCommandManager]
     *
     * @return The built [Command]
     */
    fun register(builder: CommandBuilder<E>): Command<E> {
        return synchronized(lockObject) {
            builder.buildCommand().also {
                commands.add(it)
                builderCommandMap[builder] = it
            }
        }
    }

    /**
     * Unregister the [Command] built from this [CommandBuilder]
     *
     * @return The unregistered [Command]
     */
    fun unregister(builder: CommandBuilder<E>): Command<E>? {
        return synchronized(lockObject) {
            builderCommandMap.remove(builder)?.also {
                commands.remove(it)
            }
        }
    }


    /**
     * Get all commands
     */
    fun getCommands() = commands.toSet()

    /**
     * Get command for [name]
     *
     * @throws CommandNotFoundException
     */
    fun getCommand(name: String) = commands[name] ?: throw CommandNotFoundException(name)

    /**
     * Get command for [name], or null if [name] is invalid
     */
    fun getCommandOrNull(name: String) = commands[name]


    /**
     * Invoke a command for [event]
     *
     * @throws IllegalArgumentException If [event]'s argument is empty
     * @throws CommandNotFoundException If no command found
     */
    open suspend fun invoke(event: E) {
        val name = event.args.getOrNull(0) ?: throw IllegalArgumentException("Arguments can not be empty!")
        getCommand(name).invoke(event)
    }

    /**
     * Parse [string] in to arguments ([Array] of [String])
     *
     * @throws IllegalArgumentException If [string] is blank or empty
     */
    fun parseArguments(string: String): Array<String> {
        if (string.isBlank()) {
            throw if (string.isEmpty()) IllegalArgumentException("Input can not be empty!")
            else IllegalArgumentException("Input can not be blank!")
        }

        return string
            .trim()
            .split(splitRegex)
            .map {
                it.removeSurrounding("\"")
                    .replace("''", "\"")
            }
            .toTypedArray()
    }

    private companion object {
        /**
         * Used by [parseArguments] to split the [String] into array of argument [String]
         */
        val splitRegex = " (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()
    }

}