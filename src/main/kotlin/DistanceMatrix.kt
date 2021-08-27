import kotlin.math.min

class DistanceMatrix(
    private val allPoints: Set<Position2D>,
    private val inclusions: Set<Position2D>,
    private val exclusions: Set<Position2D>
) {

    private val distancesFrom: MutableMap<Position2D, Map<Position2D, Double>> = mutableMapOf()

    fun getDistanceFromTo(from: Position2D, to: Position2D): Double {
        if (!distancesFrom.containsKey(from)) {
            distancesFrom[from] = calculateDistancesFrom(from)
        }

        return distancesFrom[from]!![to]!!
    }

    private fun calculateDistancesFrom(from: Position2D): Map<Position2D, Double> {
        val unvisited: MutableSet<Position2D> = allPoints.toMutableSet()
        val distances = allPoints.associateWith { Double.MAX_VALUE / 2 }.toMutableMap()
        distances[from] = 0.0

        fun getMinDistanceUnvisitedPoint(): Position2D = unvisited.minByOrNull { distances[it]!! }!!

        while (unvisited.isNotEmpty()) {
            val nextPoint = getMinDistanceUnvisitedPoint()
            unvisited.remove(nextPoint)
            val neighbors = getAdjacencies(nextPoint)
            for (neighbor in neighbors) {
                val edgeWeight = when (neighbor) {
                    in inclusions -> 1.0
                    in exclusions -> 1000000.0
                    else -> 1.0
                }
                val newDistance = distances[nextPoint]!! + edgeWeight
                distances[neighbor] = min(distances[neighbor]!!, newDistance)
            }
        }

        return distances
    }

    private fun getAdjacencies(p: Position2D): Set<Position2D> =
        setOf(
            p.copy(x = p.x + 1),
            p.copy(x = p.x - 1),
            p.copy(y = p.y + 1),
            p.copy(y = p.y - 1),
        ).filter { it in allPoints }.toSet()

}