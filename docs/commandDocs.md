## console.log
Log a message to the console.
### Command Parameters
- **String** String to print

### Return Type
Returns Null.
## add
Sum a series of numbers.
### Command Parameters
- **Number(s)** Numbers to add

### Return Type
Returns Number.
## sub
Subtract two numbers from eachother.
### Command Parameters
- **Number** Left hand side
- **Number** Right hand side

### Return Type
Returns Number.
## mul
Multiply a series of numbers
### Command Parameters
- **Number(s)** Numbers to multiply

### Return Type
Returns Number.
## div
Divide two numbers
### Command Parameters
- **Number** Dividend
- **Number** Divisor

### Return Type
Returns Number.
## mod
Get the modulo of two numbers.
### Command Parameters
- **Number** Number to get modulo of
- **Number** Number to modulo by

### Return Type
Returns Number.
## random
Generate a random number.
### Command Parameters
- **Number** Minimum number
- **Number** Maximum number

### Return Type
Returns Number.
## range
Generate a series of numbers.
### Command Parameters
- **Number** Minimum number
- **Number** Maximum number

### Return Type
Returns List.
## math.perlin
Generate a random number 0.0-1.0 based on location and seed.
### Command Parameters
- **Location** Perlin location
- **Number** Seed

### Return Type
Returns Number.
## loc
Generate a location from coordinates.
### Command Parameters
- **Number** X coordinate
- **Number** Y coordinate
- **Number** Z coordinate
- ***Number** Pitch coordinate
- ***Number** Yaw coordinate

### Return Type
Returns Location.
## item
Generate a location from coordinates.
### Command Parameters
- **String** Namespace ID
- ***Number** Amount of item

### Return Type
Returns Item Stack.
## world.setBlock
Set a block at a location.
### Command Parameters
- **Command** Location to set the block at
- **String** Block

### Return Type
Returns Null.
## world.setChatFormat
Requires `chat` event.
Set a the format of the outgoing chat message.
Use {player} to auto-fill in the player, and {message} for the message.
### Command Parameters
- **String** Format of the message.

### Return Type
Returns Null.
## world.loadAnvilWorld
Make a world load based on region files.
### Command Parameters
- **String** World to load

### Return Type
Returns Null.
## player.sendMessage
Send the player a message in chat.
### Command Parameters
- **String** Message to send

### Return Type
Returns Null.
## player.sendActionBar
Send a player a message in the actionbar.
### Command Parameters
- **String** Actionbar to send

### Return Type
Returns Null.
## player.sendTitle
Send a player a message through a title.
### Command Parameters
- **String** Title to send
- **String** Subtitle to send
- ***Number** Duration
- ***Number** Fade in time
- ***Number** Fade out time

### Return Type
Returns Null.
## player.setHealth
Set a player's health.
### Command Parameters
- **Number** Health to set (2 HP = 1 heart)

### Return Type
Returns Null.
## player.getHealth
Get a player's health.
### Command Parameters
None
### Return Type
Returns Number.
## player.setHunger
Set a player's hunger points.
### Command Parameters
- **Number** Points to set hunger to

### Return Type
Returns Null.
## player.getSaturation
Get a player's hunger points.
### Command Parameters
None
### Return Type
Returns Number.
## player.setSaturation
Set a player's saturation points.
### Command Parameters
- **Number** Points to set saturation to

### Return Type
Returns Null.
## player.getSaturation
Get a player's saturation points.
### Command Parameters
None
### Return Type
Returns Number.
## player.heal
Heal a player's health.
### Command Parameters
None
### Return Type
Returns Null.
## player.damage
Deal damage to a player.
### Command Parameters
None
### Return Type
Returns Null.
## player.teleport
Teleport a player to a location.
### Command Parameters
- **Command** Location to teleport

### Return Type
Returns Null.
## player.gamemode
Change a player's game mode.
### Command Parameters
- **String** Game mode to change to.

### Return Type
Returns Null.
## store
Set a value of a symbol in function-local scope.
### Command Parameters
- **Symbol** Symbol to set value of
- **Any Value** Value to set

### Return Type
Returns Null.
## load
Get a value of a symbol in function-local scope.
### Command Parameters
- **Symbol** Symbol to get value of

### Return Type
Returns Any Value.
## foreach
Loop through a series of items in a list.
### Command Parameters
- **Symbol** Variable to store value in
- **List** List to loop through
- **Block** Code to run on each iteration

### Return Type
Returns Null.
