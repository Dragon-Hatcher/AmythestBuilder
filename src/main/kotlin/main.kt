import dev.dewy.nbt.NbtReader
import java.io.File

fun main() {
    val litematic = File("C:\\Users\\danie\\IdeaProjects\\AmythestBuilder\\src\\main\\resources\\Wool Compass.litematic")
    val nbt = NbtReader.fromFile(litematic)
    readNbtIntoCluster(nbt)
}