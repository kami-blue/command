package org.kamiblue.command.utils

import org.kamiblue.command.Command

class CommandNotFoundException(string: String?) :
    Exception("No command found for: $string")

class SubCommandNotFoundException(args: Array<String>, val command: Command<*>) :
    Exception("No matching sub command found for args: \"${args.sliceArray(1 until args.size).joinToString(" ")}\"")