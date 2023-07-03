package com.potatomeme.recreation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.potatomeme.recreation.Key.GAME_KEY1
import com.potatomeme.recreation.Key.GAME_KEY3
import com.potatomeme.recreation.Key.GAME_KEY5
import com.potatomeme.recreation.Key.SELECT_GAME
import com.potatomeme.recreation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var idx: Int = 0
    private var count: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        binding.btnPlayTextGame.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            intent.putExtra(SELECT_GAME, GAME_KEY1)
            startActivity(intent)
        }

        binding.btnPlayImageGame.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            intent.putExtra(SELECT_GAME, GAME_KEY3)
            startActivity(intent)
        }
        binding.btnPlayImageGameWithEditText.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            intent.putExtra(SELECT_GAME, GAME_KEY5)
            startActivity(intent)
        }
    }
}