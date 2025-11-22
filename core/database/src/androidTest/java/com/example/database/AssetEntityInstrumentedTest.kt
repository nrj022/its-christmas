package com.example.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.database.dao.AssetDao
import com.example.database.data.InitialData
import com.example.database.entity.AssetEntity
import com.example.database.entity.AssetType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AssetEntityInstrumentedTest {
    private lateinit var assetDao: AssetDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        assetDao = db.assetDao()
    }

    @Test
    fun insertAndReadAsset() = runBlocking {
        assetDao.insertAll(InitialData.getInitialAssets())

        val asset = AssetEntity(
            assetId = 0, // autoGenerate면 0 넣기
            assetType = AssetType.OBJECT,
            unityKey = "m_003",
            thumbnailKey = "thumb_m_003"
        )
        val id = assetDao.insertAsset(asset)
        val assets = assetDao.getAll()

        assertThat(assets).hasSize(3)
        assertThat(id).isGreaterThan(2)
        assertThat(assets.first().unityKey).isEqualTo("m_001")
        assertThat(assets.last().unityKey).isEqualTo("m_003")
    }

    @Test
    fun readByType() = runBlocking {
        val asset = AssetEntity(
            assetId = 0, // autoGenerate면 0 넣기
            assetType = AssetType.BACKGROUND,
            unityKey = "bg_001",
            thumbnailKey = "thumb_bg_001"
        )

        val id = assetDao.insertAsset(asset)
        val assets = assetDao.getByType(AssetType.OBJECT)

        assertThat(id).isNotIn(assets)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}