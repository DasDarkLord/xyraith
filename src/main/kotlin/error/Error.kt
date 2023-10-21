package error

import lexer.SpanData
import lexer.TokenType
import registry.commandRegistry
import typechecker.ArgumentType

abstract class ParserError(open val span: SpanData) : Exception() {
    abstract fun emit(): Diagnostic
}

class UnexpectedToken(val expected: TokenType, val found: TokenType, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "expected ${this.expected}, found ${this.found}", span)
    }
}

class UnexpectedEOF(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(2, "unexpected end of file", span)
    }
}

class InvalidCommand(val command: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        val distances = mutableMapOf<Int, String>()
//        for(key in commandRegistry.keys) {
//            distances[calculateLevenshteinDistance(command, key)] = key
//        }
//        val sorted = distances.toSortedMap()
//        val correction = distances[sorted.firstKey()]
        val correction = "ur mom"
        return Diagnostic(3, "`$command` is not a valid command", span, "did you mean `$correction`?")
    }
}

class IncorrectArgument(val expectedType: String, val foundType: String, val commandName: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(4, "invalid argument in command $commandName - expected `$expectedType`, found `$foundType`", span)
    }
}

class InvalidEvent(val foundEvent: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(5, "$foundEvent is not a valid event", span)
    }
}

class VariableWrongType(val variable: String, val expectedType: ArgumentType, val foundType: ArgumentType, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(
            6,
            "invalid argument recieved from variable `${variable}` - expected `$expectedType`, found `$foundType`",
            span,
            "the first store of a variable dictates it's type"
        )
    }
}

class VariableWasntDeclared(val variable: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(7, "`$variable` was not declared in this scope", span)
    }
}

class InvalidTargetStore(val variable: String, val type: ArgumentType, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(8, "can not store type $type to variable $variable in target scope", span, "only `Number`, `String`, and `Boolean` are valid types to store in target scope")
    }
}

class NotAStructField(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(9, "structs can only hold struct fields", span, "remove the excess command")
    }
}

class NotAType(private val givenType: String, private val validTypes: List<String>, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        val distances = mutableMapOf<Int, String>()
        for(key in validTypes) {
            distances[calculateLevenshteinDistance(givenType, key)] = key
        }
        val sorted = distances.toSortedMap()
        val correction = distances[sorted.firstKey()]
        return Diagnostic(10, "type `$givenType` does not exist", span, "did you mean `$correction`?")
    }
}

class AlreadyDefinedType(private val givenType: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(11, "type `$givenType` was already defined in this scope", span, "remove the extra definition")
    }
}

class UnfinishedCommand(val expectedType: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(12, "missing argument - expected `$expectedType`", span)
    }
}

class TooManyArguments(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(13, "too many arguments provided", span, "try removing one")
    }
}

class NotAFieldOnStruct(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(14, "this is not a field for this structure", span)
    }
}

class Unreachable : Exception()

// thanks chatgpt
fun calculateLevenshteinDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length

    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) {
        for (j in 0..n) {
            if (i == 0) {
                dp[i][j] = j
            } else if (j == 0) {
                dp[i][j] = i
            } else if (s1[i - 1] == s2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1]
            } else {
                dp[i][j] = 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
    }

    return dp[m][n]
}
