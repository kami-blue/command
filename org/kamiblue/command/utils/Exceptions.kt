package org.kamiblue.command.utils

import org.kamiblue.command.Command
import org.kamiblue.command.AbstractCommandManager

/**
 * Exception throws when no command is found in a [AbstractCommandManager]
 *
 * @see AbstractCommandManager.getCommand
 */
class CommandNotFoundException(string: String?) :
    Exception("No command found for: $string")

/**
 * Exception throws when no subcommand is found for a [Command]
 *
 * @see Command.invoke
 */
class SubCommandNotFoundException(args: Array<String>, val command: Command<*>) :
    Exception("No matching sub command found for args: \"${args.sliceArray(1 until args.size).joinToString(" ")}\"")