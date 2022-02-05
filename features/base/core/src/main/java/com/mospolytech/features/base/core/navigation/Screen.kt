package com.mospolytech.features.base.core.navigation

open class Screen(
    args: Map<String, String> = emptyMap()
) : BaseScreen(args) {
    constructor(
        vararg args: Pair<String, Any>
    ) : this(args.associate { it.first to it.second.toString() })

    object Args {
        const val ShowStatusBar = "showStatusBar"
    }
}

open class BaseScreen(
    args: Map<String, String> = emptyMap()
) {
    constructor(
        vararg args: Pair<String, String>
    ) : this(args.toMap())

    private val _args: MutableMap<String, String> = args.toMutableMap()
    val args: Map<String, String> = _args

    protected fun setArgs(vararg args: Pair<String, String>) {
        _args.putAll(args)
    }

    protected fun setArgsIfAbsent(vararg args: Pair<String, String>) {
        args.forEach {
            _args.putIfAbsent(it.first, it.second)
        }
    }


    inline fun <reified T> getArg(key: String): T {
        return when(T::class) {
            String::class -> args[key] as T
            Int::class -> args[key]!!.toInt() as T
            else -> throw IllegalArgumentException()
        }
    }
}