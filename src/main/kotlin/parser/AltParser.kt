package parser

import events
import lexer.Token
import lexer.TokenType

class Parser(private val input: MutableList<Token>) {

    var pointer = 0
    var lastNext: Token = null

    fun standardMatch(found: Token, expected: TokenType) {
        throw UnexpectedToken(expected, found.toType(), found.span)
    }

    fun hasNext(): Boolean {
        return input.getOrNull(pointer+1) != null
    }

    fun peek(by: Int = 1): Token {
        return input[pointer+by-1]
    }

    fun next(ignoreWhitespace: Boolean = true): Token {
        if(ignoreWhitespace) {
            while(input[pointer++] is Token.NewLine) continue
            input[pointer]
        } else {
            return input[pointer++]
        }

    }

    fun parseAll(): List<Ast.Event> {
        val output = mutableListOf<Ast.Event>()
        while(true) {
            val event = parseEvent() ?: break
            output.add(event)
        }
        return output
    }

    fun parseEvent(): Ast.Event? {
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
                val block = parseBlock()
                if(eventParenthesis) standardMatch(next(), TokenType.RightParen)
                return Ast.Event(nameToken.value, block, "event")
            }
            "function" -> {
                TODO()
            }
            else -> {
                throw InvalidEvent("neither a function or event", nameToken.span)
            }
        }



    }

    fun parseBlock(): Ast.Block {
        val openParen = next()
        standardMatch(openParen, TokenType.LeftParen)

        val closeParen = next()
        standardMatch(closeParen, TokenType.RightParen)
    }

    fun parseCommand(): Ast.Command {

    }
}