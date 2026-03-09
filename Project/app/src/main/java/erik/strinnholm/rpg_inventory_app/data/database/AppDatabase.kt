package erik.strinnholm.rpg_inventory_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import erik.strinnholm.rpg_inventory_app.data.model.Character
import erik.strinnholm.rpg_inventory_app.data.dao.CharacterDao
import erik.strinnholm.rpg_inventory_app.data.model.Coins
import erik.strinnholm.rpg_inventory_app.data.dao.CoinsDao
import erik.strinnholm.rpg_inventory_app.data.model.Item
import erik.strinnholm.rpg_inventory_app.data.dao.ItemDao


@Database(entities = [Item::class, Character::class, Coins::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun characterDao(): CharacterDao
    abstract fun coinsDao(): CoinsDao

    companion object {
        @Volatile
        //private var INSTANCE: AppDatabase? = null
        private var instances: MutableMap<String, AppDatabase> = mutableMapOf()

        fun getDatabase(context: Context, dbName: String, slot: Int): AppDatabase {
            val fileName = "$dbName$slot"
            //return INSTANCE ?: synchronized(this) {
            return instances[fileName] ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    fileName
                ).build()
                instances[fileName] = instance
                //INSTANCE = instance
                instance
            }
        }
    }
}
