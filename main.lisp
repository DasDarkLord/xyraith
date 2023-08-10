(event join (
    (player.sendMessage "Welcome to the server %player%!")
    (store :x (mul (load :x) 2))
))