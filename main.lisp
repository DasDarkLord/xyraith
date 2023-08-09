(event join (
    (player.sendMessage "Welcome to the server %player%!")
    (player.teleport (globals.load :spawnLocation))
))

(event loop (
    (if (le (player.y) 0) (
        (player.teleport (globals.load :spawnLocation))
        (player.sendTitle "&cYou died!")
    ))
))

(event startup (
    (store :x 0)
    (store :y 64)
    (store :z 0)
    (foreach :index (range 0 100) (
        (world.setBlock (loc (load :x) (load :y) (load :z)) (item "stone"))
        (store :x (add (load :x) (random -1 1)))
        (store :y (add (load :y) (random -1 1)))
        (store :z (add (load :z) (random 2 3)))
    ))
    (globals.store :spawnLocation (loc 0 64 0))
))