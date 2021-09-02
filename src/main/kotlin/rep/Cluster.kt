package rep

import Position
import Position2D
import util.Axis

class Cluster(private val buds: Set<Position>) {
    private val crystals: Set<Position> = crystalsFromBuds(buds)

    private val budViews: Map<Axis, Set<Position2D>> = Axis.values().associateWith { axisView(buds, it) }
    private val crystalViews: Map<Axis, Set<Position2D>> = Axis.values().associateWith { axisView(crystals, it) }

    fun budsFromSide(axis: Axis) = budViews[axis]!!

    fun crystalsFromSide(axis: Axis) = crystalViews[axis]!!

    override fun toString(): String {
        return buds.toString()
    }

    private fun axisView(items: Set<Position>, alongAxis: Axis): Set<Position2D> {
        fun normalize(position: Position): Position2D =
            when(alongAxis) {
                Axis.X -> Position2D(position.y, position.z)
                Axis.Y -> Position2D(position.x, position.z)
                Axis.Z -> Position2D(position.x, position.y)
            }

        return items.map(::normalize).toSet()
    }

    private fun crystalsFromBuds(buds: Set<Position>): Set<Position> {
        val crystals: MutableSet<Position> = mutableSetOf()

        for (bud in buds) {
            crystals.add(Position(bud.x + 1, bud.y, bud.z))
            crystals.add(Position(bud.x - 1, bud.y, bud.z))
            crystals.add(Position(bud.x, bud.y + 1, bud.z))
            crystals.add(Position(bud.x, bud.y - 1, bud.z))
            crystals.add(Position(bud.x, bud.y, bud.z + 1))
            crystals.add(Position(bud.x, bud.y, bud.z - 1))
        }

        return crystals
    }
}