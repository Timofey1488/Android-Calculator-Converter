package com.example.converter

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.converter.UIConverter.DataModel
import com.example.converter.UIConverter.NumPadCalculator
import com.example.converter.UIConverter.NumPadCalculatorPro
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_UP
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.exp

class CalculatorActivity : AppCompatActivity() {
    private val dataModel: DataModel by viewModels()
    private var operation: Boolean = false
    private var bracketOpenCount: Int = 0
    private var exceptionCheck:Boolean = false
    private var TempBackStr:String = ""
    private val ft: FragmentTransaction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        ft?.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_in);
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
        val stack = ArrayDeque<Char>()
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

    private fun format(number: String, scale: Int): String {
        val value = BigDecimal(number)
        val symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)
        val positive = BigDecimal(1) // scale is zero
        positive.setScale(0) // unnecessary
        val negative = BigDecimal(-1) // scale is zero
        negative.setScale(0) // unnecessary
        if (value.compareTo(positive) == 1 || value.compareTo(negative) == -1) {
            symbols.setExponentSeparator("e+")
        } else {
            symbols.setExponentSeparator("e")
        }
        val formatter = DecimalFormat("0.0E0", symbols)
        formatter.roundingMode = RoundingMode.HALF_UP
        formatter.minimumFractionDigits = scale
        return formatter.format(value)
    }

    fun RPNToFinalString(str:String):BigDecimal{
        var operand:String = ""
        val stack = ArrayDeque<BigDecimal>()
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
                stack.addLast(operand.toBigDecimal())
                operand = ""

            }
            if(getPriority(str[i]) > 1){
                val a:BigDecimal = stack.removeLast()
                val b:BigDecimal = stack.removeLast()
                if(str[i] == '+')
                    stack.addLast(b.add(a))
                if(str[i] == '-')
                    stack.addLast(b.subtract(a))
                if(str[i] == '*')
                    stack.addLast(b.multiply(a))
                if(str[i] == '/')
                    stack.addLast(b.divide(a, 16, RoundingMode.HALF_UP))
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
    fun preparingFormatForRPN(str:String):String{
        var preparingString:String = ""
        try {
            var posCloseBracket:Int =0
            var temp:Boolean = false
            for (i in 0 until str.length){
                var ch:Char = str[i]
                if(ch == '-'){
                    if(i == 0)
                        preparingString +="0"
                    else if(str[i-1] == '(')
                        preparingString +="0"
                    else if(str[i-1] == '*' ||str[i-1] == '/'||str[i-1] == '+'||str[i-1] == '-')
                    {
                        temp = true
                        preparingString +="(0"
                        for (j in i+1 until str.length){
                            if(str[j] == '+'||str[j] == '-'||str[j] == '/'||str[j] == '*')
                                posCloseBracket = j
                            else if(j==str.length-1)
                                posCloseBracket = j
                        }
                    }
                }
                preparingString += ch
                if(i == posCloseBracket && temp){
                    preparingString +=")"
                    temp = false
                }
            }
            return preparingString
        }
        catch (e:Throwable){
            Toast.makeText(this, "Wrong expression", Toast.LENGTH_SHORT).show()
        }
        return preparingString

    }
    //Calculator Code
    fun equalButton(view: View){
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevText:EditText = findViewById(R.id.PrevText)
        val pos: Int =0
        var isDigit:Boolean = false
        for(i in 0 until afterText.text.length){
            if(afterText.text[i].isDigit()) isDigit = true
        }
        if(afterText.text.length == 0){
            Toast.makeText(this, "Select action", Toast.LENGTH_SHORT).show()
            return
        }
        if(!isDigit ||bracketOpenCount !=0){
            Toast.makeText(this, "Wrong expression", Toast.LENGTH_SHORT).show()
            return
        }
        if(!prevText.text.contains("e") && (prevText.text.contains("+")  || prevText.text.contains("/") || prevText.text.contains("*"))){
            Toast.makeText(this, "Wrong expression", Toast.LENGTH_SHORT).show()
            return
        }
        if(!prevText.text.contains("e") && prevText.text.contains("-") ){
            if(prevText.text[0] != '-'){
                Toast.makeText(this, "Wrong expression", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(afterText.text.contains("/") && prevText.text.toString() == "0"){
            Toast.makeText(this, "You could't devide by zero", Toast.LENGTH_SHORT).show()
            return
        }
        if(testNumberLessZero()){
            Toast.makeText(this,"Number < 0", Toast.LENGTH_SHORT).show()
            return
        }
        if(afterText.text.contains("=")){
            dataModel.paste.value = cursorPointer(prevText.text.toString())
            dataModel.change.value = "="
            return
        }
        if(!prevText.text.isEmpty())
            if(prevText.text[prevText.text.length-1] != ')')
                dataModel.change.value = prevText.text.toString()
        val prepareStr:String = preparingFormatForRPN(afterText.text.toString())
        val result:BigDecimal =  RPNToFinalString(StringToRPN(prepareStr))
        val formatStr: String = deleteZeroesInString(result.toString())
        if(result.toString().contains(".")){
            var posPoint:Int = 0
            var str:String = result.toString()
            for (i in 0 until str.length){
                if(str[i] == '.'){
                    posPoint = i
                    break
                }
            }
            val result_:BigDecimal = deleteZeroesInString(deleteFromZeroAndPoint(result.toString())).toBigDecimal()
            if(str.length-posPoint>16 && str.length<22){
                dataModel.delete.value = deleteZeroesInString(result_.setScale(6, ROUND_UP).toString())
            }
            else if(str.contains("E"))
                dataModel.delete.value = formatStr
            else if(posPoint < 14) {
                dataModel.delete.value = deleteZeroesInString(result_.setScale(2, ROUND_UP).toString())
            }
            else
                dataModel.delete.value =  deleteZeroesInString(result_.setScale(1, ROUND_UP).toString())

        }
        else if(result.toString().length > 16)
            dataModel.delete.value = deleteZeroesInString(result.toString())
        else
            dataModel.delete.value = formatStr
        prevText.setSelection(pos)
        dataModel.change.value = "="
        prevText.setSelection(prevText.text.length)

    }
    fun deleteFromZeroAndPoint(str:String):String{
        if(str.length >= 2 && str[str.length-2] == '.' && str.last() == '0'){
            return str.replaceRange(str.length-2,str.length,"")
        }
        else if(str[str.length-1] == '.'){
            return str.replaceRange(str.length-1,str.length,"")
        }
        else
            return str
    }

    fun deleteZeroesInString(str:String):String{
        var final: Boolean = false
        var posStart:Int = 0

        for (i in 0 until str.length-1){
            if(str[i] == '0'){
                posStart = i
                for (j in i+1 until str.length){
                    if(str[j] == '0' && j!=str.length-1) continue
                    if(j==str.length-1 && str[j] =='0') final = true
                    else{
                        break
                    }
                }
            }
            if(final) break
        }
        if(final)
            return deleteFromZeroAndPoint(str.replaceRange(posStart,str.length,""))
        else return deleteFromZeroAndPoint(str)
    }
    fun percentPoint(number:BigDecimal):Int{
        val prevText:EditText = findViewById(R.id.PrevText)
        val str:String = deleteZeroesInString(prevText.text.toString())
        var pos:Int = 0
        var count:Int = 0
        if(prevText.text.contains(".")){
            for(i in 0 until str.length){
                if (str[i] == '.'){
                    pos = i
                    break
                }
            }
            for (i in pos until str.length){
                count++
            }
        }
        return count
    }

    fun percentNumber(view:View){
        val prevText:EditText = findViewById(R.id.PrevText)
        var checkMinus = false
        if(prevText.text.toString() == "0" || prevText.text.isEmpty()){
            dataModel.delete.value = "0"
            return
        }
        if( prevText.text.contains("e") || prevText.text.contains("E")){
            Toast.makeText(this, "You couldn't use this expression", Toast.LENGTH_SHORT).show()
            return
        }

        if(prevText.text.contains("-")){
            dataModel.minusDelete.value = ""
            checkMinus = true
        }

        val str:String = prevText.text.toString()
        val numberA:BigDecimal = str.toBigDecimal()
        val numberB:Double = 100.0
        var numbersAfterPoint:Int = percentPoint(numberA)
        if(prevText.text.length>2 && !prevText.text.contains("."))
            dataModel.delete.value = numberA.divide(numberB.toBigDecimal(), 2, RoundingMode.HALF_UP).toString()
        else if(prevText.text.length>2)
            dataModel.delete.value = numberA.divide(numberB.toBigDecimal(), numbersAfterPoint+1, RoundingMode.HALF_UP).toString()
        else
            dataModel.delete.value = numberA.divide(numberB.toBigDecimal(), prevText.text.length, RoundingMode.HALF_UP).toString()
        if(checkMinus)
            dataModel.messageTemp.value = "-"
    }

    fun proButtonCalculator(view: View){
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.push_down_in,R.anim.push_down_in)
            .replace(R.id.num_pad_fragment_calculator, NumPadCalculatorPro.newInstance())
            .commit()
    }

    fun notProButtonCalculator(view: View){
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.push_up_in,R.anim.push_up_in)
            .replace(R.id.num_pad_fragment_calculator, NumPadCalculator.newInstance())
            .commit()
    }

    fun SavePosition(text: String){
        if(operation)
            operationClear()
        operation = false
        val prevText:EditText = findViewById(R.id.PrevText)
        val pos: Int = prevText.getSelectionStart()
        var buildText = StringBuilder(prevText.text.toString())
        val lenght = prevText.length()
        if(lenght<16 && !prevText.text.isEmpty()){
            if(prevText.text.contains("|"))
                buildText = buildText.apply{insert(pos-1, text)}
            else
                buildText = buildText.apply{insert(pos, text)}
            prevText.setText(buildText)
            prevText.setSelection(pos+1)
        }
        else if(prevText.text.isEmpty()){
            buildText = buildText.apply{insert(pos, text)}
            prevText.setText(buildText)
            prevText.setSelection(prevText.selectionStart+1)
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
        if(prevText.text.length == 1 && prevText.text.toString() == "0"){
            Toast.makeText(this,"Lenght = 0", Toast.LENGTH_SHORT).show()
            return
        }
        val posCursor:Int = prevText.selectionStart
        if(testZeroPrevPoint(cursorPointer(prevText.text.toString()),posCursor-1))
            return
        else
            SavePosition("0")
    }
    fun testZeroPrevPoint(str:String,pos:Int):Boolean{
        if(str.contains('.')){
            var posPoint:Int = 0
            for(i in 0 until str.length){
                if(str[i] == '.'){
                    posPoint = i
                    break
                }
            }
            if(posPoint !=0 && pos < posPoint){
                if(str[posPoint-1] == '0'){
                    Toast.makeText(this,"Unacceptable number", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return false
    }
    fun DoubleZero(view: View){
        val prevText: EditText = findViewById(R.id.PrevText)
        if(prevText.text.length == 1 && prevText.text.toString() == "0"){
            Toast.makeText(this,"Lenght = 0", Toast.LENGTH_SHORT).show()
            return
        }
        val posCursor:Int = prevText.selectionStart
        if(prevText.text.length == 1 && prevText.text.toString() == "0"){
            Toast.makeText(this,"Lenght = 0", Toast.LENGTH_SHORT).show()
            return
        }
        if(testZeroPrevPoint(prevText.text.toString(),posCursor))
            return
        else{
            if(prevText.text.length+2 <= 16){
                SavePosition("00")
                prevText.setSelection(prevText.getSelectionStart()+1)
            }
        }


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
        val pos:Int = prevText.selectionStart
        var buildText = StringBuilder(prevText.text.toString())
        if(pos>1){
            buildText = buildText.apply{delete(pos-2,pos-1)}
            prevText.setText(buildText)
            prevText.setSelection(pos-1)
        }
        else{
            Toast.makeText(this,"Position < 0", Toast.LENGTH_SHORT).show()
        }
    }


    fun testNumberLessZero():Boolean{
        val prevText:EditText = findViewById(R.id.PrevText)
        var prevTextCursor:String = cursorPointer(prevText.text.toString())
        if(!prevTextCursor.isEmpty()){
            if(prevTextCursor[0] == '0' && prevTextCursor.length == 1)
            {
                return false
            }
            if(prevTextCursor[0] == '0')
            {
                if (!prevTextCursor.contains("."))
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
                afterText.text.last() == '-' ||
                afterText.text.last() == '=')
    }
    fun testsForAllOperation(operationChar: String){
        val afterText:EditText = findViewById(R.id.AfterText)
        val prevText:EditText = findViewById(R.id.PrevText)
        var pos:Int = prevText.getSelectionStart()
        val tempStr:String = prevText.text.toString()
        prevText.setText(cursorPointer(prevText.text.toString()))
        if(prevText.text.contains("e")|| prevText.text.contains("E")){
            Toast.makeText(this, "You don't use this expression for action", Toast.LENGTH_SHORT).show()
            return
        }
        if(afterText.text.contains("=")){
            dataModel.paste.value = cursorPointer(prevText.text.toString())
            dataModel.change.value = operationChar
            operation = true

            return
        }
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
        dataModel.change.value = cursorPointer(prevText.text.toString())
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
        val prevText:EditText = findViewById(R.id.PrevText)
        val ZeroCheck:Boolean = prevText.text.length == 1 && prevText.text.contains("0")
        if(!prevText.text.isEmpty() && !ZeroCheck){
            if(!prevText.text.contains("-")){
                dataModel.messageTemp.value = "-"
            }
            else{
                dataModel.minusDelete.value = ""
            }
        }
    }
    fun cursorPointer(str:String):String{
        var finalStr:String = ""
        for (i in 0 until str.length)
            if(str[i] != '|')
                finalStr = finalStr.plus(str[i])
        return finalStr
    }

    fun arrowLeft(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        var pos:Int = prevText.getSelectionStart()

        if(pos != 0 && prevText.text.isNotEmpty()){
            if(prevText.text.contains("|")){
                prevText.setText(cursorPointer(prevText.text.toString()))
                pos--
                if(pos-1 < 0)
                    prevText.text.insert(pos,"|")
                else{
                    prevText.setSelection(pos-1)
                    prevText.text.insert(pos-1,"|")
                }
            }
            else if(prevText.selectionStart == 0){
                prevText.text.insert(pos,"|")
            }
            else{
                prevText.setSelection(pos-1)
                prevText.text.insert(pos-1,"|")
            }
        }
        else
            prevText.setSelection(0)

    }
    fun arrowRight(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        var pos:Int = prevText.getSelectionStart()
        if(pos != prevText.text.length && prevText.text.isNotEmpty()){
            if(prevText.text.contains("|")){
                prevText.setText(cursorPointer(prevText.text.toString()))
                pos--
                prevText.setSelection(pos+1)
                prevText.text.insert(pos+1,"|")
            }
            else if(prevText.selectionStart == prevText.text.length-1){
                prevText.text.insert(pos,"|")
            }
            else{
                prevText.setSelection(pos+1)
                prevText.text.insert(pos+1,"|")
            }
        }
        else
            prevText.setSelection(prevText.text.length)

    }

    //Pro Functions

    fun oneToXButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        var tempStrPrev:String = prevText.text.toString()
        try {
            val a:BigDecimal = prevText.text.toString().toBigDecimal()
            val b = 1
            dataModel.delete.value = b.toBigDecimal().divide(a).toString()
        }
            catch (e:Throwable){
                dataModel.delete.value = "Infinity"
                Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        finally{
            TempBackStr = tempStrPrev
        }

    }

    fun eButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        if(!prevText.text.isEmpty()){
            val a:Double = prevText.text.toString().toDouble()
            dataModel.delete.value = exp(a).toString()
            prevText.setSelection(prevText.text.length-1)
        }
        else{
            dataModel.delete.value = exp(1.0).toString()
            prevText.setSelection(prevText.text.length-1)
        }
    }

    fun powT(str:String, scale: Int){
        val prevText:EditText = findViewById(R.id.PrevText)
        var tempStrPrev:String = prevText.text.toString()
        try {
            val a:BigDecimal = str.toBigDecimal()
            if(str.length >16)
                dataModel.delete.value = format(a.pow(scale).toString(),2)
            else
                dataModel.delete.value = a.pow(scale).toString()
        }
        catch (e:Throwable ){
            dataModel.delete.value = "Infinity"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        finally{
            TempBackStr = tempStrPrev
        }
    }



    fun powThreeButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        powT(prevText.text.toString(),3)
    }
    fun powButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        powT(prevText.text.toString(),2)
    }

 //Factorial Function(Start)
    fun factorialButton(view: View){
         val prevText:EditText = findViewById(R.id.PrevText)
         var tempStrPrev:String = prevText.text.toString()
        try {
            var temp:String = factorial(prevText.text.toString().toInt())
            if(temp.length >16)
                dataModel.delete.value = format(factorial(prevText.text.toString().toInt()),2)
            else
                dataModel.delete.value = factorial(prevText.text.toString().toInt())
        }
        catch(e:Throwable){
            dataModel.delete.value = "Infinity"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        finally{
            TempBackStr = tempStrPrev
        }
    }

    fun factorial(n: Int):String {
        val res = IntArray(5000)
        var finalStr:String = ""
        // Initialize result
        res[0] = 1
        var res_size = 1

        // Apply simple factorial formula
        // n! = 1 * 2 * 3 * 4...*n
        for (x in 2..n) res_size = multiply(x, res, res_size)
        println("Factorial of given number is ")
        for (i in res_size - 1 downTo 0)
            finalStr = finalStr.plus(res[i])
        return finalStr
    }

    fun multiply(x: Int, res: IntArray, res_size: Int): Int {
        var res_size_ = res_size
        var carry = 0 // Initialize carry

        for (i in 0 until res_size_) {
            val prod = res[i] * x + carry
            res[i] = prod % 10

            carry = prod / 10 // Put rest in carry
        }
        while (carry != 0) {
            res[res_size_] = carry % 10
            carry = carry / 10
            res_size_++
        }
        return res_size_
    }

 //Factorial Function(End)
    fun BackButton(view:View){
        dataModel.copyLastPrev.value = TempBackStr
    }
    fun sqrtButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        var tempStrPrev:String = prevText.text.toString()
        try {
            if(prevText.text.toString() == "0" || prevText.text.isEmpty()){
                dataModel.delete.value = "0"
            }
            else{
                val a:BigDecimal = prevText.text.toString().toBigDecimal()
                val temp:String = Math.sqrt(a.toDouble()).toString()
                dataModel.delete.value = temp
            }
        }
        catch (e:Throwable){
            dataModel.delete.value = "Infinity"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        finally{
            TempBackStr = tempStrPrev
        }
    }

    fun piButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        dataModel.delete.value = kotlin.math.PI.toString()
    }

    fun cosButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        try {
            if(prevText.text.toString() == "0" || prevText.text.isEmpty()){
                dataModel.delete.value = "1"
            }
            else dataModel.delete.value = kotlin.math.cos(prevText.text.toString().toDouble()).toString()
        }
        catch (e:Throwable){
            dataModel.delete.value = "Exception"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    fun lnButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        try {
            dataModel.delete.value = kotlin.math.ln(prevText.text.toString().toDouble()).toString()
        }
        catch (e:Throwable){
            dataModel.delete.value = "Invalid expression"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    fun lgButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        try {
            dataModel.delete.value = deleteFromZeroAndPoint(kotlin.math.log(prevText.text.toString().toDouble(), 10.0).toString())
        }
        catch (e:Throwable){
            dataModel.delete.value = "Invalid expression"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    fun sinButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        try {
            if(prevText.text.toString() == "0" || prevText.text.isEmpty()){
                dataModel.delete.value = "0"
            }
            else dataModel.delete.value = kotlin.math.sin(prevText.text.toString().toDouble()).toString()
        }
        catch (e:Throwable){
            dataModel.delete.value = "Invalid expression"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    fun tgButton(view: View){
        val prevText:EditText = findViewById(R.id.PrevText)
        try {
                dataModel.delete.value = kotlin.math.tan(prevText.text.toString().toDouble()).toString()
        }
        catch (e:Throwable){
            dataModel.delete.value = "Invalid expression"
            Toast.makeText(this, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}