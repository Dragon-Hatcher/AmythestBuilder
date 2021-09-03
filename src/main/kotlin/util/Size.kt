package util

import dev.dewy.nbt.tags.CompoundTag
import kotlin.math.abs

data class Size(val x: Int, val y: Int, val z: Int) {

    val volume: Int
        get() = x * y * z

    /**
     * Returns this size with only positive dimensions
     */
    fun normalized() = Size(abs(x), abs(y), abs(z))

    companion object {
        fun fromRegionNbt(region: CompoundTag): Size? {
            val sizeTag = region.getCompound("Size") ?: return null
            val x = sizeTag.getInt("x")?.int ?: return null
            val y = sizeTag.getInt("y")?.int ?: return null
            val z = sizeTag.getInt("z")?.int ?: return null
            return Size(x, y, z)
        }
    }
}
