import dev.dewy.nbt.NbtReader
import java.io.File

fun main() {
    val litematic = File("C:\\Users\\danie\\IdeaProjects\\AmethystBuilder\\src\\main\\resources\\Simple Cluster Example.litematic")
    val nbt = NbtReader.fromFile(litematic)
    println(readNbtIntoCluster(nbt))
}