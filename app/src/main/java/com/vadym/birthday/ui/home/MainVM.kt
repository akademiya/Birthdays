package com.vadym.birthday.ui.home

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.usecase.CreatePersonItemUseCase
import com.vadym.birthday.domain.usecase.FabButtonVisibilityUseCase
import com.vadym.birthday.domain.usecase.ListOfPersonUseCase

class MainVM(
    private val listPersonUseCase: ListOfPersonUseCase,
    private val fabButtonVisibilityUseCase: FabButtonVisibilityUseCase,
    private val createPersonItemUseCase: CreatePersonItemUseCase
) : ViewModel() {

    private val resultLiveMutable = MutableLiveData<Person>()
    val resultLive: LiveData<Person> = resultLiveMutable
    var fabIsVisible = MutableLiveData<Int>()

    init {
        Log.e("aaa", "VM created")
    }

    override fun onCleared() {
        Log.e("aaa", "VM cleared")
        super.onCleared()
    }

    fun getListPerson() {
        resultLiveMutable.value = listPersonUseCase.execute()
    }

    fun clickByToolbar() {
        if (fabButtonVisibilityUseCase.execute()) {
            fabIsVisible.value = View.VISIBLE
        } else fabIsVisible.value = View.GONE

    }

    fun clickByFab() {
        createPersonItemUseCase.execute()
    }

    enum class GroupName {
        PRESCHOOLERS,
        PRIMARY_SCHOOL,
        SECONDARY_SCHOOL,
        HIGH_SCHOOL,
        ADULTS
    }

}