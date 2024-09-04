package com.ralphevmanzano.animals_mvi.view

sealed class MainIntent {
    data object FetchAnimals: MainIntent()
}