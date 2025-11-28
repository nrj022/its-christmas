package com.itschristmas.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.itschristmas.database.dao.AssetDao
import com.itschristmas.database.dao.CardDao
import com.itschristmas.database.data.InitialData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class AppDatabaseCallback(
    private val assetDaoProvider: Provider<AssetDao>,
    private val cardDaoProvider: Provider<CardDao>
) : RoomDatabase.Callback() {

    // 데이터베이스가 처음 생성될 때 호출됩니다. (앱 설치 후 첫 실행 시)
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            // 미리 정의된 초기 데이터를 데이터베이스에 삽입합니다.
            assetDaoProvider.get().insertAll(InitialData.getInitialAssets())
            assetDaoProvider.get().insertAll(InitialData.getInitialBackgrounds())
            cardDaoProvider.get().insertCard(InitialData.getInitialCard())
        }
    }
}
