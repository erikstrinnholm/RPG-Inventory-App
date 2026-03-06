package erik.strinnholm.rpg_inventory_app.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Simple data class to store coins.
 */
@Entity(tableName = "coins")
data class Coins(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val copper: Int,
    val silver: Int,
    val gold: Int,
    var platinum: Int
)
