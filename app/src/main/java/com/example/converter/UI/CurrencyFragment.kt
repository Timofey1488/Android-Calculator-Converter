package com.example.converter.UI

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.converter.databinding.FragmentCurrencyBinding
import javax.security.auth.callback.Callback


class CurrencyFragment : Fragment() {
    lateinit var binding: FragmentCurrencyBinding
    private val dataModel: DataModel by activityViewModels()
    lateinit var editTextBefore: EditText
    lateinit var editTextAfter: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
12312
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prevText: EditText = binding.PrevText
        editTextBefore = binding.PrevText
        editTextBefore.setShowSoftInputOnFocus(false)
        editTextAfter = binding.AfterText
        editTextAfter.setShowSoftInputOnFocus(false)


        dataModel.message.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.PrevText.append(it)
        }
        dataModel.delete.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.PrevText.setText(it)
        }
        dataModel.messageTemp.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.PrevText.append(it)
        }
        dataModel.paste.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.AfterText.setText(it)
        }
        dataModel.spinBeforeSet.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.SpinnerBefore.setSelection(it.toInt())
        }
        dataModel.spinAfterSet.observe(viewLifecycleOwner) {
            binding.PrevText.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.SpinnerAfter.setSelection(it.toInt())
        }

        dataModel.proButton.observe(viewLifecycleOwner) {
            if (it == "true") {
                binding.apply {

                    binding.CopyButtonBefore.visibility = View.VISIBLE
                    binding.CopyButtonAfter.visibility = View.VISIBLE
                    binding.PasteButtonBefore.visibility = View.VISIBLE
                    binding.SwapButton.visibility = View.VISIBLE
                }
            } else {
                binding.apply {

                    binding.CopyButtonBefore.visibility = View.INVISIBLE
                    binding.CopyButtonAfter.visibility = View.INVISIBLE
                    binding.PasteButtonBefore.visibility = View.INVISIBLE
                    binding.SwapButton.visibility = View.INVISIBLE
                }
            }

            super.onViewCreated(view, savedInstanceState)
        }
    }
}