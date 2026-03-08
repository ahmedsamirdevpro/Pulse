package com.ahmedsamir.pulse.feature.comments.di

import com.ahmedsamir.pulse.feature.comments.data.repository.CommentsRepositoryImpl
import com.ahmedsamir.pulse.feature.comments.domain.repository.CommentsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentsModule {

    @Binds
    @Singleton
    abstract fun bindCommentsRepository(
        commentsRepositoryImpl: CommentsRepositoryImpl
    ): CommentsRepository
}