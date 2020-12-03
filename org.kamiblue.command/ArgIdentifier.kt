package org.kamiblue.command

import org.kamiblue.commons.interfaces.Nameable

@Suppress("UNUSED")
data class ArgIdentifier<T : Any>(override val name: String) : Nameable