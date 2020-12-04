package org.kamiblue.command.utils

import org.kamiblue.command.AbstractArg
import org.kamiblue.command.ArgIdentifier

typealias ExecuteBlock<E> = suspend E.() -> Unit

typealias BuilderBlock<T> = AbstractArg<T>.(ArgIdentifier<T>) -> Unit