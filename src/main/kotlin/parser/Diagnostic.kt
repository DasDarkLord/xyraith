package parser

import lexer.SpanData
import java.io.File

class Diagnostic(val errorCode: Int, val problem: String, val span: SpanData) {
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
        return """
[E$errorCode] $problem
${" ".repeat(lineCount.toString().length+1)}| In file `$file.xyr`
${" ".repeat(lineCount.toString().length+1)}|
$lineCount | $line
${" ".repeat(lineCount.toString().length+1)}| ${" ".repeat(span.spanStart-lineStart-1)} ${"^".repeat(span.spanEnd-span.spanStart)}
${" ".repeat(lineCount.toString().length+1)}|
        """.trimIndent()
    }
}