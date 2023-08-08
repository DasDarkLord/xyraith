package parser

import lexer.Token
import lexer.TokenType

class Parser(val input: MutableList<Token>) {
    var pointer = 0

    fun parseEvent(): Ast.Event {
        if(input[pointer++] !is Token.LeftParen) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        val eventToken = input[pointer++]
        if(eventToken !is Token.Identifier) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        val nameToken = input[pointer++]
        if(nameToken !is Token.Identifier) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        val block = parseBlock()
        val name = nameToken.value
        if(input[pointer++] !is Token.RightParen) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        return Ast.Event(name, block)
    }

    fun parseBlock(): Ast.Block {
        if(input[pointer++] !is Token.LeftParen) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        if(input[pointer++] !is Token.RightParen) {
            throw UnexpectedToken(input[pointer].toType(), TokenType.LeftParen, input[pointer].spanStart, input[pointer].spanEnd)
        }
        return Ast.Block(listOf())
    }
}