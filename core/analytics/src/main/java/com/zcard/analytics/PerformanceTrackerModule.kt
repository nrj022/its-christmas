package com.zcard.analytics

import com.zcard.domain.analytics.PerformanceTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PerformanceTrackerModule {

    @Binds
    @Singleton
    abstract fun providePerformanceTracker(
        performanceTrackerImpl: PerformanceTrackerImpl
    ): PerformanceTracker
}