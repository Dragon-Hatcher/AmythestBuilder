import org.jblas.DoubleMatrix
import kotlin.math.min

typealias PointId = Int

class DistanceMatrix(
    points: List<Position2D>,
    inclusions: List<Position2D>,
    exclusions: List<Position2D>
) {

    init {
        DoubleMatrix().getRow(0)
    }

    private val distances = calculateDistances(points, inclusions.indices.toList(), exclusions.indices.toList())

    fun distanceBetween(from: PointId, to: PointId) = distances.get(from, to)

    fun optimalCenter(pointSet: DoubleMatrix) = distances.mmul(pointSet).indexOfMin()

    private fun calculateDistances(
        points: List<Position2D>,
        inclusions: List<PointId>,
        exclusions: List<PointId>
    ): DoubleMatrix =
        points
            .indices
            .map { calculateDistancesFromPoint(it, points, inclusions, exclusions) }
            .toTypedArray()
            .let(::DoubleMatrix)

    private fun calculateDistancesFromPoint(
        from: PointId,
        points: List<Position2D>,
        inclusionIds: List<PointId>,
        exclusionIds: List<PointId>
    ): DoubleArray {
        fun getAdjacencies(pId: PointId): Set<PointId> =
            points[pId]
                .let {
                    setOf(
                        it.copy(x = it.x + 1),
                        it.copy(x = it.x - 1),
                        it.copy(y = it.y + 1),
                        it.copy(y = it.y - 1),
                    )
                }
                .filter { it in points }
                .map { points.indexOf(it) } // TODO replace with map
                .toSet()

        val unvisited: MutableSet<PointId> = points.indices.toMutableSet()
        val distances = DoubleArray(points.size) { Double.MAX_VALUE / 2 }
        distances[from] = 0.0

        fun getMinDistanceUnvisitedPoint(): PointId = unvisited.minByOrNull { distances[it] }!!

        while (unvisited.isNotEmpty()) {
            val nextPoint = getMinDistanceUnvisitedPoint()
            unvisited.remove(nextPoint)
            val neighbors = getAdjacencies(nextPoint)
            for (neighbor in neighbors) {
                val edgeWeight = when (neighbor) {
                    in inclusionIds -> 1.0
                    in exclusionIds -> 1000000.0
                    else -> 1.1
                }
                val newDistance = distances[nextPoint] + edgeWeight
                distances[neighbor] = min(distances[neighbor], newDistance)
            }
        }

        return distances
    }
}

fun DoubleMatrix.indexOfMin(): Int {
    var minValue = Double.MAX_VALUE
    var minIndex = -1

    for ((i, d) in this.data.withIndex()) {
        if (d < minValue) {
            minValue = d
            minIndex = i
        }
    }

    return minIndex
}