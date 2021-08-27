import kotlin.math.sqrt

data class Position(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x, $y, $z)"
}

data class Position2D(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"

    fun toDPosition2D(): DPosition2D = DPosition2D(x.toDouble(), y.toDouble())
}

data class DPosition2D(val x: Double, val y: Double) {
    override fun toString(): String = "($x, $y)"

    fun toPosition2D() = Position2D(x.toInt(), y.toInt())

    fun distanceTo(other: DPosition2D): Double = sqrt((other.x - x) * (other.x - x) + (other.y - y) * (other.y - y))
}
