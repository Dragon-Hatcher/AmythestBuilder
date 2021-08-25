fun chunkBitset(bits: LongArray, chunks: Int, chunkSize: Int): List<Int> {
    if (chunkSize > 32) throw IllegalArgumentException("chunkSize must be less than 32. Instead it was $chunkSize")

    val firstLongMask = (1L shl chunkSize) - 1

    return (0 until chunks).map { chunkNum ->
        val bitNum = chunkNum * chunkSize
        val longNum = bitNum / 64
        val longOffset = (bitNum % 64)

        var result = (bits[longNum] ushr longOffset) and firstLongMask

        if (longOffset + chunkSize > 64) {
            val secondLongNum = longNum + 1
            val secondChunkSize = longOffset + chunkSize - 64
            val firstChunkSize = chunkSize - secondChunkSize

            val secondLongMask = (1L shl secondChunkSize) - 1
            val secondSection = (bits[secondLongNum] and secondLongMask) shl firstChunkSize

            result = result or (secondSection shl firstChunkSize)
        }

        result.toInt()
    }
}