package erik.strinnholm.rpg_inventory_app.data.dao
import erik.strinnholm.rpg_inventory_app.data.model.Item
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Int): Item?

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items WHERE id IN (:itemIds)")
    suspend fun deleteItems(itemIds: List<Int>)

    @Query("UPDATE items SET quantity = quantity - :quantity WHERE id = :itemId AND quantity > 0")
    suspend fun updateItemQuantity(itemId: Int, quantity: Int)

    @Query("SELECT * FROM items")
    fun getAllItems(): LiveData<List<Item>>
}
