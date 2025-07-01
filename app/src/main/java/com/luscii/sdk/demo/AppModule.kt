package com.luscii.sdk.demo

import android.content.Context
import com.luscii.sdk.Luscii
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
sealed interface AppModule {
    companion object {
        @Provides
        @Singleton
        fun provideLuscii(@ApplicationContext context: Context) = Luscii {
            applicationContext = context
            useDynamicColors = true
        }
    }
}