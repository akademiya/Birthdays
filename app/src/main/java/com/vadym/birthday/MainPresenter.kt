package com.vadym.birthday

import android.app.Application

class MainPresenter(mainView: MainActivity, applicationComponent: Application) : BasePresenter<MainActivity>(mainView) {
    init {
//        (applicationComponent as AndroidApplication).applicationComponent.inject(this)
    }

    private var counterClickSelectImg = 0F

    override fun onBindView() {}

    override fun onUnbindView() {}
}