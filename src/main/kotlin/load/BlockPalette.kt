package load

import rep.BlockType
import dev.dewy.nbt.tags.CompoundTag
import kotlin.math.ceil
import kotlin.math.log

class BlockPalette(blockTypes: List<BlockType>) {

    private val idToType: Map<BlockId, BlockType> = blockTypes.mapIndexed { i, t -> i to t }.toMap()
    private val typeToId: Map<BlockType, BlockId> = blockTypes.mapIndexed { i, t -> t to i }.toMap()

    val bitWidth: Int =
        log(blockTypes.size.toDouble(), 2.0)
        .let { ceil(it) }
        .toInt()
        .coerceAtLeast(2)

    fun idForType(type: BlockType): BlockId? = typeToId[type]

    companion object {
        fun fromRegionNbt(region: CompoundTag): BlockPalette? {
            val paletteNbt = region.getList<CompoundTag>("BlockStatePalette")
            val blockTypes: List<BlockType?> = paletteNbt.value.map { BlockType.fromCompoundTag(it) }
            if (blockTypes.any { it == null }) return null
            return BlockPalette(blockTypes.map { it!! })
        }
    }
}