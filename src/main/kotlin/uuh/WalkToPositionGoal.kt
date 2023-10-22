package uuh

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.ai.GoalSelector

class WalkToPositionGoal(entityCreature: EntityCreature, val target: Pos) : GoalSelector(entityCreature) {

    override fun shouldStart(): Boolean {
        return true
    }

    override fun start() {
        entityCreature.navigator.setPathTo(target)
    }

    override fun tick(time: Long) {}
    override fun shouldEnd(): Boolean {
        return true
    }

    override fun end() {}
}
