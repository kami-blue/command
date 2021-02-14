package org.kamiblue.command.utils

import org.kamiblue.command.CommandBuilder
import org.kamiblue.command.args.AbstractArg
import org.kamiblue.command.args.ArgIdentifier
import org.kamiblue.command.execute.IExecuteEvent

/**
 * Type alias for a block used for execution of a argument combination
 *
 * @param E Type of [IExecuteEvent], can be itself or its subtype
 *
 * @see CommandBuilder.execute
 */
typealias ExecuteBlock<E> = suspend E.() -> Unit

/**
 * Type alias for a block used for Argument building
 *
 * @param T Type of argument
 *
 * @see CommandBuilder
 */
typealias BuilderBlock<T> = AbstractArg<T>.(ArgIdentifier<T>) -> Unit
