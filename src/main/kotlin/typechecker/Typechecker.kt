package typechecker

import error.*
import events
import functions
import lang.lexer.SpanData
import parser.*
import registry.commandRegistry
import runtime.Value

import types
import structs

/**
 * The Typechecker validates your program at compile time to ensure
 * there are no issues with types.
 */
class Typechecker {
    private var localVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    private val globalVariables: MutableMap<String, ArgumentType> = mutableMapOf()
    private val entityVariables: MutableMap<String, ArgumentType> = mutableMapOf()


    /**
     * Typecheck an event
     */
    fun typecheckEvent(event: Ast.Event) {
        localVariables = mutableMapOf()

        if(event.eventType == EventType.Event) {
            if(!events.containsKey(event.name))
                throw InvalidEvent(event.name, event.eventNameSpan)
        }
        if(event.eventType is EventType.Function) {
            typecheckBlock(event.code, event.eventType.name)
        } else {
            println("block")
            typecheckBlock(event.code, null)
        }

        if(event.eventType == EventType.Struct) {
            typecheckStruct(event.code, event.name.removePrefix(":__struct_init_"))
        }
    }

    /**
     * Typecheck a struct definition
     */
    private fun typecheckStruct(block: Ast.Block, structName: String) {
        if(types.contains(structName)) throw AlreadyDefinedType(structName, block.span)

        types.add(structName)
        for(node in block.nodes) {
            if(node.name != "struct.field") {
                throw NotAStructField(node.nameSpan)
            }
            val fieldName = node.arguments[0] as Value.Symbol
            val typeName = node.arguments[1] as Value.String
            if(!types.contains(typeName.value)) throw NotAType(typeName.value, types, node.nameSpan)
            if(!structs.containsKey(structName)) structs[structName] = mutableMapOf()
            println("made struct $structName")
            structs[structName]!![fieldName.value] = ArgumentType(typeName.value, listOf())
        }
    }

    /**
     * Typecheck a block
     */
    private fun typecheckBlock(block: Ast.Block, functionName: String?) {
        for(command in block.nodes) {
            typecheckCommand(command, functionName)
        }
    }

    /**
     * Typecheck a command
     */
    private fun typecheckCommand(command: Ast.Command, functionName: String?) {
        val valueIter = command.arguments.iterator()
        val spanIter = command.nodeSpans.iterator()

        if(!commandRegistry.containsKey(command.name))
            throw InvalidCommand(command.name, command.nameSpan)
        val visitable = commandRegistry[command.name]!!["object"]!! as instructions.Visitable
        val nodeIter = visitable.arguments.list.iterator()





        while(true) {
            val nextNode = if(nodeIter.hasNext()) nodeIter.next() else break
            val nextArgumentType = when(nextNode) {
                is ArgumentNode.SingleArgumentNode -> nextNode.type
                is ArgumentNode.PluralArgumentNode -> nextNode.type
                is ArgumentNode.OptionalArgumentNode -> nextNode.type
                is ArgumentNode.OptionalPluralArgumentNode -> nextNode.type
                else -> throw Unreachable()
            }
            typecheckValue(
                valueIter, nextArgumentType, nextNode, visitable.command,
                command.nameSpan, spanIter, functionName, command
            )
        }

        for(value in command.arguments) {
            if(value is Value.Command)
                typecheckCommand(value.value, functionName)
        }

        typecheckSpecialBehavior(command, functionName)

        for(value in command.arguments) {
            if(value is Value.Block)
                typecheckBlock(value.value, functionName)
        }
    }

