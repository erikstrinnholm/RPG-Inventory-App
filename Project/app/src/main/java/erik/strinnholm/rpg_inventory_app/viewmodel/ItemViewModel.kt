package erik.strinnholm.rpg_inventory_app.viewmodel
import erik.strinnholm.rpg_inventory_app.data.database.AppDatabase
import erik.strinnholm.rpg_inventory_app.data.model.Item
import erik.strinnholm.rpg_inventory_app.data.dao.ItemDao
import erik.strinnholm.rpg_inventory_app.data.model.Character
import erik.strinnholm.rpg_inventory_app.data.dao.CharacterDao
import erik.strinnholm.rpg_inventory_app.data.model.Coins
import erik.strinnholm.rpg_inventory_app.data.dao.CoinsDao
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    //Database connections (Dao)
    private var itemDao: ItemDao? = null
    private var characterDao: CharacterDao? = null
    private var coinsDao: CoinsDao? = null

    var allItems: LiveData<List<Item>>? = null

    private var _character: MutableLiveData<Character> = MutableLiveData()
    val character: LiveData<Character> get() = _character
    private var _coins: MutableLiveData<Coins> = MutableLiveData()
    val coins: LiveData<Coins> get() = _coins



    fun loadDatabase(databaseName: String, slot: Int) {
        val db = AppDatabase.getDatabase(getApplication(), databaseName, slot)
        itemDao = db.itemDao()
        characterDao = db.characterDao()
        coinsDao = db.coinsDao()

        allItems = itemDao?.getAllItems()
        viewModelScope.launch {
            _character.value = characterDao?.getCharacter()
            _coins.value = coinsDao?.getCoins()
        }
    }

    /** Updates the database with the character */
    fun updateCharacter(character: Character) {
        _character.value = character
        //update the database
        viewModelScope.launch {
            characterDao?.updateCharacter(character)
        }
    }

    /** Updates the database with the coins */
    fun updateCoins(coins: Coins) {
        _coins.value = coins
        //update the database
        viewModelScope.launch {
            coinsDao?.updateCoins(coins)
        }
    }

    /** Deletes an item from the database. */
    fun delete(item: Item) {
        viewModelScope.launch {
            itemDao?.delete(item)
        }
    }
    /** Inserts an item to the database. */
    fun insert(item: Item) {
        viewModelScope.launch {
            itemDao?.insert(item)
        }
    }

    /**
     * Deletes a set of items by a specified quantity from the database.
     * items follow <id, quantity>
     */
    fun deleteSelectedItems(items: MutableMap<Int, Int>) {
        viewModelScope.launch {
            for ((itemId, quantity) in items) {
                val item = itemDao?.getItemById(itemId)
                if (item != null) {
                    val newQuantity = item.quantity - quantity
                    if (newQuantity > 0) {
                        // Update the item quantity
                        val updatedItem = item.copy(quantity = newQuantity)
                        itemDao?.update(updatedItem)
                    } else {
                        // If the quantity goes to zero or less, delete the item
                        itemDao?.delete(item)
                    }
                }
            }
        }
    }

}