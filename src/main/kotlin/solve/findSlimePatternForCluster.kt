package solve

import Position2D
import org.jblas.DoubleMatrix
import rep.Cluster
import util.Axis
import kotlin.math.ceil

fun findSlimePatternForCluster(cluster: Cluster) {
    val crystals = getCrystals(cluster, Axis.X)
    val buds = cluster.budsFromSide(Axis.X)
    val all = getAllPoints(crystals)
    val dMatrix = DistanceMatrix(all, crystals, buds)
    val targetSize = 8
    val clusters = ceil(crystals.size.toDouble() / targetSize.toDouble()).toInt()

    clusterCrystalSet(
        crystals.map { all.indexOf(it) }.toSet(),
        all.size,
        dMatrix,
        clusters,
        targetSize,
        all,
        buds
    )

    TODO()
}

fun getCrystals(cluster: Cluster, axis: Axis): Set<Position2D> =
    cluster.crystalsFromSide(axis) - cluster.budsFromSide(axis)

//TODO
private fun getAllPoints(crystals: Set<Position2D>): List<Position2D> {
    val width = crystals.maxOf { it.x } + 2
    val xStart = crystals.minOf { it.x } - 1
    val height = crystals.maxOf { it.y } + 2
    val yStart = crystals.minOf { it.y } - 1

    val allPoints: MutableList<Position2D> = mutableListOf()

    for (x in xStart..width) {
        for (y in yStart..height) {
            allPoints.add(Position2D(x, y))
        }
    }

    return allPoints
}

fun clusterCrystalSet(
    crystals: Set<PointId>,
    blockCount: Int,
    distanceMatrix: DistanceMatrix,
    clusterCount: Int,
    targetClusterSize: Int,
    _allPoints: List<Position2D>,
    _buds: Set<Position2D>
) {
    val groupings = Array(clusterCount) { DoubleMatrix.zeros(blockCount) }
    val centers = Array(clusterCount) { crystals.random() }

    fun changeCrystalGrouping(crystal: PointId, to: Int?) {
        groupings.forEach { it.put(crystal, 0.0) }
        to ?: return
        groupings[to].put(crystal, 1.0)
    }

    fun recalculateCenters(): Boolean {
        var change = false
        for (centerNum in centers.indices) {
            val newCenter = distanceMatrix.optimalCenter(groupings[centerNum])
            change = newCenter != centers[centerNum] || change
            centers[centerNum] = newCenter
        }
        return change
    }

    fun reassignPoints() {
        crystals.forEach { crystal ->
            val newGroup = centers
                .withIndex()
                .map { it.index to distanceMatrix.distanceBetween(crystal, it.value) }
                .minByOrNull { it.second }!!
                .let { if (it.second > 1000.0) null else it.first }
            changeCrystalGrouping(crystal, newGroup)
        }
    }

    fun groupCount() = groupings.map { it.data.count { num -> num == 1.0 } }

    fun insureSize() {
        var mostRecentGroupCount = groupCount()
        var sortedCenters = centers.withIndex()
        while (true) {
            mostRecentGroupCount = groupCount()
            sortedCenters = sortedCenters.sortedBy { -mostRecentGroupCount[it.index] }.toMutableList()

            val largestCenter = sortedCenters[0]
            if(mostRecentGroupCount.all { it <= targetClusterSize }) break

            sortedCenters.removeAt(0)

            val sortedGroupPoints = groupings[largestCenter.index]
                .data
                .withIndex()
                .filter { it.value == 1.0 }
                .map { it.index }
                .sortedBy { -distanceMatrix.distanceBetween(it, largestCenter.value) }
            val extraPointCount = sortedGroupPoints.size - targetClusterSize
            val pointsToMove = sortedGroupPoints.subList(0, extraPointCount)
            for (point in pointsToMove) {
                val newGroup = sortedCenters.minByOrNull { distanceMatrix.distanceBetween(point, it.value) }!!.index
                changeCrystalGrouping(point, newGroup)
            }
        }
    }

    fun prettyPrint() {
        val width = _allPoints.maxOf { it.x }
        val xStart = _allPoints.minOf { it.x }
        val height = _allPoints.maxOf { it.y }
        val yStart = _allPoints.minOf { it.y }

        for (x in xStart..width) {
            for (y in yStart..height) {
                val p = Position2D(x, y)
                val id = _allPoints.indexOf(p)
                if (p in _buds) {
                    print('#')
                } else if (id in crystals) {
                    val group = groupings.withIndex().firstOrNull { it.value.data[id] == 1.0 }?.index
                    print(group?.let { 'a' + it } ?: '-')
                } else {
                    print(' ')
                }
            }
            println()
        }

    }

    while (true) {
        reassignPoints()
        val change = recalculateCenters()
        insureSize()
        if (!change) break
        recalculateCenters()
        prettyPrint()
    }

}