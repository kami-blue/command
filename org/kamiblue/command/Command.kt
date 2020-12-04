package org.kamiblue.command

import org.kamiblue.command.utils.Invokable
import org.kamiblue.command.utils.SubCommandNotFoundException
import org.kamiblue.commons.interfaces.Alias
import org.kamiblue.commons.interfaces.Nameable

class Command<E : ExecuteEvent> internal constructor(
    override val name: String,
    override val alias: Array<out String>,
    val description: String,
    val finalArgs: Array<FinalArg<E>>,
) : Nameable, Alias, Invokable<E> {

    override suspend fun invoke(event: E) {
        finalArgs.firstOrNull { it.checkArgs(event.args) }?.invoke(event)
            ?: throw SubCommandNotFoundException(event.args, this)
    }

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