package com.campbell.campbell.wiemodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.campbell.campbell.databinding.FragmentFeedBinding
import com.campbell.campbell.model.Camp
import com.campbell.campbell.service.CampAPIService
import com.campbell.campbell.service.CampDatabase
import com.campbell.campbell.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : BaseViewModel(application) {
    private val campAPIService = CampAPIService()

    //internetten veriyi sürekli indirmiyor eski ineni
    //siliyor yerine tekrar indiriyor
    private val disposable = CompositeDisposable()
    private var customSharedPreferences = CustomSharedPreferences(getApplication())
    //private var refresTime = 0.1 * 60 * 1000 * 1000 * 1000L // 10 dakika hesabı

    val canliVeri = MutableLiveData<List<Camp>>()

    fun refreshData() {
        getDataFromAPI()
    }

    private fun getDataFromSQLite() {

        launch {
            val camps = CampDatabase(getApplication()).campDAo().getAllCamp()
            showCamps(camps)
        }
    }

    private fun getDataFromAPI() {
        disposable.add(
            campAPIService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Camp>>() {
                    override fun onSuccess(t: List<Camp>) {
                        storeInSQLite(t)
                    }
                    override fun onError(e: Throwable) {
                    }
                })
        )
    }
    fun refreshFromAPI() {
        getDataFromAPI()
    }
    private fun showCamps(campList: List<Camp>) {
        canliVeri.value = campList
    }
    private fun storeInSQLite(list: List<Camp>) {
        launch {
            val dao = CampDatabase(getApplication()).campDAo()
            dao.deleteAll()
            val listLong = dao.insertAll(*list.toTypedArray())
            var i = 0;
            while (i < list.size) {
                list[i].uuid = listLong[i].toInt()
                i++
            }
            showCamps(list)
        }
        customSharedPreferences.saveTime(System.nanoTime())
    }

}