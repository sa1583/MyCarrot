package fastcampus.aop.part2.mycarrot.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String,
) {
    constructor(): this("", "")
}
