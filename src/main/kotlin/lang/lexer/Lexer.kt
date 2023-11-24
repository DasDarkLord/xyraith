package lang.lexer

import error.NotANumber
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
                source[position] == '.' -> {
                    output.add(Token.Dot(SpanData(position, position++, file)))
                }
                source[position] == ':' -> {
                    output.add(Token.Colon(SpanData(position, position++, file)))
                }
                source[position] == '=' -> {
                    output.add(Token.Equals(SpanData(position, position++, file)))
                }
                source[position] == '!' -> {
                    output.add(Token.Bang(SpanData(position, position++, file)))
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
                        if(number == "->") {
                            output.add(Token.Arrow(SpanData(spanStart, position, file)))
                        } else {
                            throw NotANumber(SpanData(spanStart, position, file))
                        }

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
                    var iters = 0
                    while (position < source.length &&
                        !source[position].isWhitespace() &&
                        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789_".contains(source[position])) {
                        iters++
                        symbol = "$symbol${source[position]}"
                        position++
                    }
                    if(iters == 0)
                        position++
                    val span = SpanData(spanStart, position, file)
                    when(symbol) {
                        "if" -> output.add(Token.IfKeyword(span))
                        "foreach" -> output.add(Token.ForEachKeyword(span))
                        "global" -> output.add(Token.GlobalKeyword(span))
                        "event" -> output.add(Token.EventKeyword(span))
                        "function" -> output.add(Token.FunctionKeyword(span))
                        "struct" -> output.add(Token.StructKeyword(span))
                        else -> output.add(Token.Identifier(symbol, span))
                    }
                }
            }
            println("position: $position | output: $output")
        }
        output.add(Token.EOF(SpanData(position, position+1)))
        return preprocessMain(output, "src/")
    }
}