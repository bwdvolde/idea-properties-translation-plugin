package com.bwdvolde.ideapropertiestranslation

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.messages"

object MyBundle : AbstractBundle(BUNDLE) {

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) = getMessage(key, *params)
}
