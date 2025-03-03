package pt.isel.daw.imSystem.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.imSystem.repository.ChannelsRepository
import pt.isel.daw.imSystem.repository.MessagesRepository
import pt.isel.daw.imSystem.repository.Transaction
import pt.isel.daw.imSystem.repository.UsersRepository

class JdbiTransaction(
    private val handle: Handle,
) : Transaction {
    override val usersRepository: UsersRepository = JdbiUsersRepository(handle)
    override val channelsRepository: ChannelsRepository = JdbiChannelsRepository(handle)
    override val messagesRepository: MessagesRepository = JdbiMessagesRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}