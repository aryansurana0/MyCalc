package com.example.mycalc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.mycalc.activity.CalculatorActivity
import com.example.mycalc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var digitBtns: ArrayList<Button>
    private lateinit var operatorBtns: ArrayList<Button> // 0 -> +, 1 -> -, 2 -> *, 3 -> /

    private lateinit var OPERATORS: ArrayList<Char>

    private var lastNumeric: Boolean = true
    private var lastDecimal: Boolean = false
    private var isResultInfinityOrNaNOrVeryLarge: Boolean = false
    private var isResultZero: Boolean = false
    private var resultEvaluated = false

    private val evaluateExpression: EvaluateExpression = EvaluateExpression()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeAllBtns()

        OPERATORS = arrayListOf(
            '+', '-', '*', '/'
        )
    }

    private fun initializeAllBtns() {
        digitBtns = ArrayList()
        initializeDigitBtns()
        for (button in digitBtns) {
            button.setOnClickListener { onClickDigit(it) }
        }

        operatorBtns = arrayListOf(
            binding.btnAdd,
            binding.btnSubtract,
            binding.btnMultiply,
            binding.btnDivide
        )
        for (button in operatorBtns) {
            button.setOnClickListener { onClickOperator(it) }
        }

        binding.btnClr.setOnClickListener { onClr(it) }

        binding.btnDecimal.setOnClickListener { onDecimal(it) }

        binding.btnBcksp.setOnClickListener { onBcksp(it) }

        binding.btnEquals.setOnClickListener { onClickingEqual(it) }

        binding.goCalcBtn.setOnClickListener {
            openCalculatorActivity()
        }
    }

    private fun openCalculatorActivity() {
        val intent = Intent(this, CalculatorActivity::class.java)
        startActivity(intent)
    }

    private fun initializeDigitBtns() {
//        for (i in 0..9) {
//            val btnId: String = "btn$i"
//            val resourceId: Int = resources.getIdentifier(btnId, "id", packageName)
//            digitBtns.add(findViewById<Button>(resourceId))
//        }
        digitBtns.add(binding.btn0)
        digitBtns.add(binding.btn1)
        digitBtns.add(binding.btn2)
        digitBtns.add(binding.btn3)
        digitBtns.add(binding.btn4)
        digitBtns.add(binding.btn5)
        digitBtns.add(binding.btn6)
        digitBtns.add(binding.btn7)
        digitBtns.add(binding.btn8)
        digitBtns.add(binding.btn9)
    }

    private fun onClickDigit(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()
            if (isResultInfinityOrNaNOrVeryLarge || isResultZero || resultEvaluated) {
                setOrClearInputTextViewText()
                setOrClearResultTextViewText()
                isResultInfinityOrNaNOrVeryLarge = false
                isResultZero = false
                resultEvaluated = false
            }
            binding.inputTV.append(buttonText)
            lastNumeric = true
        }
    }

    private fun onClickOperator(view: View) {
        binding.inputTV.text?.let {
            if (view is Button) {
                val inputText = it.toString()
                val operator: String = view.text.toString()
                if (isResultInfinityOrNaNOrVeryLarge || isResultZero) {
                    setOrClearInputTextViewText()
                    isResultInfinityOrNaNOrVeryLarge = false
                    isResultZero = false
                    setOrClearResultTextViewText()
                    return
                }
                if (lastNumeric) {
                    if (inputText.isEmpty() && operator != "-") return
                    if (resultEvaluated) {
                        setOrClearInputTextViewText(binding.resultTV.text.toString())
                        resultEvaluated = false
                    }
                    binding.inputTV.append(operator)
                    lastNumeric = false
                    lastDecimal = false
                }
            }
        }
    }

    private fun onDecimal(view: View) {
        if (view is Button) {
            if (lastNumeric && !lastDecimal && !resultEvaluated) {
                binding.inputTV.append(".")
                lastNumeric = false
                lastDecimal = true
            }
        }
    }

    private fun onClr(view: View) {
        if (view is Button) {
            setOrClearInputTextViewText()
            setOrClearResultTextViewText()
            lastNumeric = true
            lastDecimal = false
        }
    }

    private fun onBcksp(view: View) {
        var str = binding.inputTV.text.toString()
        if (str.length > 1 && !isResultInfinityOrNaNOrVeryLarge) {
            if (str[str.length - 2] == '.') {
                lastDecimal = true
                lastNumeric = false
            }
            if (str[str.length - 2] in OPERATORS) {
                lastNumeric = false
            }

            val lastChar = str[str.length - 1]
            if (lastChar in OPERATORS)
                lastNumeric = true

            str = str.substring(0, str.length - 1)
            setOrClearInputTextViewText(str)
        } else {
            setOrClearInputTextViewText("0")
            isResultZero = true
        }
    }

    private fun onClickingEqual(view: View) {
        if (lastNumeric) {
            val tvVal = binding.inputTV.text.toString()
            if (tvVal.isBlank()) return
            var result = evaluateExpression.expressionToResult(tvVal)

            if (result.endsWith(".0")) {
                result = result.substring(0, result.length - 2)
            }
            if (result == "Infinity" || result == "NaN" || result.contains(
                    "E",
                    ignoreCase = false
                )
            ) isResultInfinityOrNaNOrVeryLarge = true
            if (result == "0") isResultZero = true

            if (!isResultInfinityOrNaNOrVeryLarge) {
                result = (Math.round((result.toDouble()) * 1000.0) / 1000.0).toString()
                if (result.endsWith(".0")) {
                    result = result.substring(0, result.length - 2)
                }
            }

            setOrClearResultTextViewText(result)
            resultEvaluated = true

        }
    }

    private fun setOrClearInputTextViewText(content: String = "") {
        binding.inputTV.text = content
    }

    private fun setOrClearResultTextViewText(content: String = "") {
        binding.resultTV.text = content
    }

}