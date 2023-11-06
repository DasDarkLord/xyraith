package parser

import error.*
import events
import functions
import lang.lexer.SpanData
import lang.lexer.Token
import lang.lexer.TokenType
import runtime.Value
import typechecker.ArgumentType
import java.sql.SQLIntegrityConstraintViolationException

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
        while(hasNext()) {
            val event = parseEvent()!!
            output.add(event)
        }
        return output
    }

    private fun parseEvent(): Ast.Event? {
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

                val block = parseBlock(nameToken.value, false)

                if(eventParenthesis) standardMatch(next(), TokenType.RightParen)
                return Ast.Event(nameToken.value, block, EventType.Event, nameToken.span)
            }
            "function", "entity" -> {
                standardMatch(nameToken, TokenType.Symbol)
                if(nameToken !is Token.Symbol) throw Unreachable()

                standardMatch(next(), TokenType.LeftParen)
                val accepts = mutableListOf<ArgumentType>()
                var returns: ArgumentType? = null
                var doingReturns = false
                while(true) {
                    val next = next()
                    if(next is Token.RightParen) {
                        if(doingReturns && returns != null) {
                            break
                        }
                    }
                    val txt = when (next) {
                        is Token.StringText -> next.value
                        is Token.Symbol -> next.value
                        is Token.Identifier -> next.value
                        is Token.RightParen -> ")"
                        else -> throw UnexpectedToken(TokenType.RightParen, next.toType(), next.span)
                    }
                    if(txt == "->") doingReturns = true
                    else {
                        if(!doingReturns)
                            accepts.add(ArgumentType(txt, listOf()))
                        if(doingReturns)
                            if(returns == null)
                                returns = ArgumentType(txt, listOf())
                            else
                                throw SQLIntegrityConstraintViolationException()
                    }
                    println("accepts: $accepts | returns: $returns")
                }

                val block = parseBlock(nameToken.value, true)

                if(eventParenthesis) standardMatch(next(), TokenType.RightParen)
                functions[nameToken.value] = Pair(accepts, returns!!)
                return Ast.Event(nameToken.value, block, EventType.Function(nameToken.value, accepts, returns), nameToken.span)
            }
            "struct" -> {
                standardMatch(nameToken, TokenType.Symbol)
                if(nameToken !is Token.Symbol) throw Unreachable()

                val block = parseBlock(nameToken.value, true)

                if(eventParenthesis) standardMatch(next(), TokenType.RightParen)
                return Ast.Event(":__struct_init_" + nameToken.value, block, EventType.Struct, nameToken.span)
            }
            else -> {
                println("WARNING: unknown value ${eventToken.value}")
                return null
            }
        }



    }

    private fun parseBlock(eventName: String, isFunction: Boolean): Ast.Block {
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
        if(isFunction) {
            return Ast.Block(commands, "function", openParen.span)
        }
        return Ast.Block(commands, eventName, openParen.span)
    }

    private fun parseCommand(): Ast.Command {
        val hasParens = peek() is Token.LeftParen
        if(hasParens)
            standardMatch(next(), TokenType.LeftParen)
        val nameToken = next()
        val commandName =
            when(nameToken) {
                is Token.Identifier -> nameToken.value
                is Token.Symbol -> "call"
                else -> throw UnexpectedToken(TokenType.Identifier, nameToken.toType(), nameToken.span)
            }
        val args = mutableListOf<Value>()
        val spans = mutableListOf<SpanData>()
        if(nameToken is Token.Symbol) {
            args.add(Value.Symbol(nameToken.value))
            spans.add(nameToken.span)
        }

        while(true) {
            if(!hasParens) if(peek(false) is Token.NewLine) break
            if(hasParens) if(peek() is Token.RightParen) break
            spans.add(peek().span)
            args.add(parseArgument())
        }
        if(hasParens)
            standardMatch(next(), TokenType.RightParen)
        return Ast.Command(commandName, args, nameToken.span, spans)
    }

    private fun parseArgument(): Value {
        when(val next = next(true)) {
            is Token.Code -> {
                TODO()
            }
            is Token.At -> {
                val next2 = next()
                if(next2 !is Token.Identifier) {
                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.span)
                }
                return Value.Selector(next2.value)
            }
            is Token.LeftParen -> {
                val next2 = peek(false)
                return if(next2 is Token.LeftParen || next2 is Token.NewLine) {
                    pointer--
                    Value.Block(parseBlock("callable", false))
                } else if(next2 is Token.Identifier || next2 is Token.Symbol) {
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
                throw UnexpectedToken(TokenType.Identifier, next.toType(), next.span)
            }
        }
    }
}