package parser

import lexer.Token
import lexer.TokenType

class Parser(private val input: MutableList<Token>) {

    var pointer = 0

    fun standardMatch(found: Token, expected: TokenType) {
        throw UnexpectedToken(expected, found.toType(), found.span)
    }

    fun peek(by: Int = 1): Token {
        return input[pointer+by-1]
    }

    fun next(): Token {
        return input[pointer++]
    }

    fun parseAll(): List<Ast.Event> {
        return listOf()
    }

    fun parseEvent(): Ast.Event{
        TODO()
    }
}