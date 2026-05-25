package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.response.home.HomeResponse
import retrofit2.http.GET

interface HomeApi {
    @GET("api/home")
        suspend fun getHomeData(): HomeResponse

}