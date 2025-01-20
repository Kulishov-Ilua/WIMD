package ru.kulishov.wimd

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform