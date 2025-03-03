package pt.isel.daw.imSystem.repository

interface Transaction {
    val usersRepository: UsersRepository
    val channelsRepository: ChannelsRepository
    val messagesRepository: MessagesRepository

    // other repository types
    fun rollback()
}