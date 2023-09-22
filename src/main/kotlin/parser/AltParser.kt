package parser

import events
import lexer.SpanData
import lexer.Token
import lexer.TokenType
import java.lang.Exception
import kotlin.math.exp

class Parser(private val input: MutableList<Token>) {

    var pointer = -1
    var lastNext: Token = Token.Identifier("lkhdaskjld", SpanData(0, 0, "somethign went wrong fhere"))

    private fun standardMatch(found: Token, expected: TokenType) {
        if(found.toType() != expected) {
            throw UnexpectedToken(expected, found.toType(), found.span)
        }
    }

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
            if(b+1 > input.size) throw UnexpectedEOF(startTok.span)
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
        while(!hasNext()) {
            val event = parseEvent()!!
            output.add(event)
        }
        return output
    }

    private fun parseEvent(): Ast.Event? {
        println("parsing event...")
        val eventParenthesis = peek() is Token.LeftParen
        if(eventParenthesis) standardMatch(next(), TokenType.LeftParen)
        val eventToken = next()
        val nameToken = next()

        standardMatch(eventToken, TokenType.Identifier)
        if(eventToken !is Token.Identifier) throw Unreachable()
        when(eventToken.value) {
            "event" -> {
                standardMatch(nameToken, TokenType.Identifier)
                if(nameToken !is Token.Identifier) throw Unreachable()
                if(!events.containsKey(nameToken.value)) throw InvalidEvent(nameToken.value, nameToken.span)

                val block = parseBlock(nameToken.value)

                if(eventParenthesis) standardMatch(next(), TokenType.RightParen)
                return Ast.Event(nameToken.value, block, "event")
            }
            "function" -> {
                TODO()
            }
            else -> {
                println("WARNING: unknown value ${eventToken.value}")
                return null
            }
        }



    }

    private fun parseBlock(eventName: String): Ast.Block {
        val openParen = next()
        standardMatch(openParen, TokenType.LeftParen)
        val commands = mutableListOf<Ast.Command>()
        while(true) {
            if(peek() is Token.RightParen) break
            val command = parseCommand()
            commands.add(command)
        }
        val closeParen = next()
        standardMatch(closeParen, TokenType.RightParen)
        return Ast.Block(commands, eventName)
    }

    private fun parseCommand(): Ast.Command {
        val hasParens = peek() is Token.LeftParen
        if(hasParens)
            standardMatch(next(), TokenType.LeftParen)
        val nameToken = next()
        standardMatch(nameToken, TokenType.Identifier)
        if(nameToken !is Token.Identifier) throw Unreachable()
        val args = mutableListOf<Value>()
        while(true) {
            if(!hasParens) if(peek(false) is Token.NewLine) break
            if(hasParens) if(peek() is Token.RightParen) break
            args.add(parseArgument())
        }
        if(hasParens)
            standardMatch(next(), TokenType.RightParen)

        verifyBuiltinCommand(nameToken, args)
        return Ast.Command(nameToken.value, args)
    }

    private fun parseArgument(): Value {
        when(val next = next()) {
            is Token.At -> {
                val next2 = next()
                if(next2 !is Token.Identifier) {
                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.span)
                }
                return Value.Selector(next2.value)
            }
            is Token.LeftParen -> {
                val next2 = peek()
                return if(next2 is Token.LeftParen) {
                    pointer--
                    Value.Block(parseBlock("callable"))
                } else if(next2 is Token.Identifier) {
                    pointer--
                    Value.Command(parseCommand())
                } else {
                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.span)
                }
            }
            is Token.StringText -> {
                return Value.String(next.value)
            }
            is Token.Symbol -> {
                return Value.Symbol(next.value)
            }
            is Token.Number -> {
                return Value.Number(next.value)
            }
            is Token.Identifier -> {
                return Value.String(next.value)
            }
            else -> {
                throw Unreachable()
            }
        }
    }
}