package load

import rep.BlockType
import Position
import util.Size
import util.chunkBitset
import dev.dewy.nbt.tags.CompoundTag
import java.lang.Exception

class Blocks(private val blocks: List<BlockId>, private val palette: BlockPalette, private val size: Size) {

    fun blocksOfType(type: BlockType): Set<Position> {
        val id = palette.idForType(type) ?: return setOf()
        return blocks
            .withIndex()
            .filter { it.value == id }
            .map { it.index.toPosition(size) }
            .toSet()
    }

    companion object {
        fun fromRegionNbt(region: CompoundTag, palette: BlockPalette, size: Size): Blocks? {
            val blockStatesNbt = region.getLongArray("BlockStates")?.value ?: return null
            return try {
                val bits = chunkBitset(blockStatesNbt, size.volume, palette.bitWidth)
                Blocks(bits, palette, size)
            } catch (e: Exception) {
                null
            }
        }
    }

    //    east -> west   +x
    //    north -> south +z
    //    bottom -> top  +y

    private fun Position.toIndex(size: Size) = x + (z / size.x) + (y / (size.x * size.z))

    private fun Int.toPosition(size: Size): Position {
        val x = this % size.x
        val z = (this / size.x) % size.z
        val y = this / (size.x * size.y)
        return Position(x, y, z)
    }
}

typealias BlockId = Int
