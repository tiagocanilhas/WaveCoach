package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import waveCoach.repository.Transaction
import waveCoach.repository.TransactionManager

private val jdbi =
    Jdbi.create(
        PGSimpleDataSource().apply {
            setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
        },
    ).configureWithAppRequirements()

fun testWithHandleAndRollback(block: (Handle) -> Unit) =
    jdbi.useTransaction<Exception> { handle ->
        block(handle)
        handle.rollback()
    }

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) =
    jdbi.useTransaction<Exception> { handle ->
        val transaction = JdbiTransaction(handle)

        val transactionManager =
            object : TransactionManager {
                override fun <R> run(block: (Transaction) -> R): R {
                    return block(transaction)
                }
            }
        block(transactionManager)

        handle.rollback()
    }
