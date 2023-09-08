# Bytecode Info
This document acts as Xyraith's documentation  for it's bytecode format.

## Names
- **Byte** 1 byte in memory
- **Short** BE 2 bytes in memory representing a number
- **Char** BE 2 bytes in memory representing a character
- **Int** BE 4 bytes in memory
- **Double** BE 8 bytes in memory representing a number with decimals
- **String** - Variadic amount of bytes representing `Char`, terminates when it sees a 0.
## Magic Number
Xyraith's magic number (in hexadecimal) is:
```
65 74 68 65 72 77 65 61 76 65 72
```
## Constants Table
Xyraith has a constants table at the start of its files. Repeat the below for each constant:
```
/* ID of the constant */
[Int - Constant ID]
/* Type of the constant */
[Byte - Constant Type {
    1 = Number
    2 = String
    // do more later
}]
/* Value of the constant */
[Depends on Constant Type {
    If it's a number => {
        [Double - Constant Value]
    }
    If it's a string => {
        [String - Constant value]
    }
    // do more later
}]
```
## Basic Blocks
Xyraith's bytecode is split into "basic blocks". This is the format for them:
```
/* block header */
0x00
/* id of the constant with the name of the basic block */
[Int - Block ID Constant]
/* Event ID, see `Events.kt` for list of events and their IDs */
[Byte - Event ID]
/* Series of instructions. It must always end with a return instruction. */
[Instruction opcodes]
```

## Instructions
There are two types of instructions - opcodes and shortcodes.
Opcodes take up one byte, while shortcodes take up three.
Opcodes are used for more common operations - pushing onto stack, adding, etc.
Shortcodes are used for less common operations - such as interacting with the MC server.

Here is their format:
### Opcodes
```
/* Instruction opcode */
[Byte - 0x00-0xFE representing the opcode]
```

### Shortcodes
```
/* Shortcode opcode */
0xFF [Short - a short representing the shortcode ID]
```
