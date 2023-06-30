package com.potatomeme.recreation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.potatomeme.recreation.databinding.ItemBasicBinding

class BasicCategoryAdapter(
    private val itemClickFunction: (Int) -> (Unit),
    private val lastItemClickFunction: () -> (Unit),
) :  ListAdapter<String, BasicCategoryAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemBasicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(string: String,position: Int, clickFunction: (Int) -> Unit) {
            binding.tvBasicText.text = string
            binding.root.setOnClickListener {
                Log.d(TAG, "bind: clicked $position")
                clickFunction(position)
            }
        }
        fun bindLast(string: String,clickFunction: () -> Unit){
            binding.tvBasicText.text = string
            binding.root.setOnClickListener {
                Log.d(TAG, "bind: clicked Last")
                clickFunction()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val str = currentList[position]
        if (position == currentList.lastIndex){
            holder.bindLast(str, lastItemClickFunction)
        }else{
            holder.bind(str,position, itemClickFunction)
        }
    }

    companion object {
        private const val TAG = "BasicCategoryAdapter"
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