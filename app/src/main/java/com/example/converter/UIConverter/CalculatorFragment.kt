package com.example.converter.UIConverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.example.converter.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {
    lateinit var binding: FragmentCalculatorBinding
    private val dataModel: DataModel by activityViewModels()
    lateinit var editTextBefore: EditText
    lateinit var editTextAfter: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prevText: EditText = binding.PrevText
        editTextBefore = binding.PrevText
        editTextBefore.setShowSoftInputOnFocus(false)
        editTextBefore.setCursorVisible(true)
        editTextAfter = binding.AfterText
        editTextAfter.setShowSoftInputOnFocus(false)

        dataModel.message.observe(viewLifecycleOwner) {
            binding.PrevText.append(it)
        }
        dataModel.delete.observe(viewLifecycleOwner) {
            binding.PrevText.setText(it)
        }
        dataModel.messageTemp.observe(viewLifecycleOwner) {
            binding.PrevText.text.insert(0, "-")
            binding.PrevText.setSelection(binding.PrevText.text.length)
        }
        dataModel.minusDelete.observe(viewLifecycleOwner) {
            binding.PrevText.text.delete(0,1)
            binding.PrevText.setSelection(binding.PrevText.text.length)
        }
        dataModel.paste.observe(viewLifecycleOwner) {
            binding.AfterText.setText(it)
        }
        dataModel.change.observe(viewLifecycleOwner) {
            binding.AfterText.append(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalculatorFragment()
    }

}