# Xyraith
Xyraith is a dynamically-typed, object-oriented programming language designed for creating Minecraft servers.

## Example Server
This server is an example that just broadcasts messages when you join & leave.
```lisp
(evemt startup (
    (world.defaultPlatform)
))
(event join (
    (player.sendMessage @a "<light_blue>%player% <blue>has joined!")
))
(event quit (
    (player.sendMessage @a "<light_blue>%player% <blue>has left.")
))
```