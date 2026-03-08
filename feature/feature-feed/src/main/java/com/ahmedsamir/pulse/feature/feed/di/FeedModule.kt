package com.ahmedsamir.pulse.feature.feed.di

import com.ahmedsamir.pulse.feature.feed.data.repository.FeedRepositoryImpl
import com.ahmedsamir.pulse.feature.feed.domain.repository.FeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeedModule {

    @Binds
    @Singleton
    abstract fun bindFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl
    ): FeedRepository
}