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
import com.vadym.birthday.domain.usecase.DeleteItemUseCase
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
    private val calculateBirthdayUseCase: CalculateBirthdayUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    private val allPersons = mutableListOf<Person>()

    private val resultLiveMutable = MutableLiveData<List<Person>>()
    val resultPersonListLive: LiveData<List<Person>> get() = resultLiveMutable

    private val _filterListMutable = MutableLiveData<List<Person>>()
    val filterPersonListLive: LiveData<List<Person>> get() = _filterListMutable

    private var _fabIsVisible = MutableLiveData<Int>()
    val fabIsVisible: LiveData<Int> get() = _fabIsVisible

    private var _isDevMode = MutableLiveData<Boolean>()
    val isDevMode: LiveData<Boolean> get() = _isDevMode

    private var _errorLive = MutableLiveData<String>()
    val errorLive: LiveData<String> = _errorLive

    private var _saveSuccessLive = MutableLiveData<Boolean>()
    val saveSuccessLive: LiveData<Boolean> get() = _saveSuccessLive

    var birthOfDateLive = MutableLiveData<String>()

    private var _isBirthTodayLive = MutableLiveData<Boolean>()
    val isBirthTodayLive: LiveData<Boolean> get() = _isBirthTodayLive

    private var _isBirthWeekLive = MutableLiveData<Boolean>()
    val isBirthWeekLive: LiveData<Boolean> get() = _isBirthWeekLive

    private var _isPersonDeleted = MutableLiveData<Boolean>()
    val isPersonDeleted: LiveData<Boolean> get() = _isPersonDeleted

    init {}

    override fun onCleared() {
        super.onCleared()
    }

    fun getListPerson() {
        listPersonUseCase.execute { personList ->
            resultLiveMutable.value = personList
            setPersons(personList)
        }
    }

    fun clickByToolbar() {
        if (fabButtonVisibilityUseCase.execute()) {
            _fabIsVisible.value = View.VISIBLE
            _isDevMode.value = true
        } else {
            _fabIsVisible.value = View.GONE
            _isDevMode.value = false
        }
    }

    fun onRemovePersonClick(personId: String) {
        deleteItemUseCase.execute(personId) { isDeleted ->
            _isPersonDeleted.value = isDeleted
        }
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


    fun isBirthToday(personId: String, birthOfDate: String) {
        _isBirthTodayLive.value = calculateBirthdayUseCase.execute(personId, birthOfDate)
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



    fun setPersons(persons: List<Person>) {
        allPersons.clear()
        allPersons.addAll(persons)
        resultLiveMutable.value = allPersons // Initialize with the full list
    }

    fun filterListByCategory(category: String) {
        val filteredList = when (category) {
            "today" -> allPersons.filter { calculateBirthdayUseCase.execute(it.personId.toString(), it.personDayOfBirth.toString()) }
            "week" -> allPersons.filter { calculateBirthdayUseCase.isBirthdayInThisWeek(it.personDayOfBirth.toString()) }
            "preschoolers" -> allPersons.filter { it.age!!.toInt() in 0..6 }
            "primary_school" -> allPersons.filter { it.age!!.toInt() in 7..11 }
            "secondary_school" -> allPersons.filter { it.age!!.toInt() in 12..16 }
            "high_school" -> allPersons.filter { it.age!!.toInt() in 17..19 }
            "adults" -> allPersons.filter { it.age!!.toInt() > 20 }
            else -> allPersons
        }
        resultLiveMutable.value = filteredList
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