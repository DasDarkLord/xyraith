package docs

import code.visitables
import parser.*

fun generateDocumentation(): String {
    var output = ""
    for(obj in visitables) {
        val documentation = CommandDocument(
            obj.command,
            decomposeList(obj.arguments),
            obj.description
        )
        output += documentation.toHtml()
    }
    return output
}

fun decomposeList(list: ArgumentList): List<Pair<String, String>> {
    val output = mutableListOf<Pair<String, String>>()
    for(arg in list.list) {
        output.add(when(arg) {
            is SingleArgumentNode -> Pair(arg.type.toString(), arg.desc)
            is OptionalArgumentNode -> Pair("*${arg.type.toString()}", arg.desc)
            is PluralArgumentNode -> Pair("${arg.type.toString()}(s)", arg.desc)
            else -> Pair("Unknown", "If you see this, please report this as an issue!")
        })
    }
    return output
}

fun wrapDocumentation(str: String): String {
    return """
<head>
    <title>Xyraith Command Documentation</title>
    <style>
        $css
    </style>
</head>
<body>
    <div class="page">
        <h1>Xyraith Syntax</h1>
        To define a event, use:
        <br>
        <code>
            (event join (<br>
            ;; commands here<br>
            ))<br>
        </code><br>
        The parenthesis around the event are optional.<br>
        You can also use other events, like:
        <ul>
            <li>join - Runs when a player joins the server</li>
            <li>quit - Runs when a player leaves the server</li>
            <li>tick - Runs every tick for a player</li>
        </ul>
        <br>
        Command syntax goes like so:<br>
        <code>
            (commandName {args})
        </code><br>
        For example:
        <code>
            (player.sendMessage "hi!")
        </code><br>
        The parenthesis around the command are optional aswell, though are automatically terminated by<br>
        a new line.<br>
        Here's how to represent each currently supported datatype:
        <ul>
            <li>Number - A standard number. (1, 2.76, -12.1)</li>
            <li>String - Text wrapped in quotation marks ("hi there!", "<blue>ok")</li>
            <li>Location - A special command (loc {x} {y} {z} [pitch] [yaw]) ({} = required, [] = optional)</li>
        </ul><br>
        For example:
        <code>
            (event join (
                (player.sendMessage "hi there!")
                (world.setBlock (loc 0 50 0) "minecraft:stone")
            ))
        </code><br>
        With that out of the way...
        <h1>Xyraith Command Documentation</h1>
        <div class="contents">
            $str
        </div>
    </div>
</body>
    """.trimIndent()
}

var css = """
    
.page {
    background-image: linear-gradient( rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5) ), url("img/background.png");
    color: #fefefe;
    font-size: 36;
    font-family: sans-serif;
    font-size: 130%;
    text-align: center;
	
    position: absolute;
    top:0;
    bottom: 0;
    left: 0;
    right: 0;

    width: 100%;
    min-height: 100%;
    height: fit-content;

    background-size: cover;
    overflow: hidden;
}
a {
    color: #bababa;
}
header a {
    color: #bababa;
    transition: border 200ms, color 200ms;
    text-decoration: none;
    padding: 2px 10px;
    display: inline-block;
}
header a:hover {
    border-bottom: 3px solid white;
    color: white;
}
header a[href='#'] {
    border-bottom: 3px solid white;
    color: white;
}

ul {
    list-style-type: none;
    padding: 0;
}
.button {
	transition-duration: 0.4s;
}
.button:hover {
	background-color: rgb(168, 168, 168);
	color: black;
}
footer {
	text-align: center;
	padding: 3px;
	background-color: linear-gradient(rgb(129, 8, 150), rgb(184, 11, 214));
	color: white;
	position: absolute;
	bottom: 0;
	width: 100%;
}
h1 {
    background: linear-gradient(rgb(255, 0, 0), rgb(255, 133, 133));
    text-align: center;
    -webkit-text-fill-color: transparent;
    background-clip: text;
	-webkit-background-clip: text;
}

h2 {
    background: linear-gradient(rgb(255, 0, 0), rgb(255, 133, 133));
    text-align: center;
    -webkit-text-fill-color: transparent;
    background-clip: text;
	-webkit-background-clip: text;
}
img.title-logo {
    max-width: 500px;
    width: 100%;
}

""".trimIndent()