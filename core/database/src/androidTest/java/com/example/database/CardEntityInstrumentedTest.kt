package com.example.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.database.dao.CardDao
import com.example.database.entity.AssetEntity
import com.example.database.entity.CardEntity
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
class CardEntityInstrumentedTest {
    private lateinit var cardDao: CardDao
    private lateinit var db: AppDatabase

    private val card = CardEntity(
        cardId = 1,
        title = "card_001",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        cardDao = db.cardDao()
    }

    @Test
    fun insertAndReadAsset() = runBlocking {
        val id = cardDao.insertCard(card)
        val cards = cardDao.getAllCards()

        assertThat(cards).hasSize(1)
        assertThat(id).isEqualTo(1)
        assertThat(cards.first().title).isEqualTo("card_001")
    }

    @Test
    fun updateCardBackground() = runBlocking {
        val asset = AssetEntity(
            assetId = 0, // autoGenerate면 0 넣기
            assetType = "OBJECT",
            unityKey = "m_003",
            thumbnailKey = "thumb_m_003"
        )
        db.assetDao().insertAsset(asset)

        val id = cardDao.insertCard(card)
        cardDao.updateBackgroundAssetId(id, 1)

        val card = cardDao.getCardById(id)

        assertThat(card.backgroundAssetId?.let { db.assetDao().getById(it).unityKey }).isEqualTo("m_003")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}