package com.example.converter.UI

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel : ViewModel() {
    val message: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val messageTemp: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val minusDelete: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val delete: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val paste: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val change: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val spinBeforeSet: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val spinAfterSet: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val proButton: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val copy: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}

