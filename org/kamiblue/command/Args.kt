package org.kamiblue.command

import org.kamiblue.commons.interfaces.Alias
import org.kamiblue.commons.interfaces.Nameable

abstract class AbstractArg<T : Any> : Nameable {

    private val typeName = javaClass.simpleName.removeSuffix("Arg")
    protected val argTree = ArrayList<AbstractArg<*>>()
    val identifier by lazy { ArgIdentifier<T>(name) }

    internal suspend fun checkType(string: String?) = convertToType(string) != null

    internal abstract suspend fun convertToType(string: String?): T?

    fun <T : Any> append(arg: AbstractArg<T>): AbstractArg<T> {
        if (this is FinalArg<*>) {
            throw IllegalArgumentException("${this.javaClass.simpleName} can't be appended")
        }

        arg.argTree.addAll(this.argTree)
        arg.argTree.add(this)
        return arg
    }

    override fun toString(): String {
        return "<$name:${typeName}>"
    }

}

class FinalArg<E : ExecuteEvent>(
    private val description: String,
    private val block: ExecuteBlock<E>
) : AbstractArg<Unit>(), Invokable<E> {

    override val name: String
        get() = argTree.joinToString(".")

    override suspend fun convertToType(string: String?): Unit? {
        return if (string == null) Unit
        else null
    }

    suspend fun checkArgs(argsIn: Array<String>): Boolean {
        val lastArgType = argTree.last()

        if (argsIn.size != argTree.size
            && !(argsIn.size - 1 == argTree.size && argsIn.last().isBlank())
            && !(argsIn.size > argTree.size && lastArgType is GreedyStringArg)
        ) return false

        var success = true

        for ((index, argType) in argTree.withIndex()) {
            if (argType is GreedyStringArg) {
                success = argType.checkType(argsIn.slice(index until argsIn.size).joinToString(" "))
                break
            } else {
                success = argType.checkType(argsIn.getOrNull(index))
            }

            if (!success) break
        }

        return success
    }

    override suspend fun invoke(event: E) {
        event.mapArgs(argTree)
        block.invoke(event)
    }

    override fun toString(): String {
        return if (description.isNotBlank()) "- $description" else ""
    }

    fun printArgHelp(): String {
        return argTree.subList(1, argTree.size).joinToString(" ")
    }

}

class BooleanArg(
    override val name: String
) : AbstractArg<Boolean>() {

    override suspend fun convertToType(string: String?): Boolean? {
        return string.toTrueOrNull() ?: string.toFalseOrNull()
    }

    private fun String?.toTrueOrNull() =
        if (this != null && (this.equals("true", true) || this.equals("on", true))) true
        else null

    private fun String?.toFalseOrNull() =
        if (this != null && (this.equals("false", true) || this.equals("off", true))) false
        else null

}

class EnumArg<E : Enum<E>>(
    override val name: String,
    enumClass: Class<E>
) : AbstractArg<E>() {

    private val enumValues = enumClass.enumConstants

    override suspend fun convertToType(string: String?): E? {
        return enumValues.find { it.name.equals(string, true) }
    }

}

class IntArg(
    override val name: String
) : AbstractArg<Int>() {

    override suspend fun convertToType(string: String?): Int? {
        return string?.toIntOrNull()
    }

}

class LongArg(
    override val name: String
) : AbstractArg<Long>() {

    override suspend fun convertToType(string: String?): Long? {
        return string?.toLongOrNull()
    }

}

class FloatArg(
    override val name: String
) : AbstractArg<Float>() {

    override suspend fun convertToType(string: String?): Float? {
        return string?.toFloatOrNull()
    }

}

class DoubleArg(
    override val name: String
) : AbstractArg<Double>() {

    override suspend fun convertToType(string: String?): Double? {
        return string?.toDoubleOrNull()
    }

}

open class LiteralArg(
    override val name: String,
    override val alias: Array<out String>,
) : AbstractArg<String>(), Alias {

    override suspend fun convertToType(string: String?): String? {
        return if (string.equals(name, true) || alias.any { string.equals(it, false) }) {
            string
        } else {
            null
        }
    }

    override fun toString(): String {
        return "[$name]"
    }

}

class StringArg(
    override val name: String
) : AbstractArg<String>() {

    override suspend fun convertToType(string: String?): String? {
        return string
    }

}

class GreedyStringArg(
    override val name: String
) : AbstractArg<String>() {

    override suspend fun convertToType(string: String?): String? {
        return string
    }

}