package com.cosmos.unreddit.parser

data class HtmlBlock(val block: Block, val type: BlockType) {
    enum class BlockType {
        TEXT, CODE, TABLE
    }
}
