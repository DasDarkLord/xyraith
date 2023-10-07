package error

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

class UnexpectedEOF(override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(2, "unexpected end of file", span)
    }
}

class InvalidCommand(val command: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(3, "`$command` is not a valid command", span)
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

class VariableWrongType(val variable: String, val expectedType: String, val foundType: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(
            6,
            "invalid argument recieved from variable `${variable}` - expected `$expectedType`, found `$foundType`",
            span,
            "the first store of a variable dictates it's type"
        )
    }
}

class LocalVariableWasntDeclared(val variable: String, override val span: SpanData) : ParserError(span) {
    override fun emit(): Diagnostic {
        return Diagnostic(7, "local `$variable` was not declared in this scope", span)
    }
}
class Unreachable : Exception()