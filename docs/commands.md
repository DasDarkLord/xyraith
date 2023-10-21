## add
Sum a series of numbers.
### Command Parameters
- **number(s)** Numbers to add
### Return Type
Returns number.

## asyncCall
Call a function asynchronously and pass it parameters if wanted.
You can not recievce values back from asynchronous calls.
### Command Parameters
- **symbol** Function to call
- **Unknown** If you see this, please report this as an issue!
### Return Type
Returns nothing.

## call
Call a function and pass it parameters if wanted. Functions do not share local variables.
### Command Parameters
- **symbol** Function to call
- **Unknown** If you see this, please report this as an issue!
### Return Type
Returns any.

## div
Divide two numbers
### Command Parameters
- **number** Dividend
- **number** Divisor
### Return Type
Returns number.

## eq
Check if a value is equal to another
### Command Parameters
- **any** Left hand side
- **any** Right hand side
### Return Type
Returns boolean.

## false
Return a false boolean.
### Command Parameters
None
### Return Type
Returns boolean.

## foreach
Loop through a series of items in a list.
### Command Parameters
- **symbol** Variable to store value in
- **list[any]** List to loop through
- **block** Code to run on each iteration
### Return Type
Returns nothing.

## ge
Check if a number is greater than another
### Command Parameters
- **number** Left hand side
- **number** Right hand side
### Return Type
Returns boolean.

## geq
Check if a number is greater than or equal to another
### Command Parameters
- **number** Left hand side
- **number** Right hand side
### Return Type
Returns boolean.

## if
Run a block if a condition is true
### Command Parameters
- **boolean** Condition to check
- **block** Code to run on each iteration
### Return Type
Returns nothing.

## isNull
Returns true if the value is null.
### Command Parameters
- **any** Value to compare to null
### Return Type
Returns boolean.

## item
Generate an item from an ID and an amount
### Command Parameters
- **string** Namespace ID
- ***number** Amount of item
### Return Type
Returns itemStack.

## le
Check if a number is less than another
### Command Parameters
- **number** Left hand side
- **number** Right hand side
### Return Type
Returns boolean.

## leq
Check if a number is less than or equal to another
### Command Parameters
- **number** Left hand side
- **number** Right hand side
### Return Type
Returns boolean.

## list
Makes a list with the given values.
If the values are not of the same type, an error will be thrown
at compile-time.
### Command Parameters
- **any(s)** Values of the list
### Return Type
Returns list[any].

## load
Get a value of a symbol in local scope.
### Command Parameters
- **symbol** Symbol to get value of
### Return Type
Returns any.

## loop
Run a block infinitely
### Command Parameters
- **block** Code to loop
### Return Type
Returns nothing.

## mod
Get the modulo of two numbers.
### Command Parameters
- **number** Number to get modulo of
- **number** Number to modulo by
### Return Type
Returns number.

## mul
Multiply a series of numbers
### Command Parameters
- **number(s)** Numbers to multiply
### Return Type
Returns number.

## parameter
Get a parameter passed to this function by index
### Command Parameters
- **number** Index of parameter to get
### Return Type
Returns any.

## random
Generate a random number.
### Command Parameters
- **number** Minimum number
- **number** Maximum number
### Return Type
Returns number.

## range
Generate a series of numbers.
### Command Parameters
- **number** Minimum number
- **number** Maximum number
### Return Type
Returns list[number].

## resetSelection
Set targets based on selector.
### Command Parameters
None
### Return Type
Returns nothing.

## return
Get a parameter passed to this function by index
### Command Parameters
- ***any** Value to return
### Return Type
Returns any.

## select
Set targets based on selector.
### Command Parameters
- **string** Type to target
- **string(s)** Requirements to target
### Return Type
Returns nothing.

## sleep
Delay the current thread for a given amount of milliseconds.
### Command Parameters
- **number** Millieconds to sleep for
### Return Type
Returns nothing.

## store
Set a value of a symbol in local scope.
### Command Parameters
- **symbol** Symbol to set value of
- **any** Value to set
### Return Type
Returns nothing.

## string
Return a string with all values concatenated.
### Command Parameters
- **any(s)** Values to concatenate
### Return Type
Returns string.

## sub
Subtract two numbers from eachother.
### Command Parameters
- **number** Left hand side
- **number** Right hand side
### Return Type
Returns number.

## true
Return a true boolean.
### Command Parameters
None
### Return Type
Returns boolean.

