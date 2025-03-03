package pt.isel.daw.imSystem.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}