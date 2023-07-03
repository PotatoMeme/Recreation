package com.potatomeme.recreation

import android.content.Context
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.potatomeme.recreation.databinding.ActivityRandomImageGameBinding
import com.potatomeme.recreation.databinding.ActivityRandomImageGameWithEditTextBinding
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class RandomImageGameWithEditTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRandomImageGameWithEditTextBinding

    private var idx: Int = 0
    private val categoryArr = arrayOf(
        R.array.MOVIE_WESTERN_KEY,
        R.array.MOVIE_KOREA_KEY,
        R.array.MOVIE_COMIC_KEY,
    )

    private var correctCount: Int = 0
    private var basePass: Int = 0
    private var remainPass: Int = 0

    private lateinit var movieApi: MovieApi
    private var isUserCorrectMovie = false
    private var inputSuccess = false

    var movieImgs: List<String> = listOf()
    var movieImgsIdx = 0
    private var currentMovieName = ""
    private var currentMovieThumbnail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomImageGameWithEditTextBinding.inflate(this.layoutInflater)
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
            if (inputSuccess){
                Toast.makeText(this, "통과를 눌러주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
            resultArray[idx] = 1
            remainPass--
            binding.tvRemainPassCount.text = "$remainPass"
            idx++
            getMovieData(strArray[idx])
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
            isUserCorrectMovie = false
        }

        binding.btnCount.setOnClickListener {
            if (!inputSuccess){
                Toast.makeText(this, "정답을 맞추지 못하셨습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
            binding.tvCount.text = "${idx + 1}/${strArray.size}"
            binding.tvCorrectCount.text = "$correctCount"
            getMovieData(strArray[idx])
            isUserCorrectMovie = false
            inputSuccess =false
        }

        binding.btnNext.setOnClickListener {
            movieImgsIdx++
            if (movieImgs.size == movieImgsIdx) movieImgsIdx = 0
            Glide.with(this@RandomImageGameWithEditTextActivity)
                .load(movieImgs[movieImgsIdx])
                .into(binding.ivMovie)
        }

        binding.etText.setOnEditorActionListener{ textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 내리기
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etText.windowToken, 0)
                handled = true
            }

            handled
        }

        binding.btnInput.setOnClickListener{
            if (isUserCorrectMovie){
                Toast.makeText(this, "정답을 이미 보셨습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val text = binding.etText.text.toString().replace(" ","")
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            if(text.isEmpty()){
                Toast.makeText(this, "값을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentMovieName.replace(" ","").contains(text)){
                Toast.makeText(this, "맞았습니다.", Toast.LENGTH_SHORT).show()
                Glide.with(this@RandomImageGameWithEditTextActivity)
                    .load(currentMovieThumbnail)
                    .into(binding.ivMovie)
                inputSuccess = true
            }else{
                Toast.makeText(this, "틀렸습니다.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onCreate: $currentMovieName")
            }
            binding.etText.text.clear()

        }

        binding.btnCorrect.setOnClickListener {
            if (!inputSuccess){
                isUserCorrectMovie = true
            }
            Glide.with(this@RandomImageGameWithEditTextActivity)
                .load(currentMovieThumbnail)
                .into(binding.ivMovie)
        }

        binding.fbReload.setOnClickListener {
            getMovieData(strArray[idx])
        }
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }


    private fun getMovieData(key: String) {
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
                                movieImgs = photoResponse.contents.map { it.imageUrl }
                            }
                            movieImgsIdx = 0
                            runOnUiThread {
                                Log.d(TAG, "onResponse: test")
                                Glide.with(this@RandomImageGameWithEditTextActivity)
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