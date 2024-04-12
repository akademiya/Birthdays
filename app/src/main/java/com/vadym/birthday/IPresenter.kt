package com.vadym.birthday

interface IPresenter<in V: Any> {
    fun bindView(view: V)
    fun unbindView(view: V)
}