package org.kamiblue.command

import org.kamiblue.commons.collections.AliasSet

abstract class AbstractCommandManager<T : ExecuteEvent> {

    private val commands = AliasSet<Command<T>>()

    /**
     * Build [CommandBuilder] in [builders] and register them to this [AbstractCommandManager]
     */
    fun registerAll(builders: Iterable<CommandBuilder<T>>) {
        builders.forEach { register(it) }
    }

    /**
     * Build [builder] and register it to this [AbstractCommandManager]
     *
     * @return The built [Command]
     */
    fun register(builder: CommandBuilder<T>): Command<T> {
        return builder.buildCommand().also {
            commands.add(it)
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
    open suspend fun invoke(event: T) {
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
        val splitRegex = " (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()
    }

}