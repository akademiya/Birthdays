package com.vadym.birthday.ui.home

import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.usecase.CalculateBirthdayUseCase
import com.vadym.birthday.domain.usecase.CreatePersonItemUseCase
import com.vadym.birthday.domain.usecase.FabButtonVisibilityUseCase
import com.vadym.birthday.domain.usecase.ListOfPersonUseCase
import com.vadym.birthday.domain.usecase.SavePersonDataUseCase
import com.vadym.birthday.ui.formatterDate
import java.util.Calendar

class MainViewModel(
    private val listPersonUseCase: ListOfPersonUseCase,
    private val fabButtonVisibilityUseCase: FabButtonVisibilityUseCase,
    private val createPersonItemUseCase: CreatePersonItemUseCase,
    private val savePersonDataUseCase: SavePersonDataUseCase,
    private val calculateBirthdayUseCase: CalculateBirthdayUseCase
) : ViewModel() {

    private val resultLiveMutable = MutableLiveData<List<Person>>()
    val resultPersonListLive: LiveData<List<Person>> get() = resultLiveMutable

    private var _fabIsVisible = MutableLiveData<Int>()
    val fabIsVisible: LiveData<Int> get() = _fabIsVisible

    private var _errorLive = MutableLiveData<String>()
    val errorLive: LiveData<String> = _errorLive

    private var _saveSuccessLive = MutableLiveData<Boolean>()
    val saveSuccessLive: LiveData<Boolean> get() = _saveSuccessLive

    var birthOfDateLive = MutableLiveData<String>()

    private var _isBirthTodayLive = MutableLiveData<Boolean>()
    val isBirthTodayLive: LiveData<Boolean> get() = _isBirthTodayLive

    private var _isBirthWeekLive = MutableLiveData<Boolean>()
    val isBirthWeekLive: LiveData<Boolean> get() = _isBirthWeekLive

    init {
        Log.e("aaa", "VM created")
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getListPerson() {
        resultLiveMutable.value = listPersonUseCase.execute()
    }

    fun clickByToolbar() {
        if (fabButtonVisibilityUseCase.execute()) {
            _fabIsVisible.value = View.VISIBLE
        } else _fabIsVisible.value = View.GONE
    }

    fun clickByFab() {
        createPersonItemUseCase.execute()
    }

    private fun errorsCheckIn(data: Person): Boolean {
        when {
            data.personFirstName.isNullOrEmpty() -> _errorLive.value = Errors.EMPTY_FIRST_NAME.textError
            data.personLastName.isNullOrEmpty() -> _errorLive.value = Errors.EMPTY_LAST_NAME.textError
            data.personDayOfBirth.isNullOrEmpty() -> _errorLive.value = Errors.DATE_OF_BIRTH.textError
            else -> _errorLive.value = Errors.WRONG.textError
        }
        return data.personFirstName.isNullOrEmpty()
                || data.personLastName.isNullOrEmpty()
                || data.personDayOfBirth.isNullOrEmpty()
    }

//    fun validateEditTextRow(newFirstName: EditText, newLastName: EditText) {
//        if (!hasFocus) {
//            val editText = view as EditText
//            if (editText.text.isNullOrEmpty()) {
////                editText.error = "This row cannot be empty"
//                _errorLive.value = Errors.EMPTY_ROW_FiRST_NAME
//            }
//        }
//    }

    fun onSaveButtonClick(data: Person) {
        if(!errorsCheckIn(data)) {
            _saveSuccessLive.value = savePersonDataUseCase.execute(data)
        } else _saveSuccessLive.value = true
    }


    fun isBirthToday(birthOfDate: String) {
        _isBirthTodayLive.value = calculateBirthdayUseCase.execute(birthOfDate)
    }

    fun isBirthOnWeek(birthOfDate: String) {
        _isBirthWeekLive.value = calculateBirthdayUseCase.isBirthdayInThisWeek(birthOfDate)
    }

    fun validateDate(birthOfDate: Button, date: String) {
        birthOfDateLive.value = if (birthOfDate.text.isNullOrEmpty()) {
            getToday()
        } else {
            date
        }
    }

    private fun getToday() : String {
        val calendar = Calendar.getInstance()
        return calendar.time.formatterDate()
    }

    enum class GroupName(val title: String) {
        PRESCHOOLERS("preschoolers"),
        PRIMARY_SCHOOL("primary-school"),
        SECONDARY_SCHOOL("secondary-school"),
        HIGH_SCHOOL("high-school"),
        ADULTS("adults")
    }

    enum class Errors(val textError: String) {
        EMPTY_FIRST_NAME("Enter the First name"),
        EMPTY_LAST_NAME("Enter the Last name"),
        NOT_VALID_DIGIT("Check the correct entered value"),
        DATE_OF_BIRTH("Date of birth must be choose"),
        WRONG("Something wrong")
    }

    class LetterInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (source == null) {
                return null
            }

            for (i in start until end) {
                if (!source[i].isLetter()) {
                    return ""
                }
            }
            return null
        }
    }

}