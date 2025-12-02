package com.itschristmas.data.repositoryImpl.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> ioCatching(block: suspend () -> T): Result<T> =
    runCatching { withContext(Dispatchers.IO) { block() } }