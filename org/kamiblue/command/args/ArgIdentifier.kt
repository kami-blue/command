package org.kamiblue.command.args

import org.kamiblue.commons.interfaces.Nameable

/**
 * The ID for an argument
 */
@Suppress("UNUSED")
data class ArgIdentifier<T : Any>(override val name: String) : Nameable
