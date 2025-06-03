package waveCoach.utils

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()

    data class Right<out R>(val value: R) : Either<Nothing, R>()
}

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>

fun <R> success(value: R) = Success(value)

fun <L> failure(error: L) = Failure(error)
