package parser

import lexer.TokenType

abstract class ParserError(open val spanStart: Int, open val spanEnd: Int) : Exception() {
    abstract fun emit(): Diagnostic
}
class UnexpectedToken(val expected: TokenType, val found: TokenType, override val spanStart: Int, override val spanEnd: Int) : ParserError(spanStart, spanEnd) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "expected ${this.expected}, found ${this.found}", spanStart, spanEnd)
    }
}

class InvalidCommand(val command: String, override val spanStart: Int, override val spanEnd: Int) : ParserError(spanStart, spanEnd) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "`$command` is not a valid command", spanStart, spanEnd)
    }
}

class IncorrectArgument(val expectedType: String, val foundType: String, val commandName: String, override val spanStart: Int, override val spanEnd: Int) : ParserError(spanStart, spanEnd) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "invalid argument in command $commandName - expected `$expectedType`, found `$foundType`", spanStart, spanEnd)
    }
}

class InvalidEvent(val foundEvent: String, override val spanStart: Int, override val spanEnd: Int) : ParserError(spanStart, spanEnd) {
    override fun emit(): Diagnostic {
        return Diagnostic(1, "$foundEvent is not a valid event", spanStart, spanEnd)
    }
}


class Unreachable : Exception()