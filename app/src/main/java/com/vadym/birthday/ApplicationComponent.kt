package com.vadym.birthday

import dagger.Component

//import dagger.Component

@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(modApplication: AndroidApplication)

    // VIEW
    fun inject(mainActivity: MainActivity)

    // PRESENTER
    fun inject(mainPresenter: MainPresenter)
}