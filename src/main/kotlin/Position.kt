data class Position(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x, $y, $z)"
}

data class Position2D(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}