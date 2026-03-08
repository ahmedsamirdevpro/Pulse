package com.ahmedsamir.pulse.feature.notifications.di

import com.ahmedsamir.pulse.feature.notifications.data.repository.NotificationsRepositoryImpl
import com.ahmedsamir.pulse.feature.notifications.domain.repository.NotificationsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        notificationsRepositoryImpl: NotificationsRepositoryImpl
    ): NotificationsRepository
}