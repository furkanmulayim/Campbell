package com.example.campbell.wiemodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.campbell.model.Camp
import com.example.campbell.service.CampDatabase
import kotlinx.coroutines.launch

class CampViewModel(application: Application) : BaseViewModel(application) {

    val campLiveData = MutableLiveData<Camp>()

    fun roomVerisiniAl(uuid: Int) {
        launch {

            val dao = CampDatabase(getApplication()).campDAo()
            val camp = dao.getCamp(uuid)
            campLiveData.value = camp
        }
    }
}