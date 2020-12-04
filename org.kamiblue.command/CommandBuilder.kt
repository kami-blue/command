package org.kamiblue.command

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
        block: EnumArg<E>.(ArgIdentifier<E>) -> Unit
    ) {
        val arg = EnumArg(name, E::class.java)
        this.append(arg)
        arg.block(arg.identifier)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.boolean(
        name: String,
        block: BooleanArg.(ArgIdentifier<Boolean>) -> Unit
    ) {
        val arg = BooleanArg(name)
        this.append(arg)
        arg.block(arg.identifier)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.int(
        name: String,
        block: IntArg.(ArgIdentifier<Int>) -> Unit
    ) {
        val builder = IntArg(name)
        this.append(builder)
        builder.block(builder.identifier)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.float(
        name: String,
        block: FloatArg.(ArgIdentifier<Float>) -> Unit
    ) {
        val arg = FloatArg(name)
        this.append(arg)
        arg.block(arg.identifier)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.double(
        name: String,
        block: DoubleArg.(ArgIdentifier<Double>) -> Unit
    ) {
        val arg = DoubleArg(name)
        this.append(arg)
        arg.block(arg.identifier)
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
        block: StringArg.(ArgIdentifier<String>) -> Unit
    ) {
        val arg = StringArg(name)
        this.append(arg)
        arg.block(arg.identifier)
    }

    @CommandBuilder
    protected fun AbstractArg<*>.greedy(
        name: String,
        block: GreedyStringArg.(ArgIdentifier<String>) -> Unit
    ) {
        val arg = GreedyStringArg(name)
        this.append(arg)
        arg.block(arg.identifier)
    }

    @DslMarker
    protected annotation class CommandBuilder

    internal fun buildCommand(): Command<E> {
        return Command(name, alias, description, finalArgs.toTypedArray())
    }

}