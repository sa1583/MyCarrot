package fastcampus.aop.part2.mycarrot.chatlist

data class ChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    constructor(): this("", "", "", 0)
}