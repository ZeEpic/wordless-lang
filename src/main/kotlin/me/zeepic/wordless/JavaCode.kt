package me.zeepic.wordless

import kotlin.reflect.KClass

data class JavaCode(
    val code: String,
    val returnType: KClass<*>?,
    //val scope: Scope
)