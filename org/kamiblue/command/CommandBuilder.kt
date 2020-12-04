package org.kamiblue.command

import org.kamiblue.command.utils.BuilderBlock
import org.kamiblue.command.utils.ExecuteBlock

@Suppress("UNUSED")
open class CommandBuilder<E : ExecuteEvent>(
    name: String,
    alias: Array<out String> = emptyArray(),
    private val description: String = "",
) : LiteralArg(name, alias) {

    protected val finalArgs = ArrayList<FinalArg<E>>()

    @CommandBuilder
    protected fun AbstractArg<*>.execute(
        description: String = "",
        block: ExecuteBlock<E>
    ) {
        val arg = FinalArg(description, block)
        this.append(arg)
        finalArgs.add(arg)
    }

    @CommandBuilder
    protected inline fun <reified E : Enum<E>> AbstractArg<*>.enum(
        name: String,
        noinline block: BuilderBlock<E>
    ) {
        arg(EnumArg(name, E::class.java), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.boolean(
        name: String,
        block: BuilderBlock<Boolean>
    ) {
        arg(BooleanArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.long(
        name: String,
        block: BuilderBlock<Long>
    ) {
        arg(LongArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.int(
        name: String,
        block: BuilderBlock<Int>
    ) {
        arg(IntArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.float(
        name: String,
        block: BuilderBlock<Float>
    ) {
        arg(FloatArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.double(
        name: String,
        block: BuilderBlock<Double>
    ) {
        arg(DoubleArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.literal(
        name: String,
        vararg alias: String,
        block: LiteralArg.() -> Unit
    ) {
        val arg = LiteralArg(name, alias)
        this.append(arg)
        arg.block()
    }

    @CommandBuilder
    protected fun AbstractArg<*>.string(
        name: String,
        block: BuilderBlock<String>
    ) {
        arg(StringArg(name), block)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.greedy(
        name: String,
        block: BuilderBlock<String>
    ) {
        arg(GreedyStringArg(name), block)
    }

    @CommandBuilder
    protected fun <T : Any> AbstractArg<*>.arg(
        arg: AbstractArg<T>,
        block: BuilderBlock<T>
    ) {
        this.append(arg)
        arg.block(arg.identifier)
    }

    @DslMarker
    protected annotation class CommandBuilder

    internal fun buildCommand(): Command<E> {
        return Command(name, alias, description, finalArgs.toTypedArray())
    }

}