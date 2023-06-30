package com.potatomeme.recreation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.potatomeme.recreation.databinding.ActivitySelectBinding

class SelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectCategory = intent.getIntExtra(Key.SELECT_GAME, Key.GAME_KEY1)

        if (selectCategory == Key.GAME_KEY1) {
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                val category = arrayOf(
                    Key.CATEGORY_KEY1,
                    Key.CATEGORY_KEY2,
                    Key.CATEGORY_KEY3,
                    Key.CATEGORY_KEY4,
                    Key.CATEGORY_KEY5,
                    Key.CATEGORY_KEY6,
                )
                adapter = BasicCategoryAdapter(
                    itemClickFunction = { idx ->
                        Log.d(TAG, "onCreate: itemClickFunction")
                        val intent = Intent(this@SelectActivity, TextGameActivity::class.java)
                        intent.putExtra(Key.SELECT_SINGLE,true)
                        intent.putExtra(Key.SELECT_CATEGORY, category[idx])
                        startActivity(intent)
                    },
                    lastItemClickFunction = {
                        Log.d(TAG, "onCreate: lastItemClickFunction")
                        val intent = Intent(this@SelectActivity, SelectActivity::class.java)
                        intent.putExtra(Key.SELECT_GAME, Key.GAME_KEY2)
                        startActivity(intent)
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화", "속담", "야채", "동물", "포괄"
                        )
                    )
                }
            }
        } else if (selectCategory == Key.GAME_KEY2) {

        }

    }

    companion object {
        private const val TAG = "SelectActivity"
    }
}