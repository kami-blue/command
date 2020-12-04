package org.kamiblue.command

import org.kamiblue.command.utils.Invokable
import org.kamiblue.command.utils.SubCommandNotFoundException
import org.kamiblue.commons.interfaces.Alias
import org.kamiblue.commons.interfaces.Nameable

/**
 * Command built from [CommandBuilder], this shouldn't be used
 * directly for instance creation in implementation.
 *
 * @param E Type of [ExecuteEvent], can be itself or its subtype
 * @param name Name of this [Command], used to call the [Command] or identifying
 * @param alias Alias of [Command], functions the same as [name]
 * @param description Description of this [Command]
 * @param finalArgs Possible argument combinations of this [Command]
 */
class Command<E : ExecuteEvent> internal constructor(
    override val name: String,
    override val alias: Array<out String>,
    val description: String,
    val finalArgs: Array<FinalArg<E>>,
) : Nameable, Alias, Invokable<E> {

    /**
     * Invoke this [Command] with [event].
     *
     * @param event Event being used for invoking, must match the type [E]
     *
     * @throws SubCommandNotFoundException if no sub command is found
     */
    override suspend fun invoke(event: E) {
        finalArgs.firstOrNull { it.checkArgs(event.args) }?.invoke(event)
            ?: throw SubCommandNotFoundException(event.args, this)
    }

    /**
     * Returns argument help for this [Command].
     */
    fun printArgHelp(): String {
        return finalArgs.joinToString("\n\n") {
            var argHelp = it.printArgHelp()
            val description = it.toString()

            if (argHelp.isBlank()) argHelp = "<No Argument>"
            if (description.isNotBlank()) argHelp += "\n    $it"
            (argHelp)
        }
    }

}