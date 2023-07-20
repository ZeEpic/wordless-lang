package me.zeepic.wordless

import org.antlr.v4.runtime.RuleContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract



@OptIn(ExperimentalContracts::class)
fun require(condition: Boolean, ctx: RuleContext?, message: () -> Any) {
    contract { returns() implies condition }
    if (!condition) {
        throw WordlessException(message().toString())
    }
}

class WordlessException(message: String) : Throwable(message)
