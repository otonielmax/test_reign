package com.otoniel.testreign.ui.viewmodel

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.otoniel.testreign.R
import com.otoniel.testreign.data.model.HitsModel
import com.otoniel.testreign.domain.GetHitsUseCase
import kotlinx.coroutines.launch

class HitsViewModel: ViewModel() {

    val hitsModel = MutableLiveData<List<HitsModel>>()
    val loading = MutableLiveData<Boolean>()

    var getHitsUseCase = GetHitsUseCase()

    fun getHits(context: Context) {
        viewModelScope.launch {
            loading.postValue(true)

            val result: List<HitsModel>? = getHitsUseCase()
            if (!result.isNullOrEmpty()) {
                addHitsToLocalData(context, result)
            } else {
                getHitsLocal(context)
            }

            loading.postValue(false)
        }
    }

    fun getHitsLocal(context: Context) {
        val sharedPref = context.getSharedPreferences(
            "data", Context.MODE_PRIVATE)

        val gson = Gson()
        val itemType = object : TypeToken<List<HitsModel>>() {}.type
        var list: List<HitsModel>? = gson.fromJson<List<HitsModel>>(sharedPref.getString("hits", ""), itemType)
        if (list.isNullOrEmpty()) {
            list = emptyList()
        }

        filterHitsEnabled(list)

        loading.postValue(false)
    }

    fun addHitsToLocalData(context: Context, newList: List<HitsModel>) {
        val sharedPref = context.getSharedPreferences(
            "data", Context.MODE_PRIVATE)

        val gson = Gson()
        val itemType = object : TypeToken<List<HitsModel>>() {}.type
        val list: List<HitsModel>? = gson.fromJson<List<HitsModel>>(sharedPref.getString("hits", ""), itemType)
        var mutableList: MutableList<HitsModel> = mutableListOf()
        val newMutableList: MutableList<HitsModel> = mutableListOf()

        if (list.isNullOrEmpty()) {
            with(sharedPref.edit()) {
                putString("hits", gson.toJson(newList))
                apply()
            }
            filterHitsEnabled(newList)
        } else {
            mutableList = list.toMutableList()
            for (item in newList) {
                val exist = mutableList.filter { it.story_id == item.story_id }
                if (exist.isNullOrEmpty()) {
                    newMutableList.add(item)
                }
            }
            newMutableList.addAll(mutableList)

            with(sharedPref.edit()) {
                putString("hits", gson.toJson(newMutableList))
                apply()
            }
            filterHitsEnabled(newMutableList)
        }
    }

    fun filterHitsEnabled(list: List<HitsModel>) {
        val mutableList: MutableList<HitsModel> = mutableListOf()

        for (item in list) {
            if (!item.delete) {
                mutableList.add(item)
            }
        }

        hitsModel.postValue(mutableList)
    }

    fun deleteHits(context: Context, position: Int) {
        Log.e("deleteHits","init")
        val sharedPref = context.getSharedPreferences(
            "data", Context.MODE_PRIVATE)

        val gson = Gson()
        val itemType = object : TypeToken<List<HitsModel>>() {}.type
        val list: List<HitsModel>? = gson.fromJson<List<HitsModel>>(sharedPref.getString("hits", ""), itemType)
        if (!list.isNullOrEmpty()) {
            Log.e("deleteHits","Preparando para borrar")
            val mutableList: MutableList<HitsModel> = list.toMutableList()
            mutableList[position].delete = true

            with(sharedPref.edit()) {
                putString("hits", gson.toJson(mutableList))
                apply()
            }

            filterHitsEnabled(mutableList)
        }
    }
}