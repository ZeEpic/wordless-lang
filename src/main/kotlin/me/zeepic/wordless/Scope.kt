package me.zeepic.wordless

data class Scope(
    val variables: MutableList<JavaCode> = mutableListOf(),
)