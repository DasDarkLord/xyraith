object Logger {
    fun trace(info: Any) {
        if(debug >= 5) {
            println(info)
        }
    }

    fun debug(info: Any) {
        if(debug >= 4) {
            println(info)
        }
    }

    fun info(info: Any) {
        if(debug >= 3) {
            println(info)
        }
    }
    fun warn(info: Any) {
        if(debug >= 2) {
            println(info)
        }
    }
    fun error(info: Any) {
        if(debug >= 1) {
            println(info)
        }
    }
}
