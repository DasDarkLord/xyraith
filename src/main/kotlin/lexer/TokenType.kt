package lexer

sealed class TokenType {
    object LeftParen : TokenType()
    object RightParen : TokenType()
    class Identifier : TokenType()
    class StringText : TokenType()
    class Number : TokenType()
    class Symbol : TokenType()
}