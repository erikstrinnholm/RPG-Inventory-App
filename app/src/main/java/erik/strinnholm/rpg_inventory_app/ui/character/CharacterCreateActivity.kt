package erik.strinnholm.rpg_inventory_app.ui.character

import erik.strinnholm.rpg_inventory_app.databinding.ActivityCharacterCreateBinding
import erik.strinnholm.rpg_inventory_app.data.database.AppDatabase
import erik.strinnholm.rpg_inventory_app.data.model.Character
import erik.strinnholm.rpg_inventory_app.data.model.Coins

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch



/**
 * Here the user can enter information and create a character data entry into a database.
 * The user is sent here from CharacterSelectActivity, and will be passed to CharacterOverviewActivity.
 */
class CharacterCreateActivity : AppCompatActivity() {

    private lateinit var databaseName: String
    private var saveSlot: Int = -1

    // ViewBinding reference
    private lateinit var binding: ActivityCharacterCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityCharacterCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get Database info from Intent
        databaseName = intent.getStringExtra("database_name") ?: ""
        saveSlot = intent.getIntExtra("save_slot", -1)

        // Initially disable create button
        binding.createCharacterButton.isEnabled = false

        // Set up text watchers
        binding.createCharacterName.addTextChangedListener(textWatcher)
        binding.createCharacterStrength.addTextChangedListener(textWatcher)
        binding.createCharacterSpeed.addTextChangedListener(textWatcher)

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Create button listener
        binding.createCharacterButton.setOnClickListener {
            saveCharacterData()
        }
    }
    /**
     * Saves the data filled in from the EditText fields into a database.
     * It creates a new character and coins entry, then transitions to CharacterOverviewActivity.
     */
    private fun saveCharacterData() {
        val name = binding.createCharacterName.text.toString()
        val strength = binding.createCharacterStrength.text.toString().toInt()
        val speed = binding.createCharacterSpeed.text.toString().toInt()
        val campaign = binding.createCharacterCampaign.text.toString()

        val character = Character(name = name, strength = strength, speed = speed, campaign = campaign)
        val emptyCoins = Coins(copper = 0, silver = 0, gold = 0, platinum = 0)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@CharacterCreateActivity, databaseName, saveSlot)
            db.characterDao().updateCharacter(character)
            db.coinsDao().updateCoins(emptyCoins)

            // Navigate to CharacterOverviewActivity
            val intent = Intent(this@CharacterCreateActivity, CharacterOverviewActivity::class.java)
            intent.putExtra("database_name", databaseName)
            intent.putExtra("save_slot", saveSlot)
            startActivity(intent)
            finish()
        }
    }
    /**
     * Checks as the text fields are updated
     * Only enables the create character button if name, strength, and speed are not empty.
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.createCharacterButton.isEnabled =
                binding.createCharacterName.text.isNotBlank() &&
                        binding.createCharacterStrength.text.isNotBlank() &&
                        binding.createCharacterSpeed.text.isNotBlank()
        }
        override fun afterTextChanged(s: Editable?) {}
    }
}