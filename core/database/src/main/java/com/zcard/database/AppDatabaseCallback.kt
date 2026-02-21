package com.zcard.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zcard.database.dao.AssetDao
import com.zcard.database.data.InitialData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class AppDatabaseCallback(
    private val assetDaoProvider: Provider<AssetDao>,
) : RoomDatabase.Callback() {

    // 데이터베이스가 처음 생성될 때 호출 (앱 설치 후 첫 실행 시)
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            // 미리 정의된 초기 데이터를 데이터베이스에 삽입
            assetDaoProvider.get().insertAll(InitialData.getInitialBackgrounds())
            assetDaoProvider.get().insertAll(InitialData.getInitialAssets())
        }
    }
}
