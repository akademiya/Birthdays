package com.vadym.birthday.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vadym.birthday.R

const val CORRECT_PASSWORD = "467874"

class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        val infoPassword = findViewById<ImageView>(R.id.info_password)

        infoPassword.setOnClickListener {
            Toast.makeText(this, "Навіщо потрібен пароль?" +
                    "\nЗаради захисту даних, якщо телефон потрапить у чужі руки." +
                    "\nНебесний код 1 + Небесний код 7", Toast.LENGTH_LONG).show()
        }

        val editTexts = listOf(
            findViewById<EditText>(R.id.et_digit_1).apply { transformationMethod = null },
            findViewById<EditText>(R.id.et_digit_2).apply { transformationMethod = null },
            findViewById<EditText>(R.id.et_digit_3).apply { transformationMethod = null },
            findViewById<EditText>(R.id.et_digit_4).apply { transformationMethod = null },
            findViewById<EditText>(R.id.et_digit_5).apply { transformationMethod = null },
            findViewById<EditText>(R.id.et_digit_6).apply { transformationMethod = null }
        )

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        } else {
                            validatePassword(editTexts)
                        }
                    }
                }
            })
        }

    }


    private fun validatePassword(editTexts: List<EditText>) {
        val enteredPassword = editTexts.joinToString("") { it.text.toString() }

        if (enteredPassword == CORRECT_PASSWORD) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Неправильний пароль. Спробуйте ще раз.", Toast.LENGTH_SHORT).show()
            editTexts.forEach { it.text.clear() }
            editTexts.first().requestFocus()
        }
    }

}