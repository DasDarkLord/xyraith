package typechecker

import code.instructions.Visitable
import error.*
import events
import lexer.SpanData
import net.minestom.server.command.CommandParser.Result.KnownCommand.Invalid
import parser.*
import registry.commandRegistry
import kotlin.math.exp

import types
import structs

class Typechecker {
    private var localVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    private val globalVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    private val entityVariables: MutableMap<String, ArgumentType> = mutableMapOf()



    fun typecheckEvent(event: Ast.Event) {
        localVariables = mutableMapOf()

        if(event.eventType == EventType.EVENT) {
            if(!events.containsKey(event.name))
                throw InvalidEvent(event.name, event.span)
        }
        println("types at event time: $types")
        typecheckBlock(event.code)
        if(event.eventType == EventType.STRUCT) {
            typecheckStruct(event.code, event.name.removePrefix(":__struct_init_"))
        }
    }

    private fun typecheckStruct(block: Ast.Block, structName: String) {
        if(types.contains(structName)) throw AlreadyDefinedType(structName, block.span)

        types.add(structName)
        println("types: $types {we need to add $structName}")
        for(node in block.nodes) {
            if(node.name != "struct.field") {
                throw NotAStructField(node.span)
            }
            val fieldName = node.arguments[0] as Value.Symbol
            val typeName = node.arguments[1] as Value.String
            if(!types.contains(typeName.value)) throw NotAType(typeName.value, types, node.span)
            if(!structs.containsKey(structName)) structs[structName] = mutableMapOf()
            println("made struct $structName")
            structs[structName]!![fieldName.value] = ArgumentType(typeName.value, listOf())
        }
    }

    private fun typecheckBlock(block: Ast.Block) {
        for(command in block.nodes) {
            typecheckCommand(command)
        }
    }


    private fun typecheckCommand(command: Ast.Command) {
        println("types at command ${command.name} time: $types")
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
        println("types at value ${name} time: $types")
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

    fun getCommandReturnType(command: Ast.Command, expectedType: ArgumentType? = null): ArgumentType {
        println("types when pre err: $types")
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
                println("types when err: $types {${symbol.value}}")
                if(!types.contains(symbol.value)) throw NotAType(symbol.value, types, command.span)
                return ArgumentType(symbol.value, listOf())
            }
            "struct.get" -> {
                val type = command.arguments[0].getFixedType(this)
                val symbol = command.arguments[1] as Value.Symbol
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAStructField(command.span)
                return structs[type.toTypeName()]!![symbol.value]!!
            }
            "list" -> {
                return command.arguments[0].getFixedType(this)
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
            "list" -> {
                val type = command.arguments[0].getFixedType(this)

                for(argument in command.arguments) {
                    val argumentType = argument.getFixedType(this)
                    if(!argumentType.isEqualTypeTo(type)) {
                        throw IncorrectArgument(
                            type.toString(),
                            argument.getFixedType(this).toString(),
                            command.name,
                            command.span
                        )
                    }
                }
            }
            "store" -> {
                if(command.arguments[0] !is Value.Symbol) return
                val variableName = (command.arguments[0] as Value.Symbol).value
                val value = command.arguments[1].getFixedType(this)

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

                if(!expectedType.isEqualTypeTo(ArgumentType.GENERIC_LIST)) {
                    throw IncorrectArgument(
                        ArgumentType.GENERIC_LIST.toString(),
                        expectedType.toString(),
                        command.name,
                        command.span,
                    )
                }
                val finalExpected = expectedType.genericTypes

                if(!localVariables.containsKey(variableName)) {
                    localVariables[variableName] = finalExpected[0]
                }

                if(!localVariables[variableName]!!.isEqualTypeTo(finalExpected[0])) {
                    throw VariableWrongType(variableName, finalExpected[0], localVariables[variableName]!!, command.span)
                }
            }
            "struct.init" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!types.contains(symbol.value)) throw NotAType(symbol.value, types, command.span)
            }
            "struct.get" -> {
                val type = command.arguments[0].getFixedType(this)
                val symbol = command.arguments[1] as Value.Symbol
                if(!types.contains(type.toTypeName())) throw NotAType(type.toTypeName(), types, command.span)
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAStructField(command.span)
            }
            "struct.set" -> {
                val type = command.arguments[0].getFixedType(this)
                val symbol = command.arguments[1] as Value.Symbol
                if(!types.contains(type.toTypeName())) throw NotAType(type.toTypeName(), types, command.span)
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAStructField(command.span)
            }
        }
    }

}