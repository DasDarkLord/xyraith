//package parser
//
//import events
//import lexer.Token
//import lexer.TokenType
//import java.lang.Exception
//import java.lang.IndexOutOfBoundsException
//
//class Parser(private val input: MutableList<Token>) {
//    private var pointer = 0
//
//    private fun nextToken(): Token {
//        return try {
//            input[pointer++]
//        } catch (e: IndexOutOfBoundsException) {
//            Token.EOF()
//        }
//    }
//    private fun nextTokenWhitespaceless(): Token {
//        return try {
//            var tok = input[pointer++]
//            while(tok is Token.NewLine) {
//                tok = input[pointer++]
//            }
//            tok
//        } catch (e: IndexOutOfBoundsException) {
//            Token.EOF()
//        }
//    }
//    private fun standardMatch(token: Token, type: TokenType) {
//        if(token.toType() != type) {
//            throw UnexpectedToken(type, token.toType(), token.spanStart, token.spanEnd)
//        }
//    }
//    fun parseEvent(): Ast.Event? {
//        if(nextToken().toType() == TokenType.EOF) {
//            return null
//        }
//        pointer--
//
//        val eventToken = nextToken()
//        standardMatch(eventToken, TokenType.Identifier)
//        if(eventToken !is Token.Identifier) {
//            throw Unreachable()
//        }
//        val nameToken = nextToken()
//        if(eventToken.value == "func") {
//            standardMatch(nameToken, TokenType.Symbol)
//            if(nameToken !is Token.Symbol) {
//                throw Unreachable()
//            }
//        } else {
//            standardMatch(nameToken, TokenType.Identifier)
//            if(nameToken !is Token.Identifier) {
//                throw Unreachable()
//            }
//        }
//
//        when (nameToken) {
//            is Token.Identifier -> {
//                if(!events.containsKey(nameToken.value)) {
//                    throw InvalidEvent(nameToken.value, nameToken.spanStart, nameToken.spanEnd)
//                }
//                val block = parseBlock(nameToken.value)
//                val name = nameToken.value
//                return Ast.Event(name, block, "event")
//            }
//
//            is Token.Symbol -> {
//                val block = parseBlock(nameToken.value)
//                val name = nameToken.value
//                return Ast.Event(name, block, "func")
//            }
//
//            else -> {
//                throw Unreachable()
//            }
//        }
//
//    }
//
//    fun parseAll(): List<Ast.Event> {
//        val list: MutableList<Ast.Event> = mutableListOf()
//        while(true) {
//            val event = parseEvent()
//            if(event != null ) {
//                list.add(event)
//            } else {
//                break
//            }
//        }
//        return list
//    }
//
//    private fun parseBlock(eventName: String): Ast.Block {
//        standardMatch(nextTokenWhitespaceless(), TokenType.LeftParen)
//        val list: MutableList<Ast.Command> = mutableListOf()
//        while(nextTokenWhitespaceless() !is Token.RightParen) {
//            pointer--
//            list.add(parseCommand())
//        }
//        pointer--
//        standardMatch(nextTokenWhitespaceless(), TokenType.RightParen)
//        return Ast.Block(list, eventName)
//    }
//
//    private fun parseCommand(): Ast.Command {
//        var hasParens = true
//        try {
//            standardMatch(nextTokenWhitespaceless(), TokenType.LeftParen)
//        } catch(exc: Exception) {
//            hasParens = false
//            pointer--
//        }
//
//        val nameToken = nextTokenWhitespaceless()
//        standardMatch(nameToken, TokenType.Identifier)
//        if(nameToken !is Token.Identifier) {
//            throw Unreachable()
//        }
//
//        val list: MutableList<Value> = mutableListOf()
//        var next = nextTokenWhitespaceless()
//        while(true) {
//            if(hasParens) {
//                if(next is Token.RightParen) break else { pointer--; list.add(parseArgument()) }
//            } else {
//                if(next is Token.NewLine) break else { pointer--; list.add(parseArgument()) }
//            }
//            next = nextTokenWhitespaceless()
//        }
//        verifyBuiltinCommand(nameToken, list)
//        return Ast.Command(nameToken.value, list)
//    }
//    private fun parseArgument(): Value {
//        when(val next = nextTokenWhitespaceless()) {
//            is Token.At -> {
//                val next2 = nextToken()
//                if(next2 !is Token.Identifier) {
//                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.spanStart, next2.spanEnd)
//                }
//                return Value.Selector(next2.value)
//            }
//            is Token.LeftParen -> {
//                val next2 = nextToken()
//                return if(next2 is Token.LeftParen) {
//                    pointer--
//                    pointer--
//                    Value.Block(parseBlock("callable"))
//                } else if(next2 is Token.Identifier) {
//                    pointer--
//                    pointer--
//                    Value.Command(parseCommand())
//                } else {
//                    throw UnexpectedToken(TokenType.Identifier, next2.toType(), next2.spanStart, next2.spanEnd)
//                }
//            }
//            is Token.StringText -> {
//                return Value.String(next.value)
//            }
//            is Token.Symbol -> {
//                return Value.Symbol(next.value)
//            }
//            is Token.Number -> {
//                return Value.Number(next.value)
//            }
//            is Token.RightParen -> {
//                throw Unreachable()
//            }
//            is Token.EOF -> {
//                throw Unreachable()
//            }
//            is Token.Identifier -> {
//                return Value.String(next.value)
//            }
//            is Token.NewLine -> {
//                throw Unreachable()
//            }
//        }
//    }
//}