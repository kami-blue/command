package org.kamiblue.command

/**
 * Event being used for executing the [Command]
 *
 * @param commandManager Reference to the [AbstractCommandManager]
 * @param args Parsed arguments
 */
open class ExecuteEvent(
    val commandManager: AbstractCommandManager<*>,
    val args: Array<String>
) {

    /**
     * Mapping [ArgIdentifier] to their converted arguments
     */
    private val mappedArgs = HashMap<ArgIdentifier<*>, Any>()

    /**
     * Maps argument for the [argTree]
     */
    suspend fun mapArgs(argTree: List<AbstractArg<*>>) {
        for ((index, arg) in argTree.withIndex()) {
            if (arg is GreedyStringArg) {
                arg.convertToType(args.slice(index until args.size).joinToString(" "))?.let {
                    mappedArgs[arg.identifier] = it
                }
                break
            } else {
                arg.convertToType(args.getOrNull(index))?.let {
                    mappedArgs[arg.identifier] = it
                }
            }
        }
    }

    /**
     * Gets mapped value for an [ArgIdentifier]
     *
     * @throws NullPointerException If this [ArgIdentifier] isn't mapped
     */
    @Suppress("UNCHECKED_CAST")
    val <T : Any> ArgIdentifier<T>.value: T
        get() = mappedArgs[this] as T

}