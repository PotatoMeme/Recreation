package com.potatomeme.recreation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.potatomeme.recreation.databinding.ActivityMainBinding
import com.potatomeme.recreation.databinding.ActivityTextGameBinding

class TextGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextGameBinding
    private var idx: Int = 0
    private var count: Int = 0
    private val categoryArr = arrayOf(
        R.array.MOVIE_WESTERN,
        R.array.MOVIE_KOREA,
        R.array.MOVIE_COMIC,
        R.array.PROVERB,
        R.array.VEGETABLE,
        R.array.ANIMAL,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextGameBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        val single = intent.getBooleanExtra(Key.SELECT_SINGLE,true)

        val strArray = if (single){
            val categoryIdx = intent.getIntExtra(Key.SELECT_CATEGORY,0)
            resources.getStringArray(categoryArr[categoryIdx]).toList().shuffled()
        } else {
            val list = mutableListOf<String>()
            val status  = intent.getIntExtra(Key.SELECT_CATEGORY,0)
            for (i in 0 until 6){
                if (status and (1 shl i) == 1 shl i){
                    list += resources.getStringArray(categoryArr[i])
                }
            }
            list.shuffled()
        }


        idx = 0
        binding.tvText.text = strArray[idx]

        binding.btnSkip.setOnClickListener {
            if (idx == strArray.size){
                Toast.makeText(this,"끝까지 푸셨습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
            idx++
            binding.tvText.text = strArray[idx]
        }

        binding.btnCount.setOnClickListener {
            idx++
            count++
            binding.tvCount.text = count.toString()
            binding.tvText.text = strArray[idx]
        }
    }
}