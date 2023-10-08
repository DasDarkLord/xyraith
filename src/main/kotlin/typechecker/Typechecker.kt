package typechecker

import code.instructions.Visitable
import error.*
import events
import lexer.SpanData
import net.minestom.server.command.CommandParser.Result.KnownCommand.Invalid
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

        typecheckSpecialBehavior(command)

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
                if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                    throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
            }
        }
        if(node is ArgumentNode.SingleArgumentNode) {
            val next = valueIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected)
            }
            if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
        }
        if(node is ArgumentNode.OptionalArgumentNode) {
            if(!valueIter.hasNext()) return
            val next = valueIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected)
            }
            if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, span)
        }
    }

    private fun getCommandReturnType(command: Ast.Command, expectedType: ArgumentType? = null): ArgumentType {
        return when(command.name) {
            "load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!localVariables.containsKey(symbol.value))
                    throw VariableWasntDeclared(symbol.value, command.span)
                if(expectedType != null && !localVariables[symbol.value]!!.isEqualTypeTo(expectedType))
                    throw VariableWrongType(symbol.value, expectedType, localVariables[symbol.value]!!, command.span)
                println("type is ${localVariables[symbol.value]}")
                localVariables[symbol.value]!!
            }
            "global.load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!globalVariables.containsKey(symbol.value))
                    throw VariableWasntDeclared(symbol.value, command.span)
                if(expectedType != null && !globalVariables[symbol.value]!!.isEqualTypeTo(expectedType))
                    throw VariableWrongType(symbol.value, expectedType, globalVariables[symbol.value]!!, command.span)
                println("type is ${globalVariables[symbol.value]}")
                globalVariables[symbol.value]!!
            }
            "struct.init" -> {
                val symbol = command.arguments[0] as Value.Symbol
                return ArgumentType(symbol.value)
            }
            else -> {
                if(!commandRegistry.containsKey(command.name))
                        throw InvalidCommand(command.name, command.span)
                (commandRegistry[command.name]!!["object"] as Visitable).returnType
            }
        }
    }

    private fun typecheckSpecialBehavior(command: Ast.Command) {
        when(command.name) {
            "store" -> {
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
                if(!localVariables[variableName]!!.isEqualTypeTo(value)) {
                    throw VariableWrongType(
                        variableName,
                        localVariables[variableName]!!,
                        value,
                        command.span
                    )
                }
            }
            "global.store" -> {
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
                if(!globalVariables[variableName]!!.isEqualTypeTo(value)) {
                    throw VariableWrongType(
                        variableName,
                        globalVariables[variableName]!!,
                        value,
                        command.span
                    )
                }
            }
            "target.store" -> {
                if(command.arguments[0] !is Value.Symbol) return
                val variableName = (command.arguments[0] as Value.Symbol).value
                val value =
                    if(command.arguments[1] is Value.Command)
                        getCommandReturnType((command.arguments[1] as Value.Command).value)
                    else
                        command.arguments[1].castToArgumentType()

                if(!entityVariables.containsKey(variableName)) {
                    entityVariables[variableName] = value
                }
                if(!entityVariables[variableName]!!.isEqualTypeTo(value)) {
                    throw VariableWrongType(
                        variableName,
                        entityVariables[variableName]!!,
                        value,
                        command.span
                    )
                }
            }
            "foreach" -> {
                val variableName = (command.arguments[0] as Value.Symbol).value

                val expectedType =
                    if(command.arguments[1] is Value.Command)
                        getCommandReturnType((command.arguments[1] as Value.Command).value)
                    else
                        command.arguments[1].castToArgumentType()

                val finalExpected = expectedType.getGenericType()

                if(!localVariables.containsKey(variableName)) {
                    localVariables[variableName] = finalExpected
                }

                if(!localVariables[variableName]!!.isEqualTypeTo(finalExpected)) {
                    throw VariableWrongType(variableName, finalExpected, localVariables[variableName]!!, command.span)
                }
            }
        }
    }

}