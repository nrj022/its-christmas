package com.zcard.database

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zcard.database.dao.AssetDao
import com.zcard.database.data.InitialData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import java.util.concurrent.atomic.AtomicBoolean

class AppDatabaseCallback(
    private val assetDaoProvider: Provider<AssetDao>,
    private val prefs: SharedPreferences
) : RoomDatabase.Callback() {

    companion object {
        private const val TAG = "AppDatabaseCallback"
        private const val PREF_KEY_INITIAL_DATA_VERSION = "initial_data_version"
        private const val TARGET_DATA_VERSION = 1
    }

    private val isInserting = AtomicBoolean(false)

    // 데이터베이스가 처음 생성될 때 호출 (앱 설치 후 첫 실행 시)
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        checkAndInsertNewInitialData()
    }

    // 앱이 열릴때마다 실행
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // onCreate 직후 onOpen 호출 시 중복 방지
        if (!isInserting.get()) {
            checkAndInsertNewInitialData()
        }
    }

    private fun checkAndInsertNewInitialData() {
        val currentVersion = prefs.getInt(PREF_KEY_INITIAL_DATA_VERSION, 0)

        // AtomicBoolean으로 값 비교와 세팅을 거의 동시에 수행해 다른 스레드의 동시 수행을 완전히 방지
        if (currentVersion < TARGET_DATA_VERSION && isInserting.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Room은 기본적으로 한 번에 하나의 쓰기 작업만 수행
                    assetDaoProvider.get().insertAll(InitialData.getInitialBackgrounds())
                    assetDaoProvider.get().insertAll(InitialData.getInitialAssets())

                    // 업데이트 완료 후 데이터 버전 기록
                    prefs.edit { putInt(PREF_KEY_INITIAL_DATA_VERSION, TARGET_DATA_VERSION) }
                    Log.d(TAG, "Initial data synced to v$TARGET_DATA_VERSION")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync initial data", e)
                } finally {
                    isInserting.set(false)
                }
            }
        }
    }
}
