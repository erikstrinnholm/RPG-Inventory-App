package erik.strinnholm.rpg_inventory_app.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Simple data class to store a character.
 */
@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val strength: Int,
    val speed: Int,
    val campaign: String
)