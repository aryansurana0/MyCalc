package com.example.mycalc

import android.util.Log
import java.lang.Exception
import java.util.Stack

class EvaluateExpression {

    // takes the whole expression as a string and returns the parsed numbers and operators as separate lists in corresponding order
    fun parseArithmeticExpression(expression: String): Pair<ArrayList<String>, ArrayList<String>> {
        val numbers = ArrayList<String>()
        val operators = ArrayList<String>()
        var currNum = ""

        // helper
        fun addNumToList() {
            if (currNum.isNotBlank()) {
                numbers.add(currNum)
                currNum = ""
            }
        }

        for (ch in expression) {
            if (ch.isDigit() || ch == '.' || (ch == '-' && currNum.isEmpty()))
                currNum += ch
            else {
                addNumToList()
                operators.add(ch.toString())
            }
        }
        addNumToList()

        return Pair(numbers, operators)
    }

    fun infixFromNumsAndOperatorsList(
        numbers: ArrayList<String>,
        operators: ArrayList<String>
    ): ArrayList<String> {
        val infixExpression = ArrayList<String>()
        for (i in 0..numbers.size - 1) {
            infixExpression.add(numbers[i])
            if (i < operators.size)
                infixExpression.add(operators[i])
        }
        return infixExpression
    }

    private fun isNumber(token: String) = token.matches("-?\\d+(\\.\\d+)?".toRegex())

    private fun isOperator(token: String) = (token.length == 1 && token[0] in "+-*/")

    private fun precedence(token: String) = when (token) {
        "*", "/" -> 2
        "+", "-" -> 1
        else -> 0
    }

    fun infixToPostfix(infixExpression: ArrayList<String>): ArrayList<String> {
        val postfix = ArrayList<String>()
        val operatorStack = Stack<String>()

        for (token in infixExpression) {
            if (isNumber(token)) {
                postfix.add(token)
            } else if (isOperator(token)) {
                while (operatorStack.isNotEmpty() && precedence(operatorStack.peek()) >= precedence(
                        token
                    )
                ) {
                    postfix.add(operatorStack.pop())
                }
                operatorStack.push(token)
            }
        }

        while (operatorStack.isNotEmpty()) {
            postfix.add(operatorStack.pop())
        }

        return postfix
    }

    fun postfixToResult(postfix: ArrayList<String>): String {
        var result: String = ""
        val stack = Stack<String>()
        for (token in postfix) {
            if (isNumber(token)) stack.push(token)
            else {
                if (stack.size >= 2) {
                    try {
                        val num2 = stack.pop().toDouble()
                        val num1 = stack.pop().toDouble()
                        when (token) {
                            "+" -> result = (num1 + num2).toString()
                            "-" -> result = (num1 - num2).toString()
                            "*" -> result = (num1 * num2).toString()
//                            "/" -> {
//                                result = if (num2 != 0.0) (num1 / num2).toString()
//                                else "Infinity"
//                            }
                            "/" -> result = (num1 / num2).toString()
                        }
                    } catch (e: ArithmeticException) {
                        e.printStackTrace()
                        return "Infinity"
                    }
                    stack.push(result)
                }
            }
        }

        return stack.pop()
    }

    fun expressionToResult(expression: String): String {
        val (numbers, operators) = parseArithmeticExpression(expression)
        val infix = infixFromNumsAndOperatorsList(numbers, operators)
        val postfix = infixToPostfix(infix)
        val result = postfixToResult(postfix)
        return result
    }


}