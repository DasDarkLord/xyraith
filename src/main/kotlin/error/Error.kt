package error

import lang.lexer.SpanData
import lang.lexer.Token
import registry.commandRegistry
import typechecker.ArgumentType

abstract class ParserError(open val span: SpanData) : Exception() {
    abstract fun emit(): Diagnostic
}

class UnexpectedToken(val expected: String, val found: Token, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "expected ${expected}, found ${found}", span)
    }
}

class UnexpectedEOF(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(2, "unexpected end of file", span)
    }
}

class NotAValidImport(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(15, "this is not a valid import", span)
    }
}

class NotANumber(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(16, "not a valid number", span)
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
