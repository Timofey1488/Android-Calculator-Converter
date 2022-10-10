package com.example.converter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.converter.UI.DataModel
import com.example.converter.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val dataModel: DataModel by viewModels()
    lateinit var binding: ActivityMainBinding
    private var proTemp:Boolean  = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MAIN = this

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.currencyFragment,
            R.id.lengthFragment,
            R.id.speedFragment
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }
    fun DoubleZero(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        if(prevText.text.length.equals(0))
            dataModel.message.value = ""
        else
            dataModel.message.value = "00"
    }
    fun ZeroNumber(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        if(prevText.text.length == 1 && prevText.text[0].equals('0'))
            dataModel.message.value = ""
        else
            dataModel.message.value = "0"
    }
    fun OneNumber(view: View){
        dataModel.message.value = "1"
    }
    fun TwoNumber(view: View){
        dataModel.message.value = "2"
    }
    fun ThreeNumber(view: View){
        dataModel.message.value = "3"
    }
    fun FourNumber(view: View){
        dataModel.message.value = "4"
    }
    fun FiveNumber(view: View){
        dataModel.message.value = "5"
    }
    fun SixNumber(view: View){
        dataModel.message.value = "6"
    }
    fun SevenNumber(view: View){
        dataModel.message.value = "7"
    }
    fun EightNumber(view: View){
        dataModel.message.value = "8"
    }
    fun NineNumber(view: View){
        dataModel.message.value = "9"
    }
    fun PointNumber(view: View){
        var temp = false
        val prevText:EditText = findViewById(R.id.PrevText)
        for (i in prevText.text){
            if(i == '.')
                temp = true
        }
        if(prevText.text.isEmpty()){
            dataModel.message.value = "0."
        }
        else if(!temp)
            dataModel.message.value = "."
    }
    fun CNumber(view: View){
        dataModel.delete.value = ""
        dataModel.paste.value = ""
    }
    fun CircleNumber(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        val length = prevText.text.length
        val pos: Int = prevText.getSelectionStart()
        if (pos > 0){
            val text: String = prevText.text.subSequence(0, length-1).toString()
            dataModel.delete.value = text
            prevText.setSelection(pos-1)
        }
    }

    fun pasteText(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
//        val myClipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        var resultString: String = ""
//        val clipData: ClipData? = myClipboard.getPrimaryClip()
//        val count = clipData?.itemCount
//        for (i in 0 until count!!) {
//            val item = clipData.getItemAt(i);
//
//            val text = item.getText().toString()
//            if(item.getText().isDigitsOnly())
//                resultString += text
//        }
//        dataModel.delete.value = resultString

        dataModel.copy.observe(this as LifecycleOwner) {
            prevText.append(it)
        }

    }
    fun copyTextAfter(view: View) {
        val afterText: EditText = findViewById(R.id.AfterText)
        val Str: String = afterText.text.toString()
        if (Str.isNotEmpty()) {
            dataModel.copy.value = afterText.text.toString()
        } else {
            Toast.makeText(this, "Please Enter some text", Toast.LENGTH_SHORT).show()
        }
    }
    fun copyText(view: View){
       val prevText:EditText = findViewById(R.id.PrevText)
        val Str: String = prevText.text.toString()
        if(Str.isNotEmpty()){
            dataModel.copy.value = prevText.text.toString()
        }
        else
        {
            Toast.makeText(this,"Please Enter some text", Toast.LENGTH_SHORT).show()
        }

//        val Str: String = prevText.text.toString()
//        if(Str.isNotEmpty()){
//            val myClipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val myClip: ClipData = ClipData.newPlainText("Label", Str)
//            myClipboard.setPrimaryClip(myClip).toString()
//        }
//        else
//        {
//            Toast.makeText(this,"Please Enter some text", Toast.LENGTH_SHORT).show()
//        }


    }

    fun Swap(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevSpinner:Spinner = findViewById(R.id.SpinnerBefore)
        val afterSpinner:Spinner = findViewById(R.id.SpinnerAfter)
        val prevSelect = prevSpinner.selectedItemPosition
        val afterSelect = afterSpinner.selectedItemPosition
        dataModel.spinBeforeSet.value = afterSelect.toString()
        dataModel.spinAfterSet.value = prevSelect.toString()

        val buffer:String = prevText.text.toString()
        val strPrev:String = afterText.text.toString()
        dataModel.delete.value = strPrev
        dataModel.paste.value = buffer

    }
    fun ProButton(view: View){
        if(proTemp)
        {
            proTemp = false
            dataModel.proButton.value = proTemp.toString()
        }
        else {
            proTemp = true
            dataModel.proButton.value = proTemp.toString()
        }
    }
    fun EqualButton(view:View){
        val prevSpinner:Spinner = findViewById(R.id.SpinnerBefore)
        val afterSpinner:Spinner = findViewById(R.id.SpinnerAfter)
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevText:EditText = findViewById(R.id.PrevText)
        if(prevText.text.isNotEmpty()){
            when(prevSpinner.selectedItem.toString()) {
                //Length
                "мм" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "мм" -> dataModel.paste.value = prevText.text.toString()
                        "см" -> dataModel.paste.value = (prevText.text.toString().toFloat() / 10).toString()
                        "м" ->dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 1000).toString()
                    }
                }
                "см" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "мм" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 10).toString()
                        "см" -> dataModel.paste.value = prevText.text.toString()
                        "м" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 100).toString()
                    }
                }
                "м" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "мм" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 1000).toString()
                        "см" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 100).toString()
                        "м" -> dataModel.paste.value = prevText.text.toString()
                    }
                }

                //Currency
                "USD" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "USD" -> dataModel.paste.value = prevText.text.toString()
                        "BYN" -> dataModel.paste.value = (prevText.text.toString().toFloat() * 2.53).toString()
                        "RUB" ->dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 62.35).toString()
                    }
                }
                "BYN" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "USD" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 2.53).toString()
                        "BYN" -> dataModel.paste.value = prevText.text.toString()
                        "RUB" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 24.60).toString()
                    }
                }
                "RUB" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "USD" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 0.016).toString()
                        "BYN" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 0.041).toString()
                        "RUB" -> dataModel.paste.value = prevText.text.toString()
                    }
                }
                //Speed
                "м/c" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "м/c" -> dataModel.paste.value = prevText.text.toString()
                        "км/ч" -> dataModel.paste.value = (prevText.text.toString().toFloat() * 3.6).toString()
                        "ф/с" ->dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 3.281).toString()
                    }
                }
                "км/ч" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "м/c" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 3.6).toString()
                        "км/ч" -> dataModel.paste.value = prevText.text.toString()
                        "ф/с" -> dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 1.097).toString()
                    }
                }
                "ф/с" -> {
                    when (afterSpinner.selectedItem.toString()) {
                        "м/с" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() / 3.281).toString()
                        "км/ч" ->  dataModel.paste.value =
                            (prevText.text.toString().toFloat() * 1.097).toString()
                        "ф/с" -> dataModel.paste.value = prevText.text.toString()
                    }
                }
            }
        }
        else
            dataModel.paste.value = ""

    }

}
