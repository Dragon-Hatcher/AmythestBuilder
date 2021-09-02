package load

import rep.BlockType
import rep.Cluster
import dev.dewy.nbt.tags.CompoundTag
import dev.dewy.nbt.tags.RootTag
import util.Size

fun readNbtIntoCluster(nbt: RootTag): Cluster? {
    val regions = nbt.compound.getCompound("Regions") ?: return null
    if (regions.value.size != 1) return null
    val region = regions.value.values.first() as CompoundTag

    val blockPalette = BlockPalette.fromRegionNbt(region) ?: return null
    val size = Size.fromRegionNbt(region) ?: return null
    val blocks = Blocks.fromRegionNbt(region, blockPalette, size) ?: return null

    val buddingAmethystBlocks = blocks.blocksOfType(BlockType("minecraft:budding_amethyst"))
    return Cluster(buddingAmethystBlocks)
}