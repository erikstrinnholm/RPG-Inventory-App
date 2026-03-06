package erik.strinnholm.rpg_inventory_app.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val weight: Double,
    val quantity: Int,
    val description: String
)