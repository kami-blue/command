package org.kamiblue.command.utils

import org.kamiblue.command.AbstractArg
import org.kamiblue.command.ArgIdentifier
import org.kamiblue.command.ExecuteEvent
import org.kamiblue.command.CommandBuilder

/**
 * Type alias for a block used for execution of a argument combination
 *
 * @param E Type of [ExecuteEvent], can be itself or its subtype
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