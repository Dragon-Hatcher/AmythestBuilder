typealias BlockGroup = Set<Position2D>

fun findSlimePatternForCluster(cluster: Cluster) {
    val crystals = getCrystals(cluster, Axis.X)
    val buds = cluster.budsFromSide(Axis.X)
    val all = getAllPoints(crystals)
    val dMatrix = DistanceMatrix(all, crystals, buds)

    var max = 10000

    while (true) {
        val new = findOneKMeansCluster(crystals, buds, crystals.size / 6, dMatrix)
        if(new < max) {
            max = new
            println(max)
        }
    }
}

fun findOneKMeansCluster(crystals: BlockGroup, buds: BlockGroup, clusterCount: Int, distanceMatrix: DistanceMatrix): Int {
    val groupings: MutableMap<Position2D, Int> = crystals.associateWith { 0 }.toMutableMap()
    var centers = List(clusterCount) { crystals.random().toDPosition2D() }

    val defaultPosition = crystals.random().toDPosition2D()

    val MAX_ITERS = 100
    var iters = 0

    while (true) {
        if(iters > MAX_ITERS) break
        iters++
        //reassign
        for (p in groupings.keys) {
            if (groupings[p] == -1) continue
            val distances = centers.map(DPosition2D::toPosition2D).map { distanceMatrix.getDistanceFromTo(p, it) }
            if (distances.all { it > 1000.0 }) {
                groupings[p] = -1
                continue
            }
            val newGroup = distances.withIndex().minByOrNull { it.value }!!.index
            groupings[p] = newGroup
        }

        //calc centers
        val groupCount = MutableList(clusterCount) { 0 }
        val newCentersAccumulator = MutableList(clusterCount) { DPosition2D(0.0, 0.0) }
        for ((c, g) in groupings) {
            if (g == -1) continue
            groupCount[g] += 1
            val old = newCentersAccumulator[g]
            newCentersAccumulator[g] = DPosition2D(old.x + c.x, old.y + c.y)
        }
        val newCenters = newCentersAccumulator.mapIndexed { i, p -> if (groupCount[i] != 0) DPosition2D(p.x / groupCount[i], p.y / groupCount[i]) else defaultPosition }
        if(newCenters == centers) break
        centers = newCenters
    }

    val groupCount = MutableList(clusterCount + 1) { 0 }
    for ((c, g) in groupings) {
        groupCount[g + 1] += 1
    }
    if(groupCount[0] > 5) return 10000
    return groupCount.maxOf { it }
}

fun getCrystals(cluster: Cluster, axis: Axis): BlockGroup =
    cluster.crystalsFromSide(Axis.X) - cluster.budsFromSide(Axis.X)

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
                group != null -> '0' + group
                p in buds -> '#'
                else -> ' '
            }
            print(char)
        }
        println()
    }
}