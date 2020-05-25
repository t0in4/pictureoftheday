//main app logic - loading pictures, errors handling etc
package com.example.pictureoftheday.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModel (
    private val liveDataForViewToObserve: MutableLiveData<PODServerResponseData?> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
): ViewModel() {
    fun getData(): LiveData<PODServerResponseData?> {
        sendServerRequest()
        return liveDataForViewToObserve
    }
    private fun sendServerRequest() {
        retrofitImpl.getApi().getPictureOfTheDay("DEMO_KEY").enqueue(object :
            Callback<PODServerResponseData> {
            override fun onResponse(call: Call<PODServerResponseData>, response: Response<PODServerResponseData>){
                if (response.isSuccessful && response.body() != null) {
                    liveDataForViewToObserve.value = response.body()!!
                } else {
                    liveDataForViewToObserve.value = null
                }
            }
            override fun onFailure(call: Call<PODServerResponseData>, t: Throwable){
                liveDataForViewToObserve.value = null
            }
        })
    }
}
//download data from api.nasa.gov
data class PODServerResponseData(
    val date: String?,
    val explanation: String?,
    val hdurl: String?,
    val media_type: String?,
    val service_version: String?,
    val title: String?,
    val url: String?
)

//interface for web
interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<PODServerResponseData>
}
//call to api then add suffix in interface PictureOfTheDayAPI
class PODRetrofitImpl {
    fun getApi(): PictureOfTheDayAPI {
        val podRetrofit:Retrofit = Retrofit.Builder() //builder create class init
            .baseUrl("https://api.nasa.gov/") // base url + end point planetary/apod
            .addConverterFactory(  //convert server response in json into structure of data class PODServerResponseDatqa
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)//gaining ready to use interface implementation
        //and can call fun getPictureOfTheDay
    }
}