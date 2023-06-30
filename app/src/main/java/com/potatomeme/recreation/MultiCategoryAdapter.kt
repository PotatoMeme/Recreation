package com.potatomeme.recreation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.potatomeme.recreation.databinding.ItemBasicBinding
import com.potatomeme.recreation.databinding.ItemMultiBinding

class MultiCategoryAdapter(
    private val itemClickFunction: (Int,Boolean) -> (Unit)
) :  ListAdapter<String, MultiCategoryAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMultiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(string: String,position: Int, clickFunction: (Int,Boolean) -> Unit) {
            binding.tvBasicText.text = string
            binding.root.setOnClickListener {
                Log.d(TAG, "bind: clicked $position")
                binding.checkBox.isChecked = !binding.checkBox.isChecked
                clickFunction(position,binding.checkBox.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMultiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val str = currentList[position]
        holder.bind(str,position, itemClickFunction)
    }

    companion object {
        private const val TAG = "MultiCategoryAdapter"
        val diffUtil: DiffUtil.ItemCallback<String> = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}