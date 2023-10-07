package typechecker

import code.instructions.Visitable
import error.*
import events
import lexer.SpanData
import parser.*
import registry.commandRegistry
import kotlin.math.exp

class Typechecker {
    var localVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    val globalVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    val entityVariables: MutableMap<String, ArgumentType> = mutableMapOf()

    fun typecheckEvent(event: Ast.Event) {
        localVariables = mutableMapOf()

        if(event.eventType == EventType.EVENT) {
            if(!events.containsKey(event.name))
                throw InvalidEvent(event.name, event.span)
        }

        typecheckBlock(event.code)
    }

    private fun typecheckBlock(block: Ast.Block) {
        for(command in block.nodes) {
            typecheckCommand(command)
        }
    }

    private fun typecheckCommand(command: Ast.Command) {
        val valueIter = command.arguments.iterator()
        if(!commandRegistry.containsKey(command.name))
            throw InvalidCommand(command.name, command.span)
        val visitable = commandRegistry[command.name]!!["object"]!! as Visitable
        val nodeIter = visitable.arguments.list.iterator()


        if(command.name == "store") {
            if(command.arguments[0] !is Value.Symbol) return
            val variableName = (command.arguments[0] as Value.Symbol).value
            val value =
                if(command.arguments[1] is Value.Command)
                    getCommandReturnType((command.arguments[1] as Value.Command).value)
                else
                    command.arguments[1].castToArgumentType()

            if(!localVariables.containsKey(variableName)) {
                localVariables[variableName] = value
            }
            if(!localVariables[variableName]!!.isEqualTo(value)) {
                throw VariableWrongType(
                    variableName,
                    localVariables[variableName].toString(),
                    value.toString(),
                    command.span
                )
            }
        }

        if(command.name == "global.store") {
            if(command.arguments[0] !is Value.Symbol) return
            val variableName = (command.arguments[0] as Value.Symbol).value
            val value =
                if(command.arguments[1] is Value.Command)
                    getCommandReturnType((command.arguments[1] as Value.Command).value)
                else
                    command.arguments[1].castToArgumentType()

            if(!globalVariables.containsKey(variableName)) {
                globalVariables[variableName] = value
            }
            if(!globalVariables[variableName]!!.isEqualTo(value)) {
                throw VariableWrongType(
                    variableName,
                    globalVariables[variableName].toString(),
                    value.toString(),
                    command.span
                )
            }
        }

        if(command.name == "foreach") {
            val variableName = (command.arguments[0] as Value.Symbol).value

            val expectedType =
                if(command.arguments[1] is Value.Command)
                    getCommandReturnType((command.arguments[1] as Value.Command).value)
                else
                    command.arguments[1].castToArgumentType()
            if(!localVariables.containsKey(variableName)) {
                localVariables[variableName] = expectedType
            }

            if(!localVariables[variableName]!!.isEqualTo(expectedType)) {
                throw VariableWrongType(variableName, expectedType.toString(), localVariables[variableName].toString(), command.span)
            }
        }

        for(value in command.arguments) {
            if(value is Value.Block)
                typecheckBlock(value.value)

            if(value is Value.Command)
                typecheckCommand(value.value)
        }

        while(true) {
            val nextNode = if(nodeIter.hasNext()) nodeIter.next() else break
            val nextArgumentType = when(nextNode) {
                is ArgumentNode.SingleArgumentNode -> nextNode.type
                is ArgumentNode.PluralArgumentNode -> nextNode.type
                is ArgumentNode.OptionalArgumentNode -> nextNode.type
                else -> throw Unreachable()
            }
            typecheckValue(valueIter, nextArgumentType, nextNode, visitable.command, command.span)
        }


    }

    private fun typecheckValue(
        valueIter: Iterator<Value>,
        expected: ArgumentType,
        node: ArgumentNode?,
        name: String,
        span: SpanData
    ) {
        if(node == null)
            throw IncorrectArgument(expected.toString(), "NO ARGUMENT", name, span)
        if(!valueIter.hasNext() && node !is ArgumentNode.OptionalArgumentNode)
            throw IncorrectArgument(expected.toString(), "NO ARGUMENT", name, span)


        if(node is ArgumentNode.PluralArgumentNode) {
            while(true) {
                if(!valueIter.hasNext()) {
                    return
                }
                val next = valueIter.next()
                var nextType = next.castToArgumentType()
                if(next is Value.Command) {
                    nextType = getCommandReturnType(next.value, expected)
                }
                println("nextType plural = $nextType | expected: $expected")
                if(!nextType.isEqualTo(expected) && expected != ArgumentType.ANY)
                    throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
            }
        }
        if(node is ArgumentNode.SingleArgumentNode) {
            val next = valueIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected)
            }
            println("nextType single = $nextType | expected: $expected")
            if(!nextType.isEqualTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
        }
        if(node is ArgumentNode.OptionalArgumentNode) {
            if(!valueIter.hasNext()) return
            val next = valueIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected)
            }
            println("nextType optional = $nextType | expected: $expected")
            if(!nextType.isEqualTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
        }
    }

    private fun getCommandReturnType(command: Ast.Command, expectedType: ArgumentType? = null): ArgumentType {
        return when(command.name) {
            "load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!localVariables.containsKey(symbol.value))
                    throw LocalVariableWasntDeclared(symbol.value, command.span)
                if(expectedType != null && localVariables[symbol.value] != expectedType)
                    throw VariableWrongType(symbol.value, expectedType.toString(), localVariables[symbol.value].toString(), command.span)
                println("type is ${localVariables[symbol.value]}")
                localVariables[symbol.value]!!
            }
            "global.load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!globalVariables.containsKey(symbol.value))
                    throw LocalVariableWasntDeclared(symbol.value, command.span)
                if(expectedType != null && globalVariables[symbol.value] != expectedType)
                    throw VariableWrongType(symbol.value, expectedType.toString(), globalVariables[symbol.value].toString(), command.span)
                println("type is ${globalVariables[symbol.value]}")
                globalVariables[symbol.value]!!
            }

            else -> {
                (commandRegistry[command.name]!!["object"] as Visitable).returnType
            }
        }
    }


}