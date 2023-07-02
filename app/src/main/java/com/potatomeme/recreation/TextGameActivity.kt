package com.potatomeme.recreation

import android.content.Context
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.potatomeme.recreation.databinding.ActivityTextGameBinding
import kotlin.concurrent.thread

class TextGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextGameBinding
    private var idx: Int = 0

    private val categoryArr = arrayOf(
        R.array.MOVIE_WESTERN,
        R.array.MOVIE_KOREA,
        R.array.MOVIE_COMIC,
        R.array.PROVERB,
        R.array.VEGETABLE,
        R.array.ANIMAL,
    )

    private var correctCount: Int = 0
    private var basePass : Int = 0
    private var remainPass: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextGameBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val settingDialog = TextGameSettingDialog(size) { timeChecked, time, passChecked, pass ->
            Log.d(TAG, "onCreate: timeChecked : $timeChecked")
            Log.d(TAG, "onCreate: time : $time")
            Log.d(TAG, "onCreate: passChecked : $passChecked")
            Log.d(TAG, "onCreate: pass : $pass")
            basePass = if (passChecked) pass else 10
            remainPass = basePass
            binding.tvRemainPassCount.text = "$remainPass"
            if (timeChecked) {
                var inThreadTimeCount = time
                binding.tvTime.text = "${inThreadTimeCount / 60} : ${inThreadTimeCount % 60}"

                val thread = thread {
                    while (true) {
                        Thread.sleep(1000)
                        Log.d(TAG, "onCreate: ${inThreadTimeCount--}")
                        runOnUiThread {
                            binding.tvTime.text = "${inThreadTimeCount / 60} : ${
                                (inThreadTimeCount % 60).let {
                                    if (it < 10) "0$it" else it
                                }
                            }"
                        }
                        synchronized(this) {
                            if (inThreadTimeCount == 0) {
                                runOnUiThread {
                                    Toast.makeText(this, "시간이 종료되었습니다.", Toast.LENGTH_SHORT).show()
                                    val resultDialog = GameResultDialog(size,correctCount,basePass - remainPass){
                                        finish()
                                    }.apply {
                                        isCancelable = false
                                    }
                                    resultDialog.show(this.supportFragmentManager, "GameResultDialog")
                                }
                                return@thread
                            }
                        }
                    }
                }
                if (!thread.isAlive) {
                    thread.start()
                }
            }

        }.apply {
            isCancelable = false
        }
        settingDialog.show(this.supportFragmentManager, "TextGameSettingDialog")

        val single = intent.getBooleanExtra(Key.SELECT_SINGLE, true)

        val strArray = if (single) {
            val categoryIdx = intent.getIntExtra(Key.SELECT_CATEGORY, 0)
            resources.getStringArray(categoryArr[categoryIdx]).toList().shuffled()
        } else {
            val list = mutableListOf<String>()
            val status = intent.getIntExtra(Key.SELECT_CATEGORY, 0)
            for (i in 0 until 6) {
                if (status and (1 shl i) == 1 shl i) {
                    list += resources.getStringArray(categoryArr[i])
                }
            }
            list.shuffled()
        }
        val resultArray = IntArray(strArray.size)

        idx = 0
        binding.tvText.text = strArray[idx]
        binding.tvCount.text = "${idx + 1}/${strArray.size}"


        binding.btnPass.setOnClickListener {
            if (idx == strArray.lastIndex) {
                Toast.makeText(this, "끝까지 푸셨습니다.", Toast.LENGTH_SHORT).show()
                val resultDialog = GameResultDialog(size,correctCount,basePass - remainPass){
                    finish()
                }.apply {
                    isCancelable = false
                }
                resultDialog.show(this.supportFragmentManager, "GameResultDialog")
            }
            if (remainPass == 0) {
                Toast.makeText(this, "주어진 패스를 전부 사용하셨습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            resultArray[idx] = 1
            remainPass--
            binding.tvRemainPassCount.text = "$remainPass"
            idx++
            binding.tvText.text = strArray[idx]
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
        }

        binding.btnCount.setOnClickListener {
            if (idx == strArray.lastIndex) {
                Toast.makeText(this, "끝까지 푸셨습니다.", Toast.LENGTH_SHORT).show()
                val resultDialog = GameResultDialog(size,correctCount,basePass - remainPass){
                    finish()
                }.apply {
                    isCancelable = false
                }
                resultDialog.show(this.supportFragmentManager, "GameResultDialog")
            }
            resultArray[idx] = 2
            idx++
            correctCount++
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
            binding.tvCorrectCount.text = "$correctCount"
            binding.tvText.text = strArray[idx]
        }
    }

    companion object {
        private const val TAG = "TextGameActivity"
    }
}