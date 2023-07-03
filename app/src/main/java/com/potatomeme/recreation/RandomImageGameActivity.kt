package com.potatomeme.recreation

import android.content.Context
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.potatomeme.recreation.databinding.ActivityRandomImageGameBinding
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread
import kotlin.math.log

class RandomImageGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRandomImageGameBinding

    private var idx: Int = 0
    private val categoryArr = arrayOf(
        R.array.MOVIE_WESTERN_KEY,
        R.array.MOVIE_KOREA_KEY,
        R.array.MOVIE_COMIC_KEY,
    )

    private var correctCount: Int = 0
    private var basePass: Int = 0
    private var remainPass: Int = 0

    private lateinit var movieApi : MovieApi

    var movieImgs : List<String> = listOf()
    var movieImgsIdx = 0
    var currentMovieName = ""
    var currentMovieThumbnail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomImageGameBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://movie.daum.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieApi = retrofit.create(MovieApi::class.java)

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
                                    val resultDialog = GameResultDialog(
                                        size,
                                        correctCount,
                                        basePass - remainPass
                                    ) {
                                        finish()
                                    }.apply {
                                        isCancelable = false
                                    }
                                    resultDialog.show(
                                        this.supportFragmentManager,
                                        "GameResultDialog"
                                    )
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
            for (i in categoryArr.indices) {
                if (status and (1 shl i) == 1 shl i) {
                    list += resources.getStringArray(categoryArr[i])
                }
            }
            list.shuffled()
        }
        val resultArray = IntArray(strArray.size)

        idx = 0
        binding.tvCount.text = "${idx + 1}/${strArray.size}"

        getMovieData(strArray[idx])

        binding.btnPass.setOnClickListener {
            if (idx == strArray.lastIndex) {
                Toast.makeText(this, "끝까지 푸셨습니다.", Toast.LENGTH_SHORT).show()
                val resultDialog = GameResultDialog(size, correctCount, basePass - remainPass) {
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
            binding.tvText.visibility = View.INVISIBLE
            resultArray[idx] = 1
            remainPass--
            binding.tvRemainPassCount.text = "$remainPass"
            idx++
            getMovieData(strArray[idx])
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
        }

        binding.btnCount.setOnClickListener {
            if (idx == strArray.lastIndex) {
                Toast.makeText(this, "끝까지 푸셨습니다.", Toast.LENGTH_SHORT).show()
                val resultDialog = GameResultDialog(size, correctCount, basePass - remainPass) {
                    finish()
                }.apply {
                    isCancelable = false
                }
                resultDialog.show(this.supportFragmentManager, "GameResultDialog")
            }
            resultArray[idx] = 2
            idx++
            correctCount++
            binding.tvText.visibility = View.INVISIBLE
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
            binding.tvCorrectCount.text = "$correctCount"
            getMovieData(strArray[idx])
        }

        binding.btnNext.setOnClickListener {
            movieImgsIdx++
            if (movieImgs.size == movieImgsIdx) movieImgsIdx = 0
            Glide.with(this@RandomImageGameActivity)
                .load(movieImgs[movieImgsIdx])
                .into(binding.ivMovie)
        }

        binding.btnCorrect.setOnClickListener {
            binding.tvText.visibility = View.VISIBLE
            binding.tvText.text = currentMovieName
            Glide.with(this@RandomImageGameActivity)
                .load(currentMovieThumbnail)
                .into(binding.ivMovie)
        }

        binding.fbReload.setOnClickListener{
            Glide.with(this@RandomImageGameActivity)
                .load(movieImgs[movieImgsIdx])
                .into(binding.ivMovie)
        }
    }

    private fun getMovieData(key:String){
        Thread {
            try {
                val jsoup1 =
                    Jsoup.connect("https://movie.daum.net/moviedb/main?movieId=$key")
                        .get()
                val movieName = jsoup1.select("span.txt_tit").find { it != null }?.text()
                val mainImgUrlRegex = Regex("""url\((.*?)\)""")
                val mainImgUrl = mainImgUrlRegex.find(
                    jsoup1.select("span.bg_img").first()?.attr("style") ?: ""
                )?.groupValues?.getOrNull(1)
                currentMovieName = movieName ?: ""
                currentMovieThumbnail = mainImgUrl ?: ""

                Log.d(TAG, "onCreate: jsoupfind $key")
                Log.d(TAG, "onCreate: jsoupfind $mainImgUrl")
                Log.d(TAG, "onCreate: jsoupfind $movieName")

                val cal = movieApi.getPhotos(key)
                cal.enqueue(object : Callback<MoviePhotoResponse> {
                    override fun onResponse(
                        call: Call<MoviePhotoResponse>,
                        response: Response<MoviePhotoResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val moviePhotoResponse = response.body()
                            moviePhotoResponse?.let { photoResponse ->
                               movieImgs = photoResponse.contents.map{ it.imageUrl}
                            }
                            movieImgsIdx = 0
                            runOnUiThread{
                                Log.d(TAG, "onResponse: test")
                                Glide.with(this@RandomImageGameActivity)
                                    .load(movieImgs[movieImgsIdx])
                                    .into(binding.ivMovie)
                            }
                        } else {
                            Toast.makeText(applicationContext, "API 호출 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<MoviePhotoResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "API 호출 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    companion object {
        private const val TAG = "RandomImageGameActivity"
    }

}