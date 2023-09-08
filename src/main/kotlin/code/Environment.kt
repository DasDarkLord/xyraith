package code

import parser.Value

data class Environment(val localVariables: Map<String, Value>)