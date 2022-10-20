package com.example.converter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.example.converter.UI.DataModel
import com.example.converter.UI.NumPadCalculator
import com.example.converter.UI.NumPadCalculatorPro
import java.math.BigDecimal
import kotlin.collections.ArrayDeque


class CalculatorActivity : AppCompatActivity() {
    private val dataModel: DataModel by viewModels()
    private var operation: Boolean = false
    private var equalCheck: Boolean = false
    private var bracketOpenCount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_calculator)
        getSupportActionBar()?.hide();

    }
    override fun onSaveInstanceState(outState: Bundle) {

        val prevText:EditText = findViewById(R.id.PrevText)
        val afterText:EditText = findViewById(R.id.AfterText)
        outState.run{
            putString("prevText", prevText.text.toString())
            putString("afterText", afterText.text.toString())
            putInt("pos", prevText.getSelectionStart())
            putBoolean("operation", operation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val prevText:EditText = findViewById(R.id.PrevText)
        val afterText:EditText = findViewById(R.id.AfterText)
        prevText.setText(savedInstanceState.getString("prevText"))
        afterText.setText(savedInstanceState.getString("afterText"))
        prevText.setSelection(savedInstanceState.getInt("pos"))
        operation = savedInstanceState.getBoolean("operation")
        super.onRestoreInstanceState(savedInstanceState)
    }
    //RPN

    fun StringToRPN(str:String):String{
        var final:String = ""
        var stack = ArrayDeque<Char>()
        var priority:Int
        for(i in 0 until str.length){
            priority = getPriority(str[i])
            if(priority == 0)
                final +=str[i]
            if(priority == 1)
                stack.addLast(str[i])
            if(priority > 1){
                final += " "
                while(!stack.isEmpty()){
                    if(getPriority(stack.last()) >= priority)
                        final += stack.removeLast()
                    else break
                }
                stack.addLast(str[i])
            }
            if(priority == -1){
                final += " "
                while(getPriority(stack.last()) != 1)
                    final += stack.removeLast()
                stack.removeLast()
            }
        }
        while(!stack.isEmpty())
            final += stack.removeLast()
        return final
    }

    fun RPNToFinalString(str:String):Double{
        var operand:String = ""
        var stack = ArrayDeque<Double>()
        var temp:Int = 0
        for(i in 0 until str.length){
            if(temp > 0 && temp > i) {
                continue
            }
            temp = i
            if(str[i] == ' ') continue
            if(getPriority(str[i]) == 0){

                while(str[temp] != ' ' && getPriority(str[temp]) == 0){
                    operand += str[temp]
                    temp += 1
                }
                stack.addLast(operand.toDouble())
                operand = ""

            }
            if(getPriority(str[i]) > 1){
                var a:Double = stack.removeLast()
                var b:Double = stack.removeLast()
                if(str[i] == '+')
                    stack.addLast(b+a)
                if(str[i] == '-')
                    stack.addLast(b-a)
                if(str[i] == '*')
                    stack.addLast(b*a)
                if(str[i] == '/')
                    stack.addLast(b/a)
            }
        }
        return stack.removeLast()
    }
    fun getPriority(symbol:Char):Int{
        if (symbol== '*' || symbol == '/') return 3
        else if(symbol== '+' || symbol == '-') return 2
        else if(symbol== '(') return 1
        else if(symbol== ')') return -1
        else return 0
    }
    //Calculator Code
    fun equalButton(view: View){
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevText:EditText = findViewById(R.id.PrevText)
        if(equalCheck)
            dataModel.paste.value = ""

        if(!prevText.text.isEmpty())
            dataModel.change.value = prevText.text.toString()
        val result:Double =  RPNToFinalString(StringToRPN(afterText.text.toString()))
        dataModel.delete.value = result.toString()
        equalCheck = true
    }

    fun proButtonCalculator(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.num_pad_fragment_calculator, NumPadCalculatorPro.newInstance())
            .commit()
    }

    fun notProButtonCalculator(view: View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.num_pad_fragment_calculator, NumPadCalculator.newInstance())
            .commit()
    }

    fun DoubleZero(view: View){
        val prevText: EditText = findViewById(R.id.PrevText)
        if(prevText.text.length.equals(0))
            Toast.makeText(this,"Lenght = 0", Toast.LENGTH_SHORT).show()
        else
            dataModel.message.value = "00"
    }
    fun SavePosition(text: String){
        if(operation)
            operationClear()
        operation = false
        val prevText:EditText = findViewById(R.id.PrevText)
        val pos: Int = prevText.getSelectionStart()
        var buildText = StringBuilder(prevText.text.toString())
        val lenght = prevText.length()
        if(lenght<16){
            buildText = buildText.apply{insert(pos, text)}
            prevText.setText(buildText)
            prevText.setSelection(pos+1)
        }
        else{
            Toast.makeText(this,"Lenght > 16", Toast.LENGTH_SHORT).show()
        }
    }

    fun ZeroNumber(view: View){
        if(operation)
            operationClear()
        operation = false
        val prevText:EditText = findViewById(R.id.PrevText)
        if(prevText.text.length == 0){
            dataModel.message.value = "0"
            return
        }
        if(prevText.text.length == 1 && prevText.text[0].equals('0'))
            dataModel.message.value = ""
        else
            dataModel.message.value = "0"
    }
    fun OneNumber(view: View){
        SavePosition("1")

    }
    fun TwoNumber(view: View){
        SavePosition("2")
    }
    fun ThreeNumber(view: View){
        SavePosition("3")
    }
    fun FourNumber(view: View){
        SavePosition("4")
    }
    fun FiveNumber(view: View){
        SavePosition("5")
    }
    fun SixNumber(view: View){
        SavePosition("6")
    }
    fun SevenNumber(view: View){
        SavePosition("7")
    }
    fun EightNumber(view: View){
        SavePosition("8")
    }
    fun NineNumber(view: View){
        SavePosition("9")
    }
    fun PointNumber(view: View){
        var temp = false
        val prevText:EditText = findViewById(R.id.PrevText)
        for (i in prevText.text){
            if(i == '.')
                temp = true
        }
        if(prevText.text.isEmpty()){
            SavePosition("0.")
            val pos: Int = prevText.getSelectionStart()
            prevText.setSelection(pos+1)
        }
        else if(!temp)
            SavePosition(".")
    }
    fun CNumber(view: View){
        dataModel.delete.value = ""
        dataModel.paste.value = ""
        bracketOpenCount = 0
        operation = false
    }
    fun CircleNumber(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        val pos: Int = prevText.getSelectionStart()
        var buildText = StringBuilder(prevText.text.toString())
        if(pos>0){
            buildText = buildText.apply{delete(pos-1,pos)}
            prevText.setText(buildText)
            prevText.setSelection(pos-1)
        }
        else{
            Toast.makeText(this,"Position < 0", Toast.LENGTH_SHORT).show()
        }
    }
    fun testNumberLessZero():Boolean{
        val prevText:EditText = findViewById(R.id.PrevText)
        if(!prevText.text.isEmpty()){
            if(prevText.text[0] == '0')
            {
                if (!prevText.text.contains("."))
                    return true
            }
        }
        return false
    }
    fun operationClear(){
        dataModel.delete.value = ""
    }
    fun zeroTestStr(): Boolean {
        val afterText:EditText = findViewById(R.id.AfterText)
        return afterText.text.isEmpty()
    }
    fun testOperation(): Boolean {
        val afterText: EditText = findViewById(R.id.AfterText)
        return !afterText.text.isEmpty() &&(afterText.text.last() == '*' ||
                afterText.text.last() == '/' ||
                afterText.text.last() == '+' ||
                afterText.text.last() == '-' )
    }
    fun testsForAllOperation(operationChar: String){
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevText:EditText = findViewById(R.id.PrevText)
        if(testOperation() && operation){
            afterText.setText(removeLastChar(afterText.text.toString()))
            dataModel.change.value = operationChar
            operation = true
            return
        }
        if(zeroTestStr()){
            if(prevText.text.contains("0.") && prevText.text.length == 2){
                dataModel.change.value = "0"
                dataModel.change.value = operationChar
                operation = true
                return
            }
            else if(prevText.text.length > 2 && prevText.text[0] != '0'){
                dataModel.change.value = prevText.text.toString()
                dataModel.change.value = operationChar
                operation = true
                return
            }
            else if(prevText.text.isEmpty() && afterText.text.isEmpty()){
                if(testNumberLessZero()) {
                    Toast.makeText(this, "Number < 0", Toast.LENGTH_SHORT).show()
                    return
                }
                else{
                    dataModel.change.value = "0"
                    dataModel.change.value = operationChar
                    operation = true
                    return
                }

            }
            else if(testNumberLessZero()){
                Toast.makeText(this,"Number < 0", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(prevText.text.contains("0.") && prevText.text.length == 2){
            dataModel.change.value = "0"
            dataModel.change.value = operationChar
            operation = true
            return
        }
        else if(testNumberLessZero()){
            Toast.makeText(this,"Number < 0", Toast.LENGTH_SHORT).show()
            return
        }
        dataModel.change.value = prevText.text.toString()
        dataModel.change.value = operationChar
        operation = true
    }
    fun multipleNumber(view: View){
        testsForAllOperation("*")
    }
    private fun removeLastChar(str:String):String{
        return str.replaceFirst(".$".toRegex(),"")
    }
    fun delNumber(view: View){
        testsForAllOperation("/")
    }
    fun plusNumber(view: View){
        testsForAllOperation("+")
    }
    fun minusNumber(view: View){
        testsForAllOperation("-")
    }
    fun bracketFirstNumber(view: View){
        dataModel.change.value = "("
        bracketOpenCount += 1
    }
    fun bracketSecondNumber(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        if(bracketOpenCount > 0){
            if(!prevText.text.isEmpty()){
                dataModel.change.value = prevText.text.toString()
                operation = true
            }
            dataModel.change.value = ")"
            bracketOpenCount -= 1
        }
    }
    fun plusMinusNumber(view: View){

    }
}