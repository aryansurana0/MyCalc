package com.example.mycalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlin.ArithmeticException

class MainActivity : AppCompatActivity() {
    private lateinit var inputTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var digitBtns: ArrayList<Button>
    private lateinit var operatorBtns: ArrayList<Button> // 0 -> +, 1 -> -, 2 -> *, 3 -> /
    private lateinit var clrBtn: Button
    private lateinit var decimalBtn: Button
    private lateinit var backspBtn: ImageButton
    private lateinit var equalsBtn: Button

    private lateinit final var OPERATORS: ArrayList<Char>

    private var lastNumeric: Boolean = true
    private var lastDecimal: Boolean = false

    private var isResultInfinityOrNaNOrVeryLarge: Boolean = false
    private var isResultZero: Boolean = false

    private var resultEvaluated = false

    val evaluateExpression: EvaluateExpression = EvaluateExpression()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById<TextView>(R.id.resultTV)
        inputTextView = findViewById<TextView>(R.id.inputTV)

        initializeAllBtns()

        OPERATORS = ArrayList()
        OPERATORS.add('+')
        OPERATORS.add('-')
        OPERATORS.add('*')
        OPERATORS.add('/')
    }

    private fun initializeAllBtns() {
        digitBtns = ArrayList()
        initializeDigitBtns()
        for (button in digitBtns) {
            button.setOnClickListener { onClickDigit(it) }
        }

        operatorBtns = arrayListOf(
            findViewById<Button>(R.id.btnAddition),
            findViewById<Button>(R.id.btnSubtract),
            findViewById<Button>(R.id.btnMultiply),
            findViewById<Button>(R.id.btnDivide),
        )
        for (button in operatorBtns) {
            button.setOnClickListener { onClickOperator(it) }
        }

        clrBtn = findViewById<Button>(R.id.btnClr)
        clrBtn.setOnClickListener { onClr(it) }

        decimalBtn = findViewById<Button>(R.id.btnDecimal)
        decimalBtn.setOnClickListener { onDecimal(it) }

        backspBtn = findViewById<ImageButton>(R.id.btnBcksp)
        backspBtn.setOnClickListener { onBacksp(it) }

        equalsBtn = findViewById<Button>(R.id.btnEquals)
        equalsBtn.setOnClickListener { onClickingEqual(it) }
    }

    private fun initializeDigitBtns() {
        for (i in 0..9) {
            val btnId: String = "btn$i"
            val resourceId: Int = resources.getIdentifier(btnId, "id", packageName)
            digitBtns.add(findViewById<Button>(resourceId))
        }
    }

    private fun onClickDigit(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()
//            Toast.makeText(this, "Button $buttonText clicked", Toast.LENGTH_SHORT).show()
            if (isResultInfinityOrNaNOrVeryLarge || isResultZero || resultEvaluated) {
                inputTextView.text = ""
                resultTextView.text = ""
                isResultInfinityOrNaNOrVeryLarge = false
                isResultZero = false
                resultEvaluated = false
            }
            inputTextView.append(buttonText)
            lastNumeric = true
//            lastDecimal = false
        }
    }

    private fun onClickOperator(view: View) {
        inputTextView.text?.let {
            if (view is Button) {
                val inputText = it.toString()
                val operator: String = view.text.toString()
                if (isResultInfinityOrNaNOrVeryLarge || isResultZero) {
                    inputTextView.text = ""
                    isResultInfinityOrNaNOrVeryLarge = false
                    isResultZero = false
                    resultTextView.text = ""
                    return
                }
//                if (lastNumeric && !isOperatorAdded(it.toString(), op)) {
                if (lastNumeric) {
                    if (inputText.isEmpty() && operator != "-") return
                    if (resultEvaluated) {
                        inputTextView.text = resultTextView.text
                        resultEvaluated = false
                    }
                    inputTextView.append(operator)
//                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    lastNumeric = false
                    lastDecimal = false
                }
            }
        }
    }

    private fun isOperatorAdded(value: String, op: String): Boolean {
        return if (value.isEmpty()) {
            op != "-"
        } else if (value[0] == '-') {
            checkContainsAllOperators(value.substring(1))
        } else {
            checkContainsAllOperators(value)
        }
    }

    private fun checkContainsAllOperators(str: String): Boolean {
        return str.contains("+")
                || str.contains("-")
                || str.contains("*")
                || str.contains("/")
    }

    private fun onDecimal(view: View) {
        if (view is Button) {
            if (lastNumeric && !lastDecimal && !resultEvaluated) {
                inputTextView.append(".")
                lastNumeric = false
                lastDecimal = true
            }
        }
    }

    private fun onClr(view: View) {
        if (view is Button) {
            inputTextView.text = ""
            resultTextView.text = ""
            lastNumeric = true
            lastDecimal = false
        }
    }

    private fun onBacksp(view: View) {
        var str = inputTextView.text.toString()
        if (str.length > 1 && !isResultInfinityOrNaNOrVeryLarge) {
            var spliceInd = str.length - 1

            if (str[str.length - 2] == '.')
                spliceInd--

            val lastChar = str[str.length - 1]
            if (lastChar in OPERATORS)
                lastNumeric = true

            str = str.substring(0, spliceInd)
            inputTextView.text = str
        } else {
            inputTextView.text = "0"
            isResultZero = true
        }
    }

    private fun onClickingEqual(view: View) {
        if (lastNumeric) {
            val tvVal = inputTextView.text.toString()
            if (tvVal.isBlank()) return
            var result = evaluateExpression.expressionToResult(tvVal)

            if (result.endsWith(".0")) {
                result = result.substring(0, result.length - 2)
            }
//            inputTextView.text = result
            if (result == "Infinity" || result == "NaN" || result.contains("E", ignoreCase = false)) isResultInfinityOrNaNOrVeryLarge = true
            if (result == "0") isResultZero = true

            if (!isResultInfinityOrNaNOrVeryLarge) resultTextView.text = (Math.round((result.toDouble()) * 1000.0) / 1000.0).toString()
            else resultTextView.text = result
            resultEvaluated = true

        }
    }

    private fun evaluateExpr(expr: String): String {

        var isNeg = false
        var expression = expr
        var result: String = ""
        try {
            if (expression.startsWith("-")) {
                isNeg = true
                expression = expression.substring(1)
            }
            val operator = findOperator(expression)
            val splitExpr = expression.split(operator)
            val num1 = if (isNeg) {
                "-${splitExpr[0]}".toDouble()
            } else {
                splitExpr[0].toDouble()
            }
            val num2 = splitExpr[1].toDouble()

            when (operator) {
                "+" -> result = (num1 + num2).toString()
                "-" -> result = (num1 - num2).toString()
                "*" -> result = (num1 * num2).toString()
                "/" -> {
                    result = if (num2 != 0.0) (num1 / num2).toString()
                    else "Infinity"
                }

                else -> {}
            }
        } catch (e: ArithmeticException) {
            e.printStackTrace()
        }
        return result
    }

    private fun findOperator(expr: String): String {
        return if (expr.contains("+")) "+"
        else if (expr.contains("-")) "-"
        else if (expr.contains("*")) "*"
        else "/"
    }

}