package com.mathias8dev.webfluxblognotificationsender.utils


inline fun <T> tryOrNull(function: () -> T): T? = runCatching {
    function()
}.getOrNull()
