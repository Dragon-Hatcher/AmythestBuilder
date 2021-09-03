import dev.dewy.nbt.NbtReader
import load.readNbtIntoCluster
import solve.findSlimePatternForCluster
import java.io.File

fun main() {
    val litematic = File("src\\main\\resources\\Big Cluster Example.litematic")
    val nbt = NbtReader.fromFile(litematic)
    val cluster = readNbtIntoCluster(nbt) ?: return
    findSlimePatternForCluster(cluster)
}