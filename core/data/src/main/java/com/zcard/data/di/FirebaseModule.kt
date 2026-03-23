package com.zcard.data.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // 별도 프로세스(카드 편집 화면)에서 실행될 경우를 대비
    private fun initializeFirebase(context: Context) {
        if(FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(@ApplicationContext context: Context): FirebaseStorage {
        initializeFirebase(context)
        return Firebase.storage
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        initializeFirebase(context)
        return FirebaseAuth.getInstance()
    }
}