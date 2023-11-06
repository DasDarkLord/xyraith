package lang.lexer

import java.lang.NumberFormatException

class Lexer(val source: String, val file: String) {
    fun transform(): MutableList<Token> {
        val output: MutableList<Token> = mutableListOf()
        val source = source.trim()
        var position = 0
        while(position < source.length) {
            when {
                source[position] == '\n' -> {
                    output.add(Token.NewLine(SpanData(position, position++)))
                }
                source[position].isWhitespace() -> {
                    position++
                }
                source[position] == '(' || source[position] == '{' || source[position] == '[' -> {
                    output.add(Token.LeftParen(SpanData(position, position++, file)))
                }
                source[position] == ')' || source[position] == '}' || source[position] == ']' -> {
                    output.add(Token.RightParen(SpanData(position, position++, file)))
                }
                source[position] == '@' -> {
                    output.add(Token.At(SpanData(position, position++, file)))
                }
                source[position].isDigit() || source[position] == '-' -> {
                    val spanStart = position
                    var number = ""
                    while(position < source.length &&
                        (source[position].isDigit()
                                || source[position] == '-'
                                || source[position] == '.'
                                // hacky fix to make -> writable
                                || source[position] == '>')) {
                        number = "$number${source[position]}"
                        position++
                    }
                    try {
                        output.add(Token.Number(number.toDouble(), SpanData(spanStart, position, file)))
                    } catch(e: NumberFormatException) {
                        output.add(Token.StringText(number, SpanData(spanStart, position, file)))
                    }

                }
                source[position] == '"' -> {
                    val spanStart = position
                    var string = ""
                    position++
                    while(position < source.length && source[position] != '"') {
                        string = "$string${source[position]}"
                        position++
                    }
                    output.add(Token.StringText(string, SpanData(spanStart, position, file)))
                    position++
                }
                source[position] == ';' -> {
                    position++
                    while(position < source.length && source[position] != '\n') {
                        position++
                    }
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
                        output.add(Token.Symbol(symbol, SpanData(spanStart, position, file)))
                    } else if(symbol.startsWith("$")) {
                        output.add(Token.Code(symbol, SpanData(spanStart, position, file)))
                    } else {
                        output.add(Token.Identifier(symbol, SpanData(spanStart, position, file)))
                    }
                }
            }
        }
        return preprocessMain(output, "src/")
    }
}