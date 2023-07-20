package me.zeepic.wordless

import WordlessBaseVisitor
import java.io.File
import java.io.FileWriter
import java.lang.StringBuilder

private const val OUTPUT_FILE = "output.java"

class WordlessVisitor : WordlessBaseVisitor<JavaCode>() {

//    override fun visitProgram(ctx: WordlessParser.ProgramContext?): JavaCode {
//        File(OUTPUT_FILE).createNewFile()
//        val fileWriter = FileWriter(OUTPUT_FILE)
//        fileWriter.write(visitChildren(ctx).code)
//        fileWriter.close()
//        println("Done.")
//        return super.visitProgram(ctx)
//    }

    override fun visitLine(ctx: WordlessParser.LineContext?): JavaCode {
        val code = visitChildren(ctx)
        return JavaCode(code?.code + "\n", null)
    }

    override fun visitStatement(ctx: WordlessParser.StatementContext?): JavaCode {
        val code = visitChildren(ctx)
        return JavaCode(code?.code + ";", null)
    }

    override fun visitVariable(ctx: WordlessParser.VariableContext?): JavaCode {
        val expression = visitChildren(ctx?.expression())
        val variableName = ctx?.ID()?.text
        val returnType = expression.returnType
        require(returnType != null, ctx) {
            "Variable $variableName can't be assigned a value that doesn't return anything"
        }
        return JavaCode("$returnType$variableName = ${expression.code};", null)
    }

    override fun visitConstant(ctx: WordlessParser.ConstantContext?): JavaCode {
        if (ctx?.INT() != null) {
            return JavaCode(ctx.INT().text, Int::class)
        }
        if (ctx?.STRING() != null) {
            return JavaCode(ctx.STRING().text, String::class)
        }
        if (ctx?.BOOL() != null) {
            return JavaCode(ctx.BOOL().text, Boolean::class)
        }
        if (ctx?.FLOAT() != null) {
            return JavaCode(ctx.FLOAT().text, Double::class)
        }
        require(false) {
            "Unknown constant type"
        }
        return JavaCode("error", null)
    }

    override fun visitLoop(ctx: WordlessParser.LoopContext?): JavaCode {
        val condition = visitChildren(ctx?.expression()?.get(0))
        val list = visitChildren(ctx?.expression()?.get(1))
        val id = ctx?.ID()?.text ?: "it"
        val block = visitChildren(ctx?.block())!!
        val isWhileLoop = list == null

        require(condition != null || list != null, ctx) {
            "Loop must have a condition (while loop) or a list (for each loop)"
        }

        require(list?.returnType?.java?.isAssignableFrom(Iterable::class.java) == true || isWhileLoop, ctx) {
            "Loop list must be an iterable"
        }

        val whileTemplate = """
            |while (${condition?.code}) {
            |   ${block.code}
            |}
        """.trimMargin()
        val ifBreakTemplate = """
            |if (${condition?.code}) {
            |   break;
            |}
        """.trimIndent()
        val forLoopTemplate = """
            |for (int ${id}Index = 0; ${id}Index < ${list?.code}.size(); ${id}Index++) {
            |   ${if (condition != null) ifBreakTemplate else ""}
            |   var $id = ${list?.code}.get(${id}Index);
            |   ${block.code}
            |}
        """.trimMargin()
        val code = if (isWhileLoop) whileTemplate else forLoopTemplate
        return JavaCode(code, null)
    }

    override fun visitFunctionCall(ctx: WordlessParser.FunctionCallContext?): JavaCode {
        val arguments = ctx?.expression()?.map { visitChildren(it)?.code }
        val function = ctx?.ID()?.text
        val code = StringBuilder()
        code.append(function)
        code.append('(')
        arguments?.filterNotNull()?.forEach {
            code.append(it)
            code.append(", ")
        }
        code.deleteCharAt(code.length - 1) // Delete last comma
        code.append(')')
        return JavaCode(code.toString(), null)
    }
}