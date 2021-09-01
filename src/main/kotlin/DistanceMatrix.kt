import org.jblas.DoubleMatrix
import kotlin.math.min

class DistanceMatrix(
    private val allPoints: List<Position2D>,
    private val inclusions: Set<Position2D>,
    private val exclusions: Set<Position2D>
) {

    private val distancesFrom: Map<Position2D, Map<Position2D, Double>> =
        allPoints.associateWith(::calculateDistancesFrom)

//    private val distancesMatrix = SimpleMatrix(allPoints.size, allPoints.size, true, allPoints.map { row ->
//        val thisRow = distancesFrom[row]!!
//        val a = allPoints.map { thisRow[it]!! }
//        a
//    }.flatten().toDoubleArray())

//    private val distancesMatrix: Primitive64Matrix = Primitive64Matrix.FACTORY.make(allPoints.size.toLong(), allPoints.size.toLong())

    private val distancesMatrix: DoubleMatrix = DoubleMatrix(
        allPoints.map { row ->
            val thisRow = distancesFrom[row]!!
            allPoints.map { thisRow[it]!! }.toDoubleArray()
        }.toTypedArray()
    )

    init {
        println("${distancesMatrix.rows}, ${distancesMatrix.columns}")
    }

    fun getDistanceFromTo(from: Position2D, to: Position2D): Double {
        return distancesFrom[from]!![to]!!
    }

    fun findBestCenterFor(group: DoubleMatrix)
//    : Position2D
    {
        val results = distancesMatrix.mmul(group)

//        var minI = -1
//        var minV = Double.MAX_VALUE
//        for ((i, v) in results.withIndex()) {
//            if (v < minV) {
//                minV = v
//                minI = i
//            }
//        }

//        return allPoints[minI]
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
                    else -> 1.1
                }
                val newDistance = distances[nextPoint]!! + edgeWeight
                distances[neighbor] = min(distances[neighbor]!!, newDistance)
            }
        }

        return distances.map { it.key to (it.value * it.value) }.toMap()
    }

    private fun getAdjacencies(p: Position2D): Set<Position2D> =
        setOf(
            p.copy(x = p.x + 1),
            p.copy(x = p.x - 1),
            p.copy(y = p.y + 1),
            p.copy(y = p.y - 1),
        ).filter { it in allPoints }.toSet()

}