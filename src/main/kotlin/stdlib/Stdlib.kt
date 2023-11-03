package stdlib

//Automatically generated in `build.gradle.kts`

val stdlibFiles = mutableMapOf<String, String>(
"std/math" to """struct :math {
}

function :math.e (-> number) {
    return 2.718281828459045
}

function :math.pi (-> number) {
    return 3.141592653589793
}

function :math.tau (-> number) {
    return (mul (call :math.pi) 2)
}

function :math.degrees_to_radians (-> number) {
    return 0.017453292519943295
}

function :math.radians_to_degrees (-> number) {
    return 57.29577951308232
}

function :math.toDegrees (number -> number) {
    return (mul (parameter 0) (call :math.radians_to_degrees))
}

function :math.toRadians (number -> number) {
    return (mul (parameter 0) (call :math.degrees_to_radians))
}

function :math.factorial (number -> number) {
    if (leq (parameter 0) 0) {
        return 1
    }
    return (mul (parameter 0) (call :math.factorial (sub (parameter 0) 1)))
}

function :math.sqrt (number -> number) {
    return (pow (parameter 0) 0.5)
}

function :math.cbrt (number -> number) {
    return (pow (parameter 0) (div 1 3))
}

function :math.sin (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "sin(D)D") (parameter 0))
}

function :math.asin (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "asin(D)D") (parameter 0))
}

function :math.sinh (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "sinh(D)D") (parameter 0))
}

function :math.cos (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "cos(D)D") (parameter 0))
}

function :math.acos (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "acos(D)D") (parameter 0))
}

function :math.cosh (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "cosh(D)D") (parameter 0))
}

function :math.tan (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "tan(D)D") (parameter 0))
}

function :math.atan (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "atan(D)D") (parameter 0))
}

function :math.atan2 (number, number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "atan2(DD)D") (parameter 0) (parameter 1))
}

function :math.tanh (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "tanh(D)D") (parameter 0))
}

function :math.log (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "log(D)D") (parameter 0))
}

function :math.log10 (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "log10(D)D") (parameter 0))
}

function :math.log1p (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "log1p(D)D") (parameter 0))
}

function :math.round (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "round(D)D") (parameter 0))
}

function :math.ceil (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "ceil(D)D") (parameter 0))
}

function :math.floor (number -> number) {
    return (invokeMethod (getMethod (javaClass "java.lang.Math") "floor(D)D") (parameter 0))
}"""
,"std/mc/location" to """;; TODO: location
struct :loc {
    struct.field :x number 0
    struct.field :y number 0
    struct.field :z number 0
    struct.field :pitch number 0
    struct.field :yaw number 0
}

function :loc (number number number number number -> :loc) {
    return (struct.set (struct.set (struct.set (struct.set (struct.set
        (struct.init :loc) :x (parameter 0)) :y (parameter 1)) :z (parameter 2)) :pitch (parameter 3)) :yaw (parameter 4))
}

function :loc.setX (:loc number -> :loc) {
    return (struct.set (parameter 0) :x (parameter 1))
}

function :loc.setY (:loc number -> :loc) {
    return (struct.set (parameter 0) :y (parameter 1))
}

function :loc.setZ (:loc number -> :loc) {
    return (struct.set (parameter 0) :z (parameter 1))
}

function :loc.setPitch (:loc number -> :loc) {
    return (struct.set (parameter 0) :pitch (parameter 1))
}

function :loc.setYaw (:loc number -> :loc) {
    return (struct.set (parameter 0) :yaw (parameter 1))
}

function :loc.addX (:loc number -> :loc) {
    return (struct.set (parameter 0) :x (add (struct.get (parameter 0) :x) (parameter 1)))
}

function :loc.addY (:loc number -> :loc) {
    return (struct.set (parameter 0) :y (add (struct.get (parameter 0) :y) (parameter 1)))
}

function :loc.addZ (:loc number -> :loc) {
    return (struct.set (parameter 0) :z (add (struct.get (parameter 0) :z) (parameter 1)))
}

function :loc.addPitch (:loc number -> :loc) {
    return (struct.set (parameter 0) :pitch (add (struct.get (parameter 0) :pitch) (parameter 1)))
}

function :loc.addYaw (:loc number -> :loc) {
    return (struct.set (parameter 0) :yaw (add (struct.get (parameter 0) :yaw) (parameter 1)))
}

struct :vec {
    struct.field :x number 0
    struct.field :y number 0
    struct.field :z number 0
}

function :vec (number number number  -> :vec) {
    return (struct.set (struct.set (struct.set
        (struct.init :vec) :x (parameter 0)) :y (parameter 1)) :z (parameter 2))
}"""
,"std/mc/particle" to """struct :particle {
    struct.field :id string ""
    struct.field :offset ":vec" (:vec 0 0 0)
    struct.field :motion ":vec" (:vec 0 0 0)
}

function :particle (string -> :particle) {
    store :s (struct.init :particle)
    store :s (struct.set (load :s) :id (parameter 0))
    return (load :s)
}

function :particle.setOffset (:particle number number number -> :particle) {
    return (struct.set (parameter 0) :motion (:vec (parameter 1) (parameter 2) (parameter 3)))
}

function :player.playParticle (:particle :loc :vec -> nothing) {
    (player.playRawParticle
        (struct.get (parameter 0) :id)
        (struct.get (parameter 1) :x)
        (struct.get (parameter 1) :y)
        (struct.get (parameter 1) :z)
        (struct.get (parameter 2) :x)
        (struct.get (parameter 2) :y)
        (struct.get (parameter 2) :z))
}"""
,"std/mc/player" to """struct :gamemode {
    struct.field :id number 0
}

function :gamemode.survival (-> :gamemode) {
    return (struct.init :gamemode)
}

function :gamemode.creative (-> :gamemode) {
    return (struct.set (struct.init :gamemode) :id 1)
}

function :gamemode.adventure (-> :gamemode) {
    return (struct.set (struct.init :gamemode) :id 2)
}

function :gamemode.spectator (-> :gamemode) {
    return (struct.set (struct.init :gamemode) :id 3)
}"""
,"std/mc" to """import "std/mc/location"
import "std/mc/particle"
import "std/mc/player""""
,"std/std" to """import "std/math""""
,
)