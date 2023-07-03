package com.potatomeme.recreation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.potatomeme.recreation.databinding.ActivitySelectBinding

class SelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectBinding
    private var status = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectCategory = intent.getIntExtra(Key.SELECT_GAME, Key.GAME_KEY1)

        if (selectCategory == Key.GAME_KEY1) {
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BasicCategoryAdapter(
                    itemClickFunction = { idx ->
                        Log.d(TAG, "onCreate: itemClickFunction")
                        val intent = Intent(this@SelectActivity, TextGameActivity::class.java)
                        intent.putExtra(Key.SELECT_SINGLE, true)
                        intent.putExtra(Key.SELECT_CATEGORY, idx)
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
            binding.btnSelect.visibility = View.VISIBLE
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiCategoryAdapter(
                    itemClickFunction = { pos, checked ->
                        Log.d(TAG, "onCreate: beforeStatus : $status")
                        status = if (checked) {
                            status + (1 shl pos)
                        } else {
                            status - (1 shl pos)
                        }
                        Log.d(TAG, "onCreate: afterStatus : $status")
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화", "속담", "야채", "동물"
                        )
                    )
                }
            }
            binding.btnSelect.setOnClickListener {
                if (status == 0) {
                    Toast.makeText(this, "항목을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(this@SelectActivity, TextGameActivity::class.java)
                intent.putExtra(Key.SELECT_SINGLE, false)
                intent.putExtra(Key.SELECT_CATEGORY, status)
                startActivity(intent)
            }
        } else if (selectCategory == Key.GAME_KEY3) {
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BasicCategoryAdapter(
                    itemClickFunction = { idx ->
                        Log.d(TAG, "onCreate: itemClickFunction")
                        val intent =
                            Intent(this@SelectActivity, RandomImageGameActivity::class.java)
                        intent.putExtra(Key.SELECT_SINGLE, true)
                        intent.putExtra(Key.SELECT_CATEGORY, idx)
                        startActivity(intent)
                    },
                    lastItemClickFunction = {
                        Log.d(TAG, "onCreate: lastItemClickFunction")
                        val intent = Intent(this@SelectActivity, SelectActivity::class.java)
                        intent.putExtra(Key.SELECT_GAME, Key.GAME_KEY4)
                        startActivity(intent)
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화", "포괄"
                        )
                    )
                }
            }
        } else if (selectCategory == Key.GAME_KEY4) {
            binding.btnSelect.visibility = View.VISIBLE
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiCategoryAdapter(
                    itemClickFunction = { pos, checked ->
                        Log.d(TAG, "onCreate: beforeStatus : $status")
                        status = if (checked) {
                            status + (1 shl pos)
                        } else {
                            status - (1 shl pos)
                        }
                        Log.d(TAG, "onCreate: afterStatus : $status")
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화"
                        )
                    )
                }
            }
            binding.btnSelect.setOnClickListener {
                if (status == 0) {
                    Toast.makeText(this, "항목을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(this@SelectActivity, RandomImageGameActivity::class.java)
                intent.putExtra(Key.SELECT_SINGLE, false)
                intent.putExtra(Key.SELECT_CATEGORY, status)
                startActivity(intent)
            }
        } else if (selectCategory == Key.GAME_KEY5) {
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BasicCategoryAdapter(
                    itemClickFunction = { idx ->
                        Log.d(TAG, "onCreate: itemClickFunction")
                        val intent =
                            Intent(this@SelectActivity, RandomImageGameWithEditTextActivity::class.java)
                        intent.putExtra(Key.SELECT_SINGLE, true)
                        intent.putExtra(Key.SELECT_CATEGORY, idx)
                        startActivity(intent)
                    },
                    lastItemClickFunction = {
                        Log.d(TAG, "onCreate: lastItemClickFunction")
                        val intent = Intent(this@SelectActivity, SelectActivity::class.java)
                        intent.putExtra(Key.SELECT_GAME, Key.GAME_KEY6)
                        startActivity(intent)
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화", "포괄"
                        )
                    )
                }
            }
        } else if (selectCategory == Key.GAME_KEY6) {
            binding.btnSelect.visibility = View.VISIBLE
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiCategoryAdapter(
                    itemClickFunction = { pos, checked ->
                        Log.d(TAG, "onCreate: beforeStatus : $status")
                        status = if (checked) {
                            status + (1 shl pos)
                        } else {
                            status - (1 shl pos)
                        }
                        Log.d(TAG, "onCreate: afterStatus : $status")
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화"
                        )
                    )
                }
            }
            binding.btnSelect.setOnClickListener {
                if (status == 0) {
                    Toast.makeText(this, "항목을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(this@SelectActivity, RandomImageGameWithEditTextActivity::class.java)
                intent.putExtra(Key.SELECT_SINGLE, false)
                intent.putExtra(Key.SELECT_CATEGORY, status)
                startActivity(intent)
            }
        }  else if (selectCategory == Key.GAME_KEY7) {
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BasicCategoryAdapter(
                    itemClickFunction = { idx ->
                        Log.d(TAG, "onCreate: itemClickFunction")
                        val intent =
                            Intent(this@SelectActivity, RandomImageGameWithEditTextActivity::class.java)
                        intent.putExtra(Key.SELECT_SINGLE, true)
                        intent.putExtra(Key.SELECT_CATEGORY, idx)
                        intent.putExtra(Key.SELECT_DIFFICULT, true)
                        startActivity(intent)
                    },
                    lastItemClickFunction = {
                        Log.d(TAG, "onCreate: lastItemClickFunction")
                        val intent = Intent(this@SelectActivity, SelectActivity::class.java)
                        intent.putExtra(Key.SELECT_GAME, Key.GAME_KEY8)
                        startActivity(intent)
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화", "포괄"
                        )
                    )
                }
            }
        } else if (selectCategory == Key.GAME_KEY8) {
            binding.btnSelect.visibility = View.VISIBLE
            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MultiCategoryAdapter(
                    itemClickFunction = { pos, checked ->
                        Log.d(TAG, "onCreate: beforeStatus : $status")
                        status = if (checked) {
                            status + (1 shl pos)
                        } else {
                            status - (1 shl pos)
                        }
                        Log.d(TAG, "onCreate: afterStatus : $status")
                    }
                ).apply {
                    submitList(
                        listOf(
                            "해외영화", "한국영화", "만화영화"
                        )
                    )
                }
            }
            binding.btnSelect.setOnClickListener {
                if (status == 0) {
                    Toast.makeText(this, "항목을 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(this@SelectActivity, RandomImageGameWithEditTextActivity::class.java)
                intent.putExtra(Key.SELECT_SINGLE, false)
                intent.putExtra(Key.SELECT_CATEGORY, status)
                intent.putExtra(Key.SELECT_DIFFICULT, true)
                startActivity(intent)
            }
        }

    }

    companion object {
        private const val TAG = "SelectActivity"
    }
}