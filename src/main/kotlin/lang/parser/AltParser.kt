package parser

import error.*
import events
import functions
import lang.lexer.SpanData
import lang.lexer.Token
import runtime.Value
import typechecker.ArgumentType
import java.sql.SQLIntegrityConstraintViolationException

class Parser(private val input: MutableList<Token>) {

    var pointer = -1
    var lastNext: Token = Token.Identifier("lkhdaskjld", SpanData(0, 0, "somethign went wrong fhere"))

    fun hasNext(): Boolean {
        return input.getOrNull(pointer+1) != null
    }

    private fun peek(ignoreWhitespace: Boolean = true): Token {
        var b = pointer
        if(ignoreWhitespace) {
            if(pointer > 0) {
                val startTok = input[b]
                if(b+1 > input.size) throw UnexpectedEOF(startTok.span)
            }

            var p = input[++b]
            while(p is Token.NewLine) {
                if(b+1 > input.size) throw UnexpectedEOF(p.span)
                p = input[++b]
            }

            return p
        } else {
            val startTok = input[b]
            if(b+1 >= input.size) throw UnexpectedEOF(startTok.span)
            return input[++b]
        }

    }

    fun next(ignoreWhitespace: Boolean = true): Token {
        if(ignoreWhitespace) {
            if(pointer > 0) {
                val startTok = input[pointer]
                if(pointer+1 > input.size) throw UnexpectedEOF(startTok.span)
            }
            var p = input[++pointer]
            while(p is Token.NewLine) {
                if(pointer+1 > input.size) throw UnexpectedEOF(p.span)
                p = input[++pointer]
            }
            return p
        } else {
            val startTok = input[pointer]
            if(pointer+1 > input.size) throw UnexpectedEOF(startTok.span)
            return input[++pointer]
        }
    }

    fun parseAll(): List<Ast.Event> {
        val output = mutableListOf<Ast.Event>()
        return output
    }
}