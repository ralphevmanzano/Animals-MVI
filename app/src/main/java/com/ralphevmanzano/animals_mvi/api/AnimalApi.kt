package com.ralphevmanzano.animals_mvi.api

import com.ralphevmanzano.animals_mvi.model.Animal
import retrofit2.http.GET

interface AnimalApi {

    @GET("animals.json")
    suspend fun getAnimals(): List<Animal>
}