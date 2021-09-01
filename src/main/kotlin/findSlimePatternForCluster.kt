import org.jblas.DoubleMatrix
import kotlin.math.ceil
import kotlin.math.sqrt

typealias BlockGroup = Set<Position2D>

fun findSlimePatternForCluster(cluster: Cluster) {
    val crystals = getCrystals(cluster, Axis.X)
    val buds = cluster.budsFromSide(Axis.X)
    val all = getAllPoints(crystals).toList()
    val dMatrix = DistanceMatrix(all, crystals, buds)
    val clusters = ceil(crystals.size.toDouble() / TARGET_SIZE.toDouble()).toInt()

    var max = 10000.0
    var iters = 0

//    while (iters < 100) {
//        println(iters++)
        val new = findOneKMeansCluster(crystals, buds, all, clusters, dMatrix, max)
        if (new < max) {
            max = new
            println(max)
        }
//    }
}

const val MAX_ITERS = 100
const val TARGET_SIZE = 8

fun findOneKMeansCluster(crystals: BlockGroup, buds: BlockGroup, all: List<Position2D>, clusterCount: Int, distanceMatrix: DistanceMatrix, oldMax: Double): Double {
    val groupings: MutableMap<Position2D, Int> = crystals.associateWith { 0 }.toMutableMap()
    val groupMatrices: List<DoubleMatrix> = List(clusterCount) { DoubleMatrix(Array(all.size) { arrayOf(0.0).toDoubleArray() }) }
    var centers = List(clusterCount) { crystals.random() }
    val indices = all.withIndex().associate { it.value to it.index }

    var iters = 0

    val defaultPosition = crystals.random()

    fun recalculateCenters(): Boolean {
        fun newMethod() = (0 until clusterCount).map {
            distanceMatrix.findBestCenterFor(groupMatrices[it])
        }

        fun oldMethod(): List<Position2D> {
            val newCenters = MutableList(clusterCount) { defaultPosition }
            for (c in 0 until clusterCount) {
                val points = groupings.filter { it.value == c }.map { it.key }
                val newCenter = all.minByOrNull { possibleCenter ->
                    points.sumOf { distanceMatrix.getDistanceFromTo(it, possibleCenter) }
                }!!
                newCenters[c] = newCenter
            }
            return newCenters
        }

        val newCenters = newMethod()
//        val newCenters = oldMethod()

//        val ret = newCenters != centers
//        centers = newCenters
//        return ret
        return false
    }

    val start = System.currentTimeMillis()
    repeat(1000) { recalculateCenters() }
    println(System.currentTimeMillis() - start)

    TODO()
//    while (true) {
//        if (iters > MAX_ITERS) break
//        iters++
//
//        //reassign
//        for (point in groupings) {
//            val (p, g) = point
//            val index = indices[p]!!
//            if (groupings[p] == -1) continue
//            val distances = centers.map { distanceMatrix.getDistanceFromTo(p, it) }
//            if (distances.all { it > 1000.0 }) {
//                groupings[p] = -1
//                groupMatrices[g].set(index, 0.0)
//                continue
//            }
//            val newGroup = distances.withIndex().minByOrNull { it.value }!!.index
//            groupings[p] = newGroup
//            groupMatrices[g].set(index, 0.0)
//            groupMatrices[newGroup].set(index, 1.0)
//        }
//
//        val done = !recalculateCenters()
//
//        var groupCountInit: MutableList<Int>
//        var sortedCenters = centers.withIndex().toMutableList()
//        while (true) {
//            groupCountInit = MutableList(clusterCount) { 0 }
//            for (g in groupings.values) {
//                if (g == -1) continue
//                groupCountInit[g] += 1
//            }
//
//            if(groupCountInit.all { it <= TARGET_SIZE }) break
//
////            println(groupCountInit)
//            sortedCenters = sortedCenters.sortedBy { groupCountInit[it.index] }.reversed().toMutableList()
//            val (centerNum, center) = sortedCenters[0]
//            sortedCenters.removeAt(0)
//
//            val points = groupings.filter { it.value == centerNum }.map { it.key }
//                .sortedBy { distanceMatrix.getDistanceFromTo(it, center) }.reversed()
//            val movedPoints = points.subList(0, points.size - TARGET_SIZE)
//            for (p in movedPoints) {
//                val newGroup = sortedCenters.minByOrNull { distanceMatrix.getDistanceFromTo(p, it.value) }!!.index
//                groupings[p] = newGroup
//                val index = indices[p]!!
//                groupMatrices[centerNum].set(index, 0.0)
//                groupMatrices[newGroup].set(index, 1.0)
//            }
//
//        }
//
//        if(done) break
//
//        recalculateCenters()
//    }
//
////    groupings.prettyPrint(buds)
//    val value = centers.withIndex().maxOf { (centerNum, center) ->
//        val points = groupings.filter { it.value == centerNum }.map { it.key }
//        val totalDistance = points.sumOf { distanceMatrix.getDistanceFromTo(it, center) }
//        totalDistance / points.size.toDouble()
//    }
//
//    if(sqrt(value) < oldMax) {
//        groupings.prettyPrint(buds)
//    }
//
//    return sqrt(value)
}

fun getCrystals(cluster: Cluster, axis: Axis): BlockGroup =
    cluster.crystalsFromSide(axis) - cluster.budsFromSide(axis)

private fun getAllPoints(crystals: BlockGroup): BlockGroup {
    val width = crystals.maxOf { it.x } + 2
    val xStart = crystals.minOf { it.x } - 1
    val height = crystals.maxOf { it.y } + 2
    val yStart = crystals.minOf { it.y } - 1

    val allPoints: MutableSet<Position2D> = mutableSetOf()

    for (x in xStart..width) {
        for (y in yStart..height) {
            allPoints.add(Position2D(x, y))
        }
    }

    return allPoints
}

private fun BlockGroup.prettyPrint() {
    val width = this.maxOf { it.x }
    val xStart = this.minOf { it.x }
    val height = this.maxOf { it.y }
    val yStart = this.minOf { it.y }

    for (x in xStart..width) {
        for (y in yStart..height) {
            val char = if (Position2D(x, y) in this) '#' else '.'
            print(char)
        }
        println()
    }
}

private fun Map<Position2D, Int>.prettyPrint(buds: BlockGroup) {
    val width = this.keys.maxOf { it.x }
    val xStart = this.keys.minOf { it.x }
    val height = this.keys.maxOf { it.y }
    val yStart = this.keys.minOf { it.y }

    for (x in xStart..width) {
        for (y in yStart..height) {
            val p = Position2D(x, y)
            val group = this[p]
//            val char = if (group == null) '.' else ('0' + group)
            val char = when {
                group != null -> 'A' + group
                p in buds -> '#'
                else -> ' '
            }
            print(char)
        }
        println()
    }
}