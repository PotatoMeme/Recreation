package com.potatomeme.recreation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.potatomeme.recreation.databinding.ActivityRandomImageGameBinding

class RandomImageGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRandomImageGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomImageGameBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        val single = intent.getBooleanExtra(Key.SELECT_SINGLE, true)
    }
}