    /**
     * Typecheck a value using Argument Nodes
     */
    private fun typecheckValue(
        valueIter: Iterator<Value>,
        expected: ArgumentType,
        node: ArgumentNode?,
        name: String,
        span: SpanData,
        spanIter: Iterator<SpanData>,
        functionName: String?,
        commandRef: Ast.Command,
    ) {
        if(node == null)
            throw UnfinishedCommand(expected.toString(), span)
        if(!valueIter.hasNext() && node !is ArgumentNode.OptionalArgumentNode && node !is ArgumentNode.OptionalPluralArgumentNode)
            throw UnfinishedCommand(expected.toString(), span)


        if(node is ArgumentNode.PluralArgumentNode) {
            while(true) {
                if(!valueIter.hasNext()) {
                    return
                }
                val next = valueIter.next()
                val nextSpan = spanIter.next()
                var nextType = next.castToArgumentType()
                if(next is Value.Command) {
                    nextType = getCommandReturnType(next.value, expected, functionName)
                }
                if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                    throw IncorrectArgument(expected.toString(), nextType.toString(), name, nextSpan)
            }
        }
        if(node is ArgumentNode.OptionalPluralArgumentNode) {
            while(true) {
                if(!valueIter.hasNext())
                    return
                val next = valueIter.next()
                val nextSpan = spanIter.next()
                var nextType = next.castToArgumentType()
                if(next is Value.Command) {
                    nextType = getCommandReturnType(next.value, expected, functionName)
                }
                if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                    throw IncorrectArgument(expected.toString(), nextType.toString(), name, nextSpan)
            }
        }
        if(node is ArgumentNode.SingleArgumentNode) {
            val next = valueIter.next()
            val nextSpan = spanIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected, functionName)
            }
            if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, nextSpan)
        }
        if(node is ArgumentNode.OptionalArgumentNode) {
            if(!valueIter.hasNext()) {
                commandRef.arguments.add(node.defaultValue)
                return
            }
            val next = valueIter.next()
            val nextSpan = spanIter.next()
            var nextType = next.castToArgumentType()
            if(next is Value.Command) {
                nextType = getCommandReturnType(next.value, expected, functionName)
            }
            if(!nextType.isEqualTypeTo(expected) && expected != ArgumentType.ANY)
                throw IncorrectArgument(expected.toString(), nextType.toString(), name, nextSpan)
        }
    }

    /**
     * Gets the return type of a command and compares it to `expectedType`.
     */
    fun getCommandReturnType(command: Ast.Command, expectedType: ArgumentType?, functionName: String? = null): ArgumentType {
        println("getting return type of: ${command}")
        return when(command.name) {
            "load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!localVariables.containsKey(symbol.value))
                    throw VariableWasntDeclared(symbol.value, command.nameSpan)
                if(expectedType != null && !localVariables[symbol.value]!!.isEqualTypeTo(expectedType))
                    throw VariableWrongType(symbol.value, expectedType, localVariables[symbol.value]!!, command.nameSpan)
                println("type is ${localVariables[symbol.value]}")
                localVariables[symbol.value]!!
            }
            "global.load" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!globalVariables.containsKey(symbol.value))
                    throw VariableWasntDeclared(symbol.value, command.nameSpan)
                if(expectedType != null && !globalVariables[symbol.value]!!.isEqualTypeTo(expectedType))
                    throw VariableWrongType(symbol.value, expectedType, globalVariables[symbol.value]!!, command.nameSpan)
                println("type is ${globalVariables[symbol.value]}")
                globalVariables[symbol.value]!!
            }
            "struct.init" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!types.contains(symbol.value)) throw NotAType(symbol.value, types, command.nameSpan)
                return ArgumentType(symbol.value, listOf())
            }
            "struct.set" -> {
                return command.arguments[0].getFixedType(this, functionName)
            }
            "struct.get" -> {
                val type = command.arguments[0].getFixedType(this, functionName)
                val symbol = command.arguments[1] as Value.Symbol
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAStructField(command.nameSpan)
                return structs[type.toTypeName()]!![symbol.value]!!
            }
            "list" -> {
                return command.arguments[0].getFixedType(this, functionName)
            }
            "call" -> {
                val name = (command.arguments[0] as Value.Symbol).value
                if(!functions.containsKey(name)) {
                    throw NotAValidFunction(command.nodeSpans[0])
                }
                return functions[name]!!.second
            }
            "parameter" -> {
                if(!functions.containsKey(functionName)) throw InvalidCommand(functionName!!, command.nameSpan)
                return functions[functionName!!]!!.first[command.arguments[0].castToNumber().toInt()]
            }
            else -> {
                if(!commandRegistry.containsKey(command.name))
                        throw InvalidCommand(command.name, command.nameSpan)
                (commandRegistry[command.name]!!["object"] as instructions.Visitable).returnType
            }
        }
    }

    /**
     * Performs special behavior with certain commands in this function.
     * For example, this makes `list` return a list that is typesafe, not just
     * a list with a lot of types.
     */
    private fun typecheckSpecialBehavior(command: Ast.Command, functionName: String?) {
        when(command.name) {
            "list" -> {
                val type = command.arguments[0].getFixedType(this, functionName)

                for(argument in command.arguments) {
                    val argumentType = argument.getFixedType(this, functionName)
                    if(!argumentType.isEqualTypeTo(type)) {
                        throw IncorrectArgument(
                            type.toString(),
                            argument.getFixedType(this, functionName).toString(),
                            command.name,
                            command.nameSpan
                        )
                    }
                }
            }
            "store" -> {
                if(command.arguments[0] !is Value.Symbol) return
                val variableName = (command.arguments[0] as Value.Symbol).value
                val value = command.arguments[1].getFixedType(this, functionName)

                if(!localVariables.containsKey(variableName)) {
                    localVariables[variableName] = value
                }
                if(!localVariables[variableName]!!.isEqualTypeTo(value)) {
                    throw VariableWrongType(
                        variableName,
                        localVariables[variableName]!!,
                        value,
                        command.nameSpan
                    )
                }
            }
            "global.store" -> {
                if(command.arguments[0] !is Value.Symbol) return
                val variableName = (command.arguments[0] as Value.Symbol).value
                val value =
                    if(command.arguments[1] is Value.Command)
                        getCommandReturnType((command.arguments[1] as Value.Command).value, null, functionName)
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
                        command.nameSpan
                    )
                }
            }
            "target.store" -> {
                if(command.arguments[0] !is Value.Symbol) return
                val variableName = (command.arguments[0] as Value.Symbol).value
                val value =
                    if(command.arguments[1] is Value.Command)
                        getCommandReturnType((command.arguments[1] as Value.Command).value, null, functionName)
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
                        command.nameSpan
                    )
                }
            }
            "foreach" -> {
                val variableName = (command.arguments[0] as Value.Symbol).value

                val expectedType =
                    if(command.arguments[1] is Value.Command)
                        getCommandReturnType((command.arguments[1] as Value.Command).value, null, functionName)
                    else
                        command.arguments[1].castToArgumentType()

                if(!expectedType.isEqualTypeTo(ArgumentType.GENERIC_LIST)) {
                    throw IncorrectArgument(
                        ArgumentType.GENERIC_LIST.toString(),
                        expectedType.toString(),
                        command.name,
                        command.nameSpan,
                    )
                }
                val finalExpected = expectedType.genericTypes

                if(!localVariables.containsKey(variableName)) {
                    localVariables[variableName] = finalExpected[0]
                }

                if(!localVariables[variableName]!!.isEqualTypeTo(finalExpected[0])) {
                    throw VariableWrongType(variableName, finalExpected[0], localVariables[variableName]!!, command.nameSpan)
                }
            }
            "struct.init" -> {
                val symbol = command.arguments[0] as Value.Symbol
                if(!types.contains(symbol.value)) throw NotAType(symbol.value, types, command.nameSpan)
            }
            "struct.get" -> {
                val type = command.arguments[0].getFixedType(this, functionName)
                val symbol = command.arguments[1] as Value.Symbol
                if(!types.contains(type.toTypeName())) throw NotAType(type.toTypeName(), types, command.nameSpan)
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAStructField(command.nameSpan)
            }
            "struct.set" -> {
                val type = command.arguments[0].getFixedType(this, functionName)
                val symbol = command.arguments[1] as Value.Symbol
                if(!types.contains(type.toTypeName())) throw NotAType(type.toTypeName(), types, command.nameSpan)
                if(!structs.contains(type.toTypeName())) throw NotAType(type.toTypeName(), types, command.nameSpan)
                println("Structs: $structs\n${type.toTypeName()}")
                println("orig: $command")
                if(!structs[type.toTypeName()]!!.contains(symbol.value)) throw NotAFieldOnStruct(command.nameSpan)
            }
            "call" -> {
                val name = (command.arguments[0] as Value.Symbol).value
                if(!functions.containsKey(name)) {
                    throw NotAValidFunction(command.nodeSpans[0])
                }
                val arguments = functions[name]!!.first.iterator()
                println("functions: $functions")
                for(x in 2..command.arguments.size) {
                    if(!arguments.hasNext()) throw TooManyArguments(command.nodeSpans.last())
                    val expected = arguments.next()
                    val found = command.arguments[x-1].getFixedType(this, functionName)
                    if(!expected.isEqualTypeTo(found))
                        throw IncorrectArgument(expected.toString(), found.toString(), command.name, command.nodeSpans[x-1])
                }
                if(arguments.hasNext()) throw UnfinishedCommand(arguments.next().toString(), command.nodeSpans.last())
            }
        }
    }
}