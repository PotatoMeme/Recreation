package com.potatomeme.recreation

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.potatomeme.recreation.databinding.DialogTextGameSettingBinding

class TextGameSettingDialog(
    val size: Point,
    val correctFunction: (timeChecked: Boolean, time: Int, passChecked: Boolean, pass: Int) -> (Unit),
) : DialogFragment() {


    private var _binding: DialogTextGameSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogTextGameSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.npMinute.apply {
            minValue = 0
            value = 0
            maxValue = 10
            wrapSelectorWheel = false
        }

        binding.npSec.apply {
            minValue = 0
            value = 0
            maxValue = 59
        }

        binding.cbTime.setOnClickListener {
            if (binding.cbTime.isChecked) {
                binding.textView.visibility = View.VISIBLE
                binding.textView2.visibility = View.VISIBLE
                binding.npMinute.visibility = View.VISIBLE
                binding.npSec.visibility = View.VISIBLE
            } else {
                binding.textView.visibility = View.GONE
                binding.textView2.visibility = View.GONE
                binding.npMinute.visibility = View.GONE
                binding.npSec.visibility = View.GONE
            }
        }

        binding.cbPass.setOnClickListener {
            if (binding.cbPass.isChecked) {
                binding.etPass.visibility = View.VISIBLE
            } else {
                binding.etPass.visibility = View.GONE
            }
        }
        binding.btnCorrect.visibility = View.VISIBLE
        binding.btnCorrect.setOnClickListener {
            val timeChecked = binding.cbTime.isChecked
            val time = binding.npMinute.value * 60 + binding.npSec.value
            val passChecked = binding.cbPass.isChecked
            val pass = binding.etPass.text.toString().toIntOrNull().let {
                if (passChecked) {
                    if (it == null) {
                        Toast.makeText(context, "Pass 값을 확인해주세요", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    it
                } else 0
            }
            if (timeChecked && time == 0){
                Toast.makeText(context, "0초 입니다. 시간 값을 확인해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            correctFunction(timeChecked, time, passChecked, pass)
            dismiss()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = (size.x * 0.9).toInt()
        params?.height = (size.y * 0.8).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}