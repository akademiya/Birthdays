package com.vadym.birthday.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class CreatePersonItemActivity: BaseActivity() {

    private val vm by viewModel<MainViewModel>()
    private val PERMISSION_REQUEST_CODE = 101
    private val PICK_IMAGE_REQUEST_CODE = 102
    private var newPhotoUri: String = ""
    private var age: String = "0"

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.create_item_person)

        val buttonSelectImage = findViewById<Button>(R.id.btn_select_photo)
        val newFirstName = findViewById<EditText>(R.id.create_first_name)
        val newLastName = findViewById<EditText>(R.id.create_last_name)
        val birthOfDate = findViewById<Button>(R.id.create_birth_of_date)
        val newGroup = findViewById<Spinner>(R.id.create_group)
        val cansel = findViewById<Button>(R.id.cansel_button)
        val save = findViewById<Button>(R.id.save_button)

        buttonSelectImage.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        newFirstName.filters = arrayOf(MainViewModel.LetterInputFilter())
        newLastName.filters = arrayOf(MainViewModel.LetterInputFilter())

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

        newFirstName.setOnFocusChangeListener { view, hasFocus ->
            etErrorListener(hasFocus, view)
        }

        newLastName.setOnFocusChangeListener { view, hasFocus ->
            etErrorListener(hasFocus, view)
        }


        save.setOnClickListener {
            vm.onSaveButtonClick(
                Person(
                    personFirstName = newFirstName.text.toString(),
                    personLastName = newLastName.text.toString(),
                    age = age,
                    group = newGroup.selectedItem.toString(),
                    personDayOfBirth = birthOfDate.text.toString(),
                    personPhoto = newPhotoUri
                )
            )
        }

        cansel.setOnClickListener {
            finish()
        }

        vm.saveSuccessLive.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Successful saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
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
                editText.error = "This row cannot be empty"
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                val newPhoto = findViewById<ImageView>(R.id.upload_img_person)
                newPhoto.setImageURI(selectedImageUri)
                newPhotoUri = selectedImageUri.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
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
                vm.validateDate(birthOfDate, date)
                vm.birthOfDateLive.observe(this) {
                    birthOfDate.text = it
                }
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun calculateAge(birthOfDate: String): String {
        // Define the date format (assuming yyyyMMdd format)
        val sdf = SimpleDateFormat("yyyyMMdd")
        val birthDate = sdf.parse(birthOfDate)  // Parse the birth date string to a Date object

        // Get the current date
        val today = Calendar.getInstance()

        // Get the birth date as a Calendar instance
        val birthDay = Calendar.getInstance().apply {
            time = birthDate
        }

        // Calculate age
        var age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)

        // If today's date is before the birth date, subtract one year
        if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age.toString()
    }

}