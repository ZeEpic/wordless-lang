package me.zeepic.wordless

import WordlessLexer
import WordlessParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main() {
    // Load code from resource
    val code = CharStreams.fromFileName("C:\\Users\\isaol\\IdeaProjects\\wordless\\src\\main\\resources\\code.wl")
    val lexer = WordlessLexer(code)
    val stream = CommonTokenStream(lexer)
    val parser = WordlessParser(stream)
    val context = parser.program()
    val listener = WordlessVisitor()
    val output = listener.visitChildren(context)
    println(output)
    println(output.code)
}
