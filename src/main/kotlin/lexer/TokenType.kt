package lexer

sealed class TokenType {
    object LeftParen : TokenType()
    object RightParen : TokenType()
    object Identifier : TokenType()
    object StringText : TokenType()
    object Number : TokenType()
    object Symbol : TokenType()
    object EOF : TokenType()
}