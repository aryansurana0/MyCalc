package com.example.mycalc.calc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalc.R
import com.example.mycalc.databinding.ItemButtonBinding

class ButtonAdapter(private val btnList: ArrayList<String>) :
    RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    class ButtonViewHolder(private val binding: ItemButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(btnText: String) {
            binding.btnItem.text = btnText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
//        val binding = ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding = DataBindingUtil.inflate<ItemButtonBinding>(LayoutInflater.from(parent.context), R.layout.item_button, parent, false)
        return ButtonViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return btnList.size
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val btnText = btnList[position]
        holder.bind(btnText)
    }
}