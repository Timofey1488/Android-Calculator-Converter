package com.example.converter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }
    fun convertor_btn_menu(view: View){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun calculator_btn_menu(view: View){
        var intent = Intent(this, CalculatorActivity::class.java)
        startActivity(intent)
    }
}