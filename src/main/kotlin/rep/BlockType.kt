package rep

import dev.dewy.nbt.tags.CompoundTag

data class BlockType(private val name: String) {

    override fun toString() = name

    companion object {
        fun fromCompoundTag(tag: CompoundTag): BlockType? {
            val name = tag.getString("Name")?.value ?: return null
            return BlockType(name)
        }
    }
}