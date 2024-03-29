package com.omega_r.adapters


/**
 * Created by Anton Knyazev on 13.04.2019.
 */
interface OmegaIdentifiable<T> {

    val id: T

    val idAsLong: Long
        get() = when (val id = id) {
            is String -> id.toLongHash()
            is Int -> id.toLong()
            is Long -> id
            else -> id.hashCode().toLong()
        }
}

fun <T : OmegaIdentifiable<I>, I> Iterable<T>.contains(id: I): Boolean {
    return firstOrNull(predicate = { it.id == id }) != null
}

@Suppress("unused")
fun <T : OmegaIdentifiable<I>, I> Iterable<T>.indexOfFirst(id: I): Int {
    return indexOfFirst(predicate = { it.id == id })
}

@Suppress("unused")
fun <T : OmegaIdentifiable<I>, I> Iterable<T>.indexOfLast(id: I): Int {
    return indexOfLast(predicate = { it.id == id })
}

@Suppress("unused")
fun <T : OmegaIdentifiable<I>, I> Iterable<T>.firstOrNull(id: I): T? {
    return firstOrNull(predicate = { it.id == id })
}

fun <T : OmegaIdentifiable<I>, I> Iterable<T>.filterById(id: I) = filter { it.id == id }