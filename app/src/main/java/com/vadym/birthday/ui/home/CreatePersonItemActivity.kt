package com.vadym.birthday.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import com.vadym.birthday.ui.formatterDate
import com.vadym.birthday.ui.simpleFormatterDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CreatePersonItemActivity: BaseActivity() {

    private val vm by viewModel<MainViewModel>()
    private var birthDay: String = "00000000"
    private var age: String = "0"
    private var isEdit = false
    private lateinit var editedSharedPref: SharedPreferences
    private lateinit var position: SharedPreferences
    private lateinit var idEditablePerson: String
    private var count = 0
    private var lastChild: Int = 0

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.create_item_person)

        editedSharedPref = getSharedPreferences("Edit", MODE_PRIVATE)
        position = getSharedPreferences("Position", MODE_PRIVATE)
        val newFirstName = findViewById<EditText>(R.id.create_first_name)
        val newLastName = findViewById<EditText>(R.id.create_last_name)
        val birthOfDate = findViewById<Button>(R.id.create_birth_of_date)
        val newGroup = findViewById<Spinner>(R.id.create_group)
        val gender = findViewById<Spinner>(R.id.create_gender)
        val cansel = findViewById<Button>(R.id.cansel_button)
        val save = findViewById<Button>(R.id.save_button)
        vm.resultPersonListLive.observe(this) { personList ->
            lastChild = personList.lastIndex
        }
        val lastPosition = position.getInt("lastPosition", -1)


        isEdit = intent.getBooleanExtra("isEdit", false)


        if (!newFirstName.isFocused && newFirstName.text.isNullOrEmpty()) {
            newFirstName.error
        }

        datePickerDialog(birthOfDate)

        ArrayAdapter.createFromResource(
            this,
            R.array.group_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newGroup.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gender.adapter = adapter
        }

        newFirstName.setOnFocusChangeListener { view, hasFocus ->
            etErrorListener(hasFocus, view)
        }

        newLastName.setOnFocusChangeListener { view, hasFocus ->
            etErrorListener(hasFocus, view)
        }

        if (isEdit) {
            val person = intent.getSerializableExtra("person") as? Person
            person?.let {
                idEditablePerson = it.personId.toString()
                newFirstName.setText(person.personFirstName, TextView.BufferType.EDITABLE)
                newLastName.setText(person.personLastName, TextView.BufferType.EDITABLE)
                birthOfDate.apply {
                    text = person.personDayOfBirth?.formatterDate()
                    setTypeface(null, Typeface.BOLD)
                    isEnabled = false
                    setBackgroundColor(ContextCompat.getColor(this@CreatePersonItemActivity, R.color.black))
                }



                val genderOptions = listOf("Male", "Female")
                val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                gender.adapter = genderAdapter

                val genderPosition = genderOptions.indexOf(person.gender)
                if (genderPosition >= 0) {
                    gender.setSelection(genderPosition)
                }
                gender.isEnabled = false


                val groupOptions = listOf(
                    MainViewModel.GroupName.PRESCHOOLERS.title,
                    MainViewModel.GroupName.ELEMENTARY_SCHOOL.title,
                    MainViewModel.GroupName.SECONDARY_SCHOOL.title,
                    MainViewModel.GroupName.HIGH_SCHOOL.title,
                    MainViewModel.GroupName.ADULTS.title
                    )
                val groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupOptions)
                groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                newGroup.adapter = groupAdapter
                val groupPosition = groupOptions.indexOf(person.group)
                if (groupPosition >= 0) {
                    newGroup.setSelection(groupPosition)
                }

            }
            save.text = "update"
        }


        save.setOnClickListener {
            if (!isEdit) {
                vm.onSaveButtonClick(
                    Person(
                        personId = "id",
                        personFirstName = newFirstName.text.toString(),
                        personLastName = newLastName.text.toString(),
                        age = age,
                        group = newGroup.selectedItem.toString(),
                        gender = gender.selectedItem.toString(),
                        personDayOfBirth = birthDay,
                        position = lastPosition
                    )
                )
            } else {
                vm.onUpdateButtonClick(
                    Person(
                        personId = idEditablePerson,
                        personFirstName = newFirstName.text.toString(),
                        personLastName = newLastName.text.toString(),
                        group = newGroup.selectedItem.toString()
                    )
                )
                isEdit = false
            }

        }

        cansel.setOnClickListener {
            finish()
        }

        vm.saveSuccessLive.observe(this) { success ->
            if (success) {
                finish()
            } else {
                Toast.makeText(this, "Something wrong with saving", Toast.LENGTH_SHORT).show()
            }
        }

        vm.errorLive.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

    }

    private fun etErrorListener(hasFocus: Boolean, view: View?) {
        if (!hasFocus) {
            val editText = view as EditText
            if (editText.text.isNullOrEmpty()) {
                editText.error = "This row can not be empty"
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun datePickerDialog(birthOfDate: Button) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        birthOfDate.setOnClickListener {
            hideKeyboard(it)
            val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                val monthPlus = monthOfYear + 1
                val currentMonth = if (monthPlus.toString().length == 1) { "0$monthPlus" } else "$monthPlus"
                val date = "$year$currentMonth$dayOfMonth"
                age = calculateAge(date)
                birthDay = date
                birthOfDate.text = date.formatterDate()
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun calculateAge(birthOfDate: String): String {
        val today = Calendar.getInstance()
        val birthDay = Calendar.getInstance().apply {
            time = birthOfDate.simpleFormatterDate()
        }
        var age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age.toString()
    }

}