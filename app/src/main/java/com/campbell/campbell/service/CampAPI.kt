package com.campbell.campbell.service

import com.campbell.campbell.model.Camp
import io.reactivex.Single
import retrofit2.http.GET

interface CampAPI {
    //https://raw.githubusercontent.com/furkanmulayim/kaynakDosyasi/main/campbell.json
    @GET("furkanmulayim/kaynakDosyasi/main/campbell.json")
    fun getCamp(): Single<List<Camp>>
    // single neden kullandık -> sadece bir defa islemi yapar ve durur
}