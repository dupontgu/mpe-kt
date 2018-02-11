@file:JsModule("events")
package com.dupont

external class EventEmitter : IEventEmitter {
    override fun on(event: String, listener: Function<Unit>)

    override fun once(event: String, listener: Function<Unit>)

    override fun removeListener(event: String, listener: Function<Unit>)

    override fun removeAllListeners(event: String?)

    override fun setMaxListeners(n: Int)

    override fun listeners(event: String)

    override fun emit(event: String, vararg params: Any)

    override fun addListener(event: String, listener: Function<Unit>)
}

external interface IEventEmitter {
    @JsName("addListener")
    fun addListener(event: String, listener: Function<Unit>)
    @JsName("on")
    fun on(event: String, listener: Function<Unit>)
    @JsName("once")
    fun once(event: String, listener: Function<Unit>)
    @JsName("removeListener")
    fun removeListener(event: String, listener: Function<Unit>)
    @JsName("removeAllListeners")
    fun removeAllListeners(event: String?)
    @JsName("setMaxListeners")
    fun setMaxListeners(n: Int)
    @JsName("listeners")
    fun listeners(event: String)
    @JsName("emit")
    fun emit(event: String, vararg params: Any)
}

