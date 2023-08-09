package com.example.mycalc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycalc.R
import com.example.mycalc.calc.ButtonAdapter
import com.example.mycalc.databinding.ActivityCalculatorBinding

class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    private lateinit var btnAdapter: ButtonAdapter

    private val btnList = arrayListOf<String>(
        "7", "8", "9", "/",
        "4", "5", "6", "*",
        "1", "2", "3", "-",
        "0", ".", "+", "CLR",
        "DEL", "="
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setContentView(R.layout.activity_calculator)

        btnAdapter = ButtonAdapter(btnList)
        binding.buttonsRV.apply {
            layoutManager = GridLayoutManager(this@CalculatorActivity, 4)
            adapter = btnAdapter
        }

//        btnAdapter.setOnItemClickListener { position ->
//            val btnText = btnList[position]
//            handleBtnClick(btnText)
//        }
    }

    private fun handleBtnClick(btnText: String) {

    }
}