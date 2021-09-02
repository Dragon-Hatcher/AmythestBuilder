import dev.dewy.nbt.NbtReader
import java.io.File

fun main() {
    val litematic = File("src\\main\\resources\\Simple rep.Cluster Example.litematic")
    val nbt = NbtReader.fromFile(litematic)
    println(_root_ide_package_.load.readNbtIntoCluster(nbt))
}