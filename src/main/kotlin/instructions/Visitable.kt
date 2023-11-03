package instructions

import code.Interpreter
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import typechecker.ArgumentList
import typechecker.ArgumentType
import java.lang.reflect.Field

var visitables: List<instructions.Visitable> = Reflections(ConfigurationBuilder().forPackage("")).getSubTypesOf(
    instructions.Visitable::class.java).map { type ->
    val instanceField: Field?
    try {
        instanceField = type.getDeclaredField("INSTANCE")
    } catch(nsfe: NoSuchFieldException) {
        return@map null
    }
    return@map instanceField.get(null) as instructions.Visitable
}.filterNotNull()

/**
 * Visitable is an interface that allows you to create a Xyraith command.
 * In order to make one, create an `object` that inherits this interface
 * and put it in the list above.
 */
interface Visitable {
    /**
     * The opcode of the command.
     */
    val code: Int

    /**
     * Determines whether the command is an extension opcode.
     * If the opcode is an extension, mark it as true.
     * If it is not, mark it as false and keep code within -127 to 127.
     */
    val isExtension: Boolean

    /**
     * The name of the command that users will call it by.
     * Unless you will make an API for it in the standard
     * library, give this a sensible name.
     */
    val command: String

    /**
     * A list of arguments the command can accept.
     * Automatically validated for you,
     * so you can do unsafe type casts in the `visit` function.
     */
    val arguments: ArgumentList

    /**
     * Description of the command. This needs to describe what the command does.
     */
    val description: String

    /**
     * The return type of the command. If there is a special case for it's return
     * type, add a case in `Typechecker` and mark this as `ANY`.
     */
    val returnType: ArgumentType

    /**
     * The code to run when the interpreter comes across your command.
     */
    suspend fun visit(visitor: Interpreter)
}