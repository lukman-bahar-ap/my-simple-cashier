package com.example.mycashier.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class, CartItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun posDao(): PosDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pos_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.posDao())
                }
            }
        }

        suspend fun populateDatabase(posDao: PosDao) {
            // dummy data
            posDao.insertProduct(Product(name = "Mie Sakera Lvl 0", category = "Makanan", price = 10000.0))
            posDao.insertProduct(Product(name = "Mie Sakera Pedas Lvl 1", category = "Makanan", price = 10000.0))
            posDao.insertProduct(Product(name = "Mie Sakera Pedas Lvl 2", category = "Makanan", price = 10000.0))
            posDao.insertProduct(Product(name = "Mie Sakera Pedas Lvl 3", category = "Makanan", price = 11000.0))
            posDao.insertProduct(Product(name = "Mie Sakera Pedas Lvl 4", category = "Makanan", price = 11000.0))
            posDao.insertProduct(Product(name = "Mie Sakera Pedas Lvl 5", category = "Makanan", price = 11000.0))
            posDao.insertProduct(Product(name = "Paket 1: 2 Porsi + 2 Es Teh ", category = "Lainya", price = 24000.0))
            posDao.insertProduct(Product(name = "Paket 2: 2 Porsi + 2 Es Teh + Dimsum ", category = "Lainya", price = 34000.0))
            posDao.insertProduct(Product(name = "Paket 3: 3 Porsi + 2 Es Teh + Dimsum ", category = "Lainya", price = 44000.0))
            posDao.insertProduct(Product(name = "Kopi Susu Ala Sakera", category = "Minuman", price = 13000.0))
            posDao.insertProduct(Product(name = "Es Teh Manis", category = "Minuman", price = 3000.0))
            posDao.insertProduct(Product(name = "Es Jeruk", category = "Minuman", price = 5000.0))
            posDao.insertProduct(Product(name = "Es Milo", category = "Minuman", price = 7000.0))
            posDao.insertProduct(Product(name = "Air Mineral", category = "Minuman", price = 4000.0))
            posDao.insertProduct(Product(name = "Roti Bakar", category = "Lainya", price = 10000.0))
            posDao.insertProduct(Product(name = "Udang Rambutan", category = "Lainya", price = 10000.0))
            posDao.insertProduct(Product(name = "Kentang Goreng", category = "Lainya", price = 10000.0))
            posDao.insertProduct(Product(name = "Lumpia Ayam", category = "Lainya", price = 10000.0))
            posDao.insertProduct(Product(name = "Dimsum", category = "Lainya", price = 12000.0))
        }
    }
}