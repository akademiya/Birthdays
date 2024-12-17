package com.vadym.birthday.ui.home

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.usecase.CalculateBirthdayUseCase
import com.vadym.birthday.domain.usecase.CreatePersonItemUseCase
import com.vadym.birthday.domain.usecase.DeleteItemUseCase
import com.vadym.birthday.domain.usecase.FabButtonVisibilityUseCase
import com.vadym.birthday.domain.usecase.ListOfPersonUseCase
import com.vadym.birthday.domain.usecase.SavePersonDataUseCase
import com.vadym.birthday.domain.usecase.UpdatePersonDataUseCase
import com.vadym.birthday.domain.usecase.UpdatePositionListUseCase
import com.vadym.birthday.ui.formatterDate
import java.util.Calendar

class MainViewModel(
    private val listPersonUseCase: ListOfPersonUseCase,
    private val fabButtonVisibilityUseCase: FabButtonVisibilityUseCase,
    private val createPersonItemUseCase: CreatePersonItemUseCase,
    private val savePersonDataUseCase: SavePersonDataUseCase,
    private val updatePersonDataUseCase: UpdatePersonDataUseCase,
    private val calculateBirthdayUseCase: CalculateBirthdayUseCase,
    private val updatePositionListUseCase: UpdatePositionListUseCase,
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
            resultLiveMutable.value = personList.sortedBy { person: Person -> person.position }
            setPersons(personList.sortedBy { person: Person -> person.position })
        }
    }


    fun updatePosition(updatedList: List<Person>) {
        updatePositionListUseCase.execute(updatedList)
    }

    fun clickByToolbar() {
        if (fabButtonVisibilityUseCase.execute()) {
            _fabIsVisible.value = View.VISIBLE
            _isDevMode.value = true
            listPersonUseCase.execute { personList ->
                personList.forEach { item ->
                    item.isDevMode = true
                }
                setPersons(personList)
            }
        } else {
            _fabIsVisible.value = View.GONE
            _isDevMode.value = false
            listPersonUseCase.execute { personList ->
                personList.forEach { item ->
                    item.isDevMode = false
                }
                setPersons(personList)
            }
        }
    }

    fun onRemovePersonClick(personId: String) {
        deleteItemUseCase.execute(personId) { isDeleted ->
            _isPersonDeleted.postValue(isDeleted)
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

    fun onUpdateButtonClick(data: Person) {
        _saveSuccessLive.value = updatePersonDataUseCase.execute(data)
    }


    fun isBirthToday(personId: String, birthOfDate: String) {
        _isBirthTodayLive.value = calculateBirthdayUseCase.isTodayMyBirthday(birthOfDate)
        calculateBirthdayUseCase.execute(personId, birthOfDate)
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
        resultLiveMutable.value = allPersons
    }

    fun filterListByCategory(category: String) {
        val filteredList = when (category) {
//            "today" -> allPersons.filter { calculateBirthdayUseCase.isTodayMyBirthday(it.personDayOfBirth.toString()) }
//            "week" -> allPersons.filter { calculateBirthdayUseCase.isBirthdayInThisWeek(it.personDayOfBirth.toString()) }

            "today" -> allPersons.filter {
                !it.personDayOfBirth.isNullOrEmpty() &&
                        calculateBirthdayUseCase.isTodayMyBirthday(it.personDayOfBirth!!)
            }
            "week" -> allPersons.filter {
                !it.personDayOfBirth.isNullOrEmpty() &&
                        calculateBirthdayUseCase.isBirthdayInThisWeek(it.personDayOfBirth!!)
            }
            GroupName.PRESCHOOLERS.title -> allPersons.filter { it.group.equals(GroupName.PRESCHOOLERS.title) }
            GroupName.ELEMENTARY_SCHOOL.title -> allPersons.filter { it.group.equals(GroupName.ELEMENTARY_SCHOOL.title) }
            GroupName.SECONDARY_SCHOOL.title -> allPersons.filter { it.group.equals(GroupName.SECONDARY_SCHOOL.title) }
            GroupName.HIGH_SCHOOL.title -> allPersons.filter { it.group.equals(GroupName.HIGH_SCHOOL.title) }
            GroupName.ADULTS.title -> allPersons.filter { it.group.equals(GroupName.ADULTS.title) }
            else -> allPersons
        }
        resultLiveMutable.value = filteredList
    }

    enum class GroupName(val title: String) {
        PRESCHOOLERS("Preschoolers"),
        ELEMENTARY_SCHOOL("Elementary School"),
        SECONDARY_SCHOOL("Secondary School"),
        HIGH_SCHOOL("High School"),
        ADULTS("Adults")
    }

    enum class Errors(val textError: String) {
        EMPTY_FIRST_NAME("Enter the First name"),
        EMPTY_LAST_NAME("Enter the Last name"),
        NOT_VALID_DIGIT("Check the correct entered value"),
        DATE_OF_BIRTH("Date of birth must be choose"),
        WRONG("Something wrong with validate row")
    }

}