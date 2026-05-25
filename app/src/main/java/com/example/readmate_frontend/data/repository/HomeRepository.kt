package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.HomeApi
import com.example.readmate_frontend.data.model.response.home.HomeResponse
import javax.inject.Inject

data class HomeRepository @Inject constructor(
    private  val api: HomeApi){
    suspend fun getHomeData(): HomeResponse{
        return api.getHomeData()
    }
}
