package com.weatheradvisor.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {

    @Provides
    @Named("openweather")
    fun provideApiKey(): String = ApiKeyProvider.apiKey
}


object ApiKeyProvider {
    lateinit var apiKey: String
}