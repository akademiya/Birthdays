package com.vadym.birthday.ui.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import com.vadym.birthday.ui.formatterDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CreatePersonItemActivity: BaseActivity() {

    private val vm by viewModel<MainViewModel>()

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.create_item_person)

        val newFirstName = findViewById<TextView>(R.id.create_first_name)
        val newLastName = findViewById<TextView>(R.id.create_last_name)
//        val newAge = findViewById<TextView>(R.id.create_age)
        val birthOfDate = findViewById<Button>(R.id.create_birth_of_date)
        val newPhoto = findViewById<ImageView>(R.id.create_img_person)
        val newGroup = findViewById<Spinner>(R.id.create_group)
        val uploadPhoto = findViewById<Button>(R.id.btn_select_photo)
        val cansel = findViewById<Button>(R.id.cansel_button)
        val save = findViewById<Button>(R.id.save_button)


        newLastName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        cansel.setOnClickListener {
            super.onBackPressed()
        }

        datePickerDialog(birthOfDate)

        ArrayAdapter.createFromResource(
            this,
            R.array.group_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newGroup.adapter = adapter
            newGroup.prompt = "Group"
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
            val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                val monthPlus = monthOfYear + 1
                val currentMonth = if (monthPlus.toString().length == 1) { "0$monthPlus" } else "$monthPlus"
                val date = "$year$currentMonth$dayOfMonth"
                birthOfDate.text = date.formatterDate()
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun getToday() : String {
        val calendar = Calendar.getInstance()
        return calendar.time.formatterDate()
    }

    enum class GroupName {
        PRESCHOOLERS,
        PRIMARY_SCHOOL,
        SECONDARY_SCHOOL,
        HIGH_SCHOOL,
        ADULTS
    }
}