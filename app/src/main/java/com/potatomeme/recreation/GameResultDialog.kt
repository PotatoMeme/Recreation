package com.potatomeme.recreation

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.potatomeme.recreation.databinding.DialogGameResultBinding

class GameResultDialog(
    val size: Point,
    val correct : Int,
    val pass : Int,
    val correctFunction: () -> (Unit),
) : DialogFragment() {
    private var _binding: DialogGameResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogGameResultBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.tvPass.append("$pass")
        binding.tvCorrect.append("$correct")
        
        binding.btnCorrect.setOnClickListener {
            correctFunction()
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