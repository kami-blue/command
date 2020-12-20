package org.kamiblue.command

import org.kamiblue.command.utils.ExecuteBlock
import org.kamiblue.command.utils.Invokable
import org.kamiblue.commons.interfaces.Alias
import org.kamiblue.commons.interfaces.Nameable

/**
 * Base of an Argument type, extends this to make new argument type
 *
 * @param T type of this argument
 */
abstract class AbstractArg<T : Any> : Nameable {

    /**
     * Type name of this argument type, used by [toString]
     */
    protected open val typeName = javaClass.simpleName.removeSuffix("Arg")

    /**
     * Argument tree for building up the arguments
     */
    protected val argTree = ArrayList<AbstractArg<*>>()

    /**
     * ID of this argument
     */
    val identifier by lazy { ArgIdentifier<T>(name) }

    /**
     * Get a immutable copy of [argTree]
     */
    fun getArgTree() = argTree.toList()

    /**
     * Check if [string] matches with this argument
     */
    internal suspend fun checkType(string: String?) = convertToType(string) != null

    /**
     * Convert [string] to the the argument type [T]
     */
    internal abstract suspend fun convertToType(string: String?): T?

    /**
     * Appends a new [AbstractArg], copy the [argTree]
     *
     * @param arg [AbstractArg] to append
     */
    fun <T : Any> append(arg: AbstractArg<T>): AbstractArg<T> {
        if (this is FinalArg<*>) {
            throw IllegalArgumentException("${this.javaClass.simpleName} can't be appended")
        }

        arg.argTree.addAll(this.argTree)
        arg.argTree.add(this)
        return arg
    }

    /**
     * Used for printing argument help
     */
    override fun toString(): String {
        return "<$name:${typeName}>"
    }

}

/**
 * An argument that take no input and has a [ExecuteBlock]
 *
 * @param description Description for this argument combination
 * @param block [ExecuteBlock] to run on invoking
 */
class FinalArg<E : IExecuteEvent>(
    private val description: String,
    private val block: ExecuteBlock<E>
) : AbstractArg<Unit>(), Invokable<E> {

    override val name: String
        get() = argTree.joinToString(".")

    override suspend fun convertToType(string: String?): Unit? {
        return if (string == null) Unit
        else null
    }

    /**
     * Check if [argsIn] matches with all arguments in [argTree]
     *
     * @return True if all matched
     */
    suspend fun checkArgs(argsIn: Array<String>): Boolean {
        val lastArgType = argTree.last()

        if (argsIn.size != argTree.size
            && !(argsIn.size - 1 == argTree.size && argsIn.last().isBlank())
            && !(argsIn.size > argTree.size && lastArgType is GreedyStringArg)
        ) return false

        return countArgs(argsIn) == argTree.size
    }

    /**
     * Count matched arguments in [argsIn]
     *
     * @return Number of matched arguments
     */
    suspend fun countArgs(argsIn: Array<String>): Int {
        var matched = 0

        for ((index, argType) in argTree.withIndex()) {
            val success = if (argType is GreedyStringArg) {
                matched++
                break
            } else {
                argType.checkType(argsIn.getOrNull(index))
            }

            if (success) matched++
            else break
        }

        return matched
    }

    /**
     * Maps arguments in the [event] and invoke the [block]
     */
    override suspend fun invoke(event: E) {
        event.mapArgs(argTree)
        block.invoke(event)
    }

    override fun toString(): String {
        return if (description.isNotBlank()) "- $description" else ""
    }

    fun printArgHelp(): String {
        return (argTree.first().name +
            argTree.subList(1, argTree.size).joinToString(" ", " ")).trimEnd()
    }

}

/**
 * Argument that takes a [Boolean] as input
 *
 * @param name Name of this argument
 */
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

/**
 * Argument that takes a [Enum] as input
 *
 * @param E Type of input [Enum]
 * @param name Name of this argument
 * @param enumClass Class of [E]
 */
class EnumArg<E : Enum<E>>(
    override val name: String,
    enumClass: Class<E>
) : AbstractArg<E>() {

    private val enumValues = enumClass.enumConstants

    override suspend fun convertToType(string: String?): E? {
        return enumValues.find { it.name.equals(string, true) }
    }

}

/**
 * Argument that takes a [Int] as input
 *
 * @param name Name of this argument
 */
class IntArg(
    override val name: String
) : AbstractArg<Int>() {

    override suspend fun convertToType(string: String?): Int? {
        return string?.toIntOrNull()
    }

}

/**
 * Argument that takes a [Long] as input
 *
 * @param name Name of this argument
 */
class LongArg(
    override val name: String
) : AbstractArg<Long>() {

    override suspend fun convertToType(string: String?): Long? {
        return string?.toLongOrNull()
    }

}

/**
 * Argument that takes a [Float] as input
 *
 * @param name Name of this argument
 */
class FloatArg(
    override val name: String
) : AbstractArg<Float>() {

    override suspend fun convertToType(string: String?): Float? {
        return string?.toFloatOrNull()
    }

}

/**
 * Argument that takes a [Double] as input
 *
 * @param name Name of this argument
 */
class DoubleArg(
    override val name: String
) : AbstractArg<Double>() {

    override suspend fun convertToType(string: String?): Double? {
        return string?.toDoubleOrNull()
    }

}

/**
 * Argument that takes a [String] as input, and must be
 * matched with [name] or one of the [alias]
 *
 * @param name Name of this argument
 * @param alias Alias of this literal argument
 */
open class LiteralArg(
    override val name: String,
    override val alias: Array<out String>,
) : AbstractArg<String>(), Alias, AutoComplete by StaticPrefixMatch(listOf(name, *alias)) {

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

/**
 * Argument that takes a [String] as input
 *
 * @param name Name of this argument
 */
class StringArg(
    override val name: String
) : AbstractArg<String>() {

    override suspend fun convertToType(string: String?): String? {
        return string
    }

}

/**
 * Argument that takes all [String] after as input
 *
 * @param name Name of this argument
 */
class GreedyStringArg(
    override val name: String
) : AbstractArg<String>() {

    override suspend fun convertToType(string: String?): String? {
        return string
    }

}