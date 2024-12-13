package com.vadym.birthday.data.repository

import android.content.Context
import android.content.Intent
import com.vadym.birthday.domain.repository.IOpenViewToDoRepository

class OpenViewToDoRepository (private val context: Context) : IOpenViewToDoRepository {

    override fun openViewToCreateItem() {
//        context.startActivity(Intent(context, CreatePersonItemActivity::class.java))
    }

    override fun openViewToEditItem() {
        TODO("Not yet implemented")
    }

}