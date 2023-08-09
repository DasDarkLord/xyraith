package lexer

class Lexer(val source: String) {
    fun transform(): MutableList<Token> {
        val output: MutableList<Token> = mutableListOf()
        val source = source.trim()
        var position = 0
        while(position < source.length) {
            when {
                source[position].isWhitespace() -> {
                    position++
                }
                source[position] == '(' -> {
                    output.add(Token.LeftParen(position, position++))
                }
                source[position] == ')' -> {
                    output.add(Token.RightParen(position, position++))
                }
                source[position].isDigit() || source[position] == '-' -> {
                    val spanStart = position
                    var number = ""
                    while(position < source.length && (source[position].isDigit() || source[position] == '-')) {
                        number = "$number${source[position]}"
                        position++
                    }
                    output.add(Token.Number(number.toDouble(), spanStart, position))
                }
                source[position] == '"' -> {
                    val spanStart = position
                    var string = ""
                    position++
                    while(position < source.length && source[position] != '"') {
                        string = "$string${source[position]}"
                        position++
                    }
                    output.add(Token.StringText(string, spanStart, position))
                    position++
                }
                else -> {
                    val spanStart = position
                    var symbol = ""
                    while (position < source.length &&
                        !source[position].isWhitespace() &&
                        source[position] != '(' &&
                        source[position] != ')' &&
                        source[position] != '"') {
                        symbol = "$symbol${source[position]}"
                        position++
                    }
                    if(symbol.startsWith(":")) {
                        output.add(Token.Symbol(symbol, spanStart, position))
                    } else {
                        output.add(Token.Identifier(symbol, spanStart, position))
                    }
                }
            }
        }
        return output
    }
}