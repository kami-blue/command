package org.kamiblue.command

open class ExecuteEvent(
    val commandManager: AbstractCommandManager<*>,
    val args: Array<String>
) {

    private val mappedArgs = HashMap<ArgIdentifier<*>, Any>()

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

    @Suppress("UNCHECKED_CAST")
    val <T : Any> ArgIdentifier<T>.value: T
        get() = mappedArgs[this] as T

}