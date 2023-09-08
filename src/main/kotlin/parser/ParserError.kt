package parser

import lexer.SpanData
import lexer.TokenType

abstract class ParserError(open val span: SpanData) : Exception() {
    abstract fun emit(): Diagnostic
}
class UnexpectedToken(val expected: TokenType, val found: TokenType, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "expected ${this.expected}, found ${this.found}", span)
    }
}

class InvalidCommand(val command: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "`$command` is not a valid command", span)
    }
}

class IncorrectArgument(val expectedType: String, val foundType: String, val commandName: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "invalid argument in command $commandName - expected `$expectedType`, found `$foundType`", span)
    }
}

class InvalidEvent(val foundEvent: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "$foundEvent is not a valid event", span)
    }
}


class Unreachable : Exception()