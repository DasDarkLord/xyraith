package error

import lexer.SpanData
import java.io.File

class Diagnostic(val errorCode: Int, val problem: String, val span: SpanData, val help: String? = null) {
    override fun toString(): String {
        val content = File(span.file).readText()
        var line = ""
        var ptr = 0
        var lineCount = 1
        var lineStart = 0
        var inRange = false
        for(char in content.toString()) {
            line += char
            ptr++
            if(char == '\n') {
                lineCount++
                if(inRange) {
                    line = line.replace("\n", "")
                    break
                }
                lineStart = ptr
                line = ""
            }
            if(ptr in span.spanStart..span.spanEnd) {
                inRange = true
            }
        }
        val (spanStart, spanEnd, file) = span
        var len = lineCount.toString().length+1
        if(len < 0) len = 1
        return """
[E$errorCode] $problem
${" ".repeat(len)}| In file `$file.xyr`
${" ".repeat(len)}|
$lineCount | $line
${" ".repeat(len)}| ${" ".repeat(span.spanStart-lineStart)} ${"^".repeat(span.spanEnd-span.spanStart)}
${" ".repeat(len)}|
${if(help == null) "" else "= help: $help"}
        """.trimIndent()
    }
}