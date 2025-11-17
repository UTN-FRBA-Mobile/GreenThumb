package com.utn.greenthumb.di

import android.content.Context
import com.utn.greenthumb.scheduler.AlarmScheduler
import com.utn.greenthumb.scheduler.AndroidAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {
    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AndroidAlarmScheduler(context)
    }
}
