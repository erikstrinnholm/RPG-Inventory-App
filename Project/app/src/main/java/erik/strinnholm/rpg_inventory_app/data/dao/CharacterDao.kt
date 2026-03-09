package erik.strinnholm.rpg_inventory_app.data.dao
import erik.strinnholm.rpg_inventory_app.data.model.Character
import androidx.room.*

/**
 * Simple interface between the database and the character dataclass.
 */
@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCharacter(character: Character)

    @Query("SELECT * FROM characters LIMIT 1")
    suspend fun getCharacter(): Character?

    @Query("DELETE FROM characters")
    suspend fun deleteCharacter()
}