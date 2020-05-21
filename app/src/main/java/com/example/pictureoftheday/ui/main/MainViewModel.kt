//основная бизнес логика - загрузка фотографий, обработка ошибок и т.д.
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
//загрузка данных с сайта api.nasa.gov
data class PODServerResponseData(
    val date: String?,
    val explanation: String?,
    val hdurl: String?,
    val media_type: String?,
    val service_version: String?,
    val title: String?,
    val url: String?
)

//интерфейс для выхода в интернет
interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<PODServerResponseData>
}
//обращаемся к api потом добавляем interface PictureOfTheDayAPI
class PODRetrofitImpl {
    fun getApi(): PictureOfTheDayAPI {
        val podRetrofit:Retrofit = Retrofit.Builder() //с помощью билдера создаем экземпляр класса
            .baseUrl("https://api.nasa.gov/") // базовый урл к нему присоединяется end point planetary/apod
            .addConverterFactory(  //конвертирует json ответ сервера в структуру данных data class PODServerResponseDatqa
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)//получаем готовую реализацию интерфейса в которой можно
        //вызвать метод fun getPictureOfTheDay
    }
}