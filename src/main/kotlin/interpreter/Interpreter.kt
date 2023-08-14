package interpreter

import java.nio.ByteBuffer

class Interpreter(val bytes: ByteBuffer) {
    val blockMap: MutableMap<Int, List<Byte>> = mutableMapOf()
    fun transform() {
        var buf: MutableList<Byte> = mutableListOf()
        var counter = 0
        var latestBlock = -10
        while(true) {
            if(bytes.remaining() == 0) {
                break
            }
            val it = bytes.get()
            println("transforming $it (counter: $counter) (latestBlock: $latestBlock) (buf: $buf)")
            if(it.toInt() == -127) {
                counter++
            } else {
                for(x in 1..counter) {
                    buf.add(-127)
                }
                counter = 0
                buf.add(it)
            }
            if(counter == 20) {
                if(latestBlock != -10) {
                    for(x in 1..20) {
                        buf.removeAt(0)
                    }
                    blockMap[latestBlock] = buf
                }
                buf = mutableListOf()
                latestBlock = bytes.getInt()
            }

        }
    }
    fun interpretBlock(id: Int) {

    }
}