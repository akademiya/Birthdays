package com.vadym.birthday.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainVM(): ViewModel() {

    private val resultLiveMutable = MutableLiveData<String>()
//    val resultLive: LiveData<String> = resultLiveMutable
    init {
        Log.e("aaa", "VM created")
    }

    override fun onCleared() {
        Log.e("aaa", "VM cleared")
        super.onCleared()
    }

//    fun getResultLive() : LiveData<String> {
//        return resultLive
//    }

    fun load() {
        resultLiveMutable.value = "re"
    }

}