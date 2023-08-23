package registry

object Extensions {
    object Player {
        const val SENDMESSAGE = 1.toShort()
        const val SETSPAWNPOINT = 10.toShort()
        const val GETLOCATION = 20.toShort()
        const val TELEPORT = 21.toShort()
    }
    object World {
        const val SETBLOCK = 1001.toShort()
        const val GETBLOCK = 1001.toShort()
    }
    object Location {
        const val GETX = 2001.toShort()
        const val GETY = 2002.toShort()
        const val GETZ = 2003.toShort()
        const val GETPITCH = 2004.toShort()
        const val GETYAW = 2005.toShort()
        const val SETX = 2006.toShort()
        const val SETY = 2007.toShort()
        const val SETZ = 2008.toShort()
        const val SETPITCH = 2009.toShort()
        const val SETYAW = 2010.toShort()
    }
    object Number {
        const val RANDOM = 3000.toShort()
    }

}