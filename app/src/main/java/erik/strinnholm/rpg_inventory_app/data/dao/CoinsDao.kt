package erik.strinnholm.rpg_inventory_app.data.dao
import erik.strinnholm.rpg_inventory_app.data.model.Coins
import androidx.room.*

/**
 * Simple interface between the database and the coins dataclass.
 */
@Dao
interface CoinsDao {
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCoins(coins: Coins)

    @Query("SELECT * FROM coins LIMIT 1")
    suspend fun getCoins(): Coins?

    @Query("DELETE FROM coins")
    suspend fun deleteCoins()
}