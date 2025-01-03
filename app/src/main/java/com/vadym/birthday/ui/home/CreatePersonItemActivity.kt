package com.vadym.birthday.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import com.vadym.birthday.ui.formatterDate
import com.vadym.birthday.ui.simpleFormatterDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class CreatePersonItemActivity: BaseActivity() {

    private val vm by viewModel<MainViewModel>()
    private val PERMISSION_REQUEST_CODE = 101
    private val PICK_IMAGE_REQUEST_CODE = 102
    private var newPhotoUri: String = ""
    private var birthDay: String = "00000000"
    private var age: String = "0"
    private var isEdit = false
    private lateinit var editedSharedPref: SharedPreferences
    private lateinit var position: SharedPreferences
    private lateinit var newPhoto: ImageView
    private lateinit var idEditablePerson: String
    private var count = 0
    private var lastChild: Int = 0

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.create_item_person)

        editedSharedPref = getSharedPreferences("Edit", MODE_PRIVATE)
        position = getSharedPreferences("Position", MODE_PRIVATE)
        val buttonSelectImage = findViewById<Button>(R.id.btn_select_photo)
        val newFirstName = findViewById<EditText>(R.id.create_first_name)
        val newLastName = findViewById<EditText>(R.id.create_last_name)
        val birthOfDate = findViewById<Button>(R.id.create_birth_of_date)
        val newGroup = findViewById<Spinner>(R.id.create_group)
        val gender = findViewById<Spinner>(R.id.create_gender)
        val cansel = findViewById<Button>(R.id.cansel_button)
        val save = findViewById<Button>(R.id.save_button)
        newPhoto = findViewById(R.id.upload_img_person)
        vm.resultPersonListLive.observe(this) { personList ->
            lastChild = personList.lastIndex
        }
        val lastPosition = position.getInt("lastPosition", -1)

        buttonSelectImage.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

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


                Glide.with(this)
                    .load(it.personPhoto)
                    .circleCrop()
                    .error(R.drawable.ic_person)
                    .into(newPhoto)
                newPhotoUri = it.personPhoto.toString()

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
                        personPhoto = newPhotoUri,
                        position = lastPosition
                    )
                )
            } else {
                vm.onUpdateButtonClick(
                    Person(
                        personId = idEditablePerson,
                        personFirstName = newFirstName.text.toString(),
                        personLastName = newLastName.text.toString(),
                        group = newGroup.selectedItem.toString(),
                        personPhoto = newPhotoUri
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
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else Toast.makeText(this, "Ви можете керувати дозволами в налаштуваннях вашого дивайса", Toast.LENGTH_LONG).show()
        }
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
                newPhoto = findViewById(R.id.upload_img_person)
                newPhoto.setImageURI(selectedImageUri)
                newPhotoUri = selectedImageUri.toString()
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
//        val sdf = SimpleDateFormat("yyyyMMdd")
//        val birthDate = sdf.parse(birthOfDate)
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