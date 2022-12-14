package com.example.converter.UIConverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import com.example.converter.databinding.FragmentNumPadBinding


class NumPadFragment : Fragment() {
    private val dataModel: DataModel by viewModels()
    private var _binding: FragmentNumPadBinding? = null
    private val binding: FragmentNumPadBinding get() = _binding!!
    private lateinit var editTextBefore: EditText
    private lateinit var editTextAfter: EditText
    private lateinit var viewCurrency:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNumPadBinding.inflate(inflater, container, false)
        return binding.root
    }




}