package lang.lexer

sealed class TokenType {
    data object LeftParen : TokenType()
    data object RightParen : TokenType()
    data object Identifier : TokenType()
    data object StringText : TokenType()
    data object Number : TokenType()
    data object Symbol : TokenType()
    data object EOF : TokenType()
    data object At : TokenType()
    data object NewLine : TokenType()
    data object Code : TokenType()
}