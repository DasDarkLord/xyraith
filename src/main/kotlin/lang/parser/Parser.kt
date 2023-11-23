package lang.parser

import error.*
import lang.lexer.SpanData
import lang.lexer.Token
import parser.Ast
import parser.EventType
import runtime.Value
import typechecker.ArgumentType

class Parser(private val input: MutableList<Token>) {
    val globalVariables: MutableList<String> = mutableListOf()
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
            val ptr = ++pointer
            if(ptr > input.size-1) {
                throw UnexpectedEOF(input.last().span)
            }
            var p = input[ptr]
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
        while(true) {
            val header = parseHeader()
            if(header.first && header.second != null)
                output.add(header.second!!)
            else if(!header.first)
                break
        }
        return output
    }

    private fun parseHeader(): Pair<Boolean, Ast.Event?> {
        when(val front = next()) {
            is Token.GlobalKeyword -> {
                val identifier = next()
                if(identifier !is Token.Identifier) {
                    throw UnexpectedToken("identifier", identifier, identifier.span)
                }
                globalVariables.add(identifier.value)
                return Pair(true, null)
            }
            is Token.EventKeyword -> {
                val eventNameToken = next()
                if(eventNameToken !is Token.Identifier) {
                    throw UnexpectedToken("identifier", eventNameToken, eventNameToken.span)
                }
                return Pair(true,
                    Ast.Event(
                        eventNameToken.value,
                        parseBlock(eventNameToken.value),
                        EventType.Event,
                        eventNameToken.span
                    )
                )
            }
            is Token.FunctionKeyword -> {
                val functionNameToken = next()
                if(functionNameToken !is Token.Identifier) {
                    throw UnexpectedToken("identifier", functionNameToken, functionNameToken.span)
                }
                return Pair(true, Ast.Event(
                    functionNameToken.value,
                    parseBlock(functionNameToken.value),
                    EventType.Function(
                        functionNameToken.value,
                        listOf(),
                        ArgumentType.NONE
                    ),
                    functionNameToken.span
                ))
            }
            is Token.StructKeyword -> {
                TODO()
            }
            is Token.EOF -> {
                return Pair(false, null)
            }
            else -> throw UnexpectedToken("valid event keyword", front, front.span)
        }
    }

    private fun parseBlock(eventName: String?): Ast.Block {
        val openBrace = next()
        if(openBrace !is Token.LeftParen) {
            throw UnexpectedToken("opening brace", openBrace, openBrace.span)
        }
        val closeBrace = next()
        if(closeBrace !is Token.RightParen) {
            throw UnexpectedToken("closing brace", closeBrace, closeBrace.span)
        }
        return Ast.Block(listOf(), eventName ?: "callable", openBrace.span)
    }

    private fun parseCommand(): Ast.Command {
        TODO()
    }

    private fun parseValue(): Value {
        val next = next()
        return when(next) {
            is Token.StringText -> Value.String(next.value)
            is Token.Number -> Value.Number(next.value)
            is Token.LeftParen -> Value.Command(parseCommand())
            is Token.Identifier -> when(next.value) {
                "true" -> Value.Command(Ast.Command("true", mutableListOf(), next.span, mutableListOf()))
                else -> throw UnexpectedToken("valid identifier", next, next.span)
            }
            else -> {
                throw UnexpectedToken("valid value", next, next.span)
            }
        }
    }
}

