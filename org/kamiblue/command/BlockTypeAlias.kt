package org.kamiblue.command

typealias ExecuteBlock<E> = suspend E.() -> Unit

typealias BuilderBlock<T> = AbstractArg<T>.(ArgIdentifier<T>) -> Unit