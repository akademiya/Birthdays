package com.vadym.birthday

abstract class BasePresenter<V : Any>(@Volatile var view: V? ) : IPresenter<V> {

    protected fun view(): V? {
        return view
    }

    override fun bindView(view: V) {
        if (this.view != null) onUnbindView()
        this.view = view
        onBindView()
    }

    override fun unbindView(view: V) {
        if (this.view === view) {
            onUnbindView()
            this.view = null
        }
    }

    protected abstract fun onBindView()
    protected abstract fun onUnbindView()
}