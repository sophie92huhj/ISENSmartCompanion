package fr.isen.soubry.isensmartcompanion.network

import fr.isen.soubry.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface EventApiService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
