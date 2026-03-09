package erik.strinnholm.rpg_inventory_app.ui.character
import erik.strinnholm.rpg_inventory_app.data.database.AppDatabase
import erik.strinnholm.rpg_inventory_app.data.model.Character
import erik.strinnholm.rpg_inventory_app.databinding.ActivityCharacterSelectBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterSelectBinding
    private val databaseName = "app_database_"
    private var deleteMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadSaveSlots()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        // Toggle delete buttons
        binding.toggleDeleteButton.setOnClickListener { toggleDeleteMode() }

        // Save slot click
        val saveButtons = listOf(
            binding.saveSlot1, binding.saveSlot2, binding.saveSlot3,
            binding.saveSlot4, binding.saveSlot5
        )
        saveButtons.forEachIndexed { index, button ->
            button.setOnClickListener { loadSlot(index + 1) }
        }

        // Delete buttons
        val deleteButtons = listOf(
            binding.deleteSaveSlot1, binding.deleteSaveSlot2, binding.deleteSaveSlot3,
            binding.deleteSaveSlot4, binding.deleteSaveSlot5
        )
        deleteButtons.forEachIndexed { index, button ->
            button.setOnClickListener { deleteSlot(index + 1) }
            button.visibility = View.GONE // hide by default
        }
    }


    /** Show or hide delete buttons */
    private fun toggleDeleteMode() {
        val deleteButtons = listOf(
            binding.deleteSaveSlot1, binding.deleteSaveSlot2, binding.deleteSaveSlot3,
            binding.deleteSaveSlot4, binding.deleteSaveSlot5
        )
        deleteMode = !deleteMode
        deleteButtons.forEach { it.visibility = if (deleteMode) View.VISIBLE else View.GONE }
    }

    /** Load a save slot */
    private fun loadSlot(slot: Int) {
        val dbName = "$databaseName$slot"
        val dbFile = getDatabasePath(dbName)

        val targetActivity = if (dbFile.exists()) {
            CharacterOverviewActivity::class.java
        } else {
            CharacterCreateActivity::class.java
        }

        startActivity(Intent(this, targetActivity).apply {
            putExtra("database_name", databaseName)
            putExtra("save_slot", slot)
        })
    }

    /** Delete a save slot */
    private fun deleteSlot(slot: Int) {
        val dbName = "$databaseName$slot"
        val dbFile = getDatabasePath(dbName)

        if (dbFile.exists()) {
            dbFile.delete()
            getSaveButton(slot).text = "New Character"
            Toast.makeText(this, "Slot $slot deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No data found in slot $slot", Toast.LENGTH_SHORT).show()
        }
    }

    /** Update all save slot buttons */
    private fun loadSaveSlots() {
        val saveButtons = listOf(
            binding.saveSlot1, binding.saveSlot2, binding.saveSlot3,
            binding.saveSlot4, binding.saveSlot5
        )

        lifecycleScope.launch {
            for (slot in 1..5) {
                val dbName = "$databaseName$slot"
                val dbFile = getDatabasePath(dbName)
                if (!dbFile.exists()) continue

                val character = loadCharacterFromDatabase(databaseName, slot) ?: continue
                saveButtons[slot - 1].text = character.name
            }
        }
    }

    /** Helper to get a save button by slot index */
    private fun getSaveButton(slot: Int) = when (slot) {
        1 -> binding.saveSlot1
        2 -> binding.saveSlot2
        3 -> binding.saveSlot3
        4 -> binding.saveSlot4
        5 -> binding.saveSlot5
        else -> error("Invalid slot $slot")
    }

    /** Retrieve character from database */
    private suspend fun loadCharacterFromDatabase(name: String, slot: Int): Character? =
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext, name, slot)
            db.characterDao().getCharacter()
        }

}