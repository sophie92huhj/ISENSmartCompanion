package fr.isen.soubry.isensmartcompanion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Interaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Ajout du TypeConverter pour la gestion des dates
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun interactionDao(): InteractionDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database"
                )
                    .fallbackToDestructiveMigration() // ✅ Évite les conflits de version
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
