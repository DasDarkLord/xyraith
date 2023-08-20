package parser

import lexer.Token
import lexer.TokenType
import java.lang.IndexOutOfBoundsException

class Parser(private val input: MutableList<Token>) {
    private var pointer = 0

    private fun nextToken(): Token {
        return try {
            input[pointer++]
        } catch (e: IndexOutOfBoundsException) {
            Token.EOF()
        }
    }
    private fun standardMatch(token: Token, type: TokenType) {
        if(token.toType() != type) {
            throw UnexpectedToken(token.toType(), type, token.spanStart, token.spanEnd)
        }
    }
    fun parseEvent(): Ast.Event? {
        if(nextToken().toType() == TokenType.EOF) {
            return null
        }
        pointer--
        standardMatch(nextToken(), TokenType.LeftParen)

        val eventToken = nextToken()
        standardMatch(eventToken, TokenType.Identifier)
        val nameToken = nextToken()
        standardMatch(nameToken, TokenType.Identifier)
        if(nameToken !is Token.Identifier) {
            throw Unreachable()
        }
        when(nameToken.value) {
            "join" -> {}
            "quit" -> {}
            else -> {
                throw InvalidEvent(nameToken.value, nameToken.spanStart, nameToken.spanEnd)
            }
        }
        val block = parseBlock(nameToken.value)
        val name = nameToken.value
        standardMatch(nextToken(), TokenType.RightParen)
        return Ast.Event(name, block)
    }

    fun parseAll(): List<Ast.Event> {
        val list: MutableList<Ast.Event> = mutableListOf()
        while(true) {
            val event = parseEvent()
            if(event != null ) {
                list.add(event)
            } else {
                break
            }
        }
        return list
    }

    private fun parseBlock(eventName: String): Ast.Block {
        standardMatch(nextToken(), TokenType.LeftParen)
        val list: MutableList<Ast.Command> = mutableListOf()
        while(nextToken() is Token.LeftParen) {
            pointer--
            list.add(parseCommand())
        }
        pointer--
        standardMatch(nextToken(), TokenType.RightParen)
        return Ast.Block(list, eventName)
    }

    private fun parseCommand(): Ast.Command {
        standardMatch(nextToken(), TokenType.LeftParen)
        val nameToken = nextToken()
        standardMatch(nameToken, TokenType.Identifier)
        if(nameToken !is Token.Identifier) {
            throw Unreachable()
        }

        val list: MutableList<Value> = mutableListOf()
        while(nextToken() !is Token.RightParen) {
            pointer--
            list.add(parseArgument())
        }
        pointer--
        standardMatch(nextToken(), TokenType.RightParen)
        verifyBuiltinCommand(nameToken, list)
        return Ast.Command(nameToken.value, list)
    }
    private fun parseArgument(): Value {
        when(val next = nextToken()) {
            is Token.At -> {
                val next2 = nextToken()
                if(next2 !is Token.Identifier) {
                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.spanStart, next2.spanEnd)
                }
                return Value.Selector(next2.value)
            }
            is Token.LeftParen -> {
                val next2 = nextToken()
                if(next2 is Token.LeftParen) {
                    pointer--
                    pointer--
                    return Value.Block(parseBlock("callable"))
                } else if(next2 is Token.Identifier) {
                    pointer--
                    pointer--
                    return Value.Command(parseCommand())
                } else {
                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.spanStart, next2.spanEnd)
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
            is Token.RightParen -> {
                throw Unreachable()
            }
            is Token.EOF -> {
                throw Unreachable()
            }
            is Token.Identifier -> {
                return Value.String(next.value)
            }
        }
    }
}