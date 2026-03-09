package erik.strinnholm.rpg_inventory_app.ui.character

import erik.strinnholm.rpg_inventory_app.R
import erik.strinnholm.rpg_inventory_app.data.model.Coins
import erik.strinnholm.rpg_inventory_app.data.model.Item
import erik.strinnholm.rpg_inventory_app.ui.fragments.AboutFragment
import erik.strinnholm.rpg_inventory_app.ui.inventory.CharacterInventoryActivity
import erik.strinnholm.rpg_inventory_app.viewmodel.ItemViewModel
import erik.strinnholm.rpg_inventory_app.databinding.ActivityCharacterOverviewBinding

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

/**
 * CharacterOverview Activity:
 * Shows character stats and carrying capacity.
 * User can open the inventory or view the About screen.
 */
class CharacterOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterOverviewBinding
    private lateinit var itemViewModel: ItemViewModel

    private lateinit var databaseName: String
    private var saveSlot: Int = -1

    companion object { private const val ABOUT_FRAGMENT_TAG = "ABOUT_FRAGMENT" }


    /** Called when activity start */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCharacterOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemViewModel = ViewModelProvider(this)[ItemViewModel::class.java]

        fetchIntentData()
        loadDatabase()

        setupToolbar()
        setupListeners()
        setupObservers()

        restoreFragmentIfNeeded(savedInstanceState)
    }

    /** Fetch database info from intent */
    private fun fetchIntentData() {
        intent?.let {
            databaseName = it.getStringExtra("database_name") ?: ""
            saveSlot = it.getIntExtra("save_slot", -1)
        }
    }

    /** Load the selected database */
    private fun loadDatabase() {
        if (databaseName.isNotEmpty() && saveSlot != -1) {
            itemViewModel.loadDatabase(databaseName, saveSlot)
        }
    }

    /** Setup toolbar and back button */
    private fun setupToolbar() {
        setSupportActionBar(binding.characterToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val backIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.back_icon)
        backIcon?.setTint(ContextCompat.getColor(this, R.color.back_button))

        binding.characterToolbar.navigationIcon = backIcon

        binding.characterToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /** Setup UI listeners */
    private fun setupListeners() = with(binding) {
        characterOverviewBackpack.setOnClickListener {
            navigateToInventory()
        }
    }

    /** Observe ViewModel data */
    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        itemViewModel.allItems?.observe(this) { items ->
            updateWeight(items, itemViewModel.coins.value)
        }
        itemViewModel.coins.observe(this) { coins ->
            updateWeight(itemViewModel.allItems?.value ?: emptyList(), coins)
        }
        itemViewModel.character.observe(this) { character ->
            character ?: return@observe
            binding.characterOverviewName.text = character.name
            binding.characterOverviewStrength.text = character.strength.toString()
            binding.characterOverviewSpeed.text = character.speed.toString()
            binding.characterOverviewLimit1Weight.text = "${character.strength * 5} lb"
            binding.characterOverviewLimit2Weight.text = "${character.strength * 10} lb"
            binding.characterOverviewLimit3Weight.text = "${character.strength * 15} lb"
        }
    }

    /** Toolbar menu */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_character_overview, menu)
        return true
    }

    /** Toolbar item handling */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_connect -> {
                // Future feature
                true
            }
            R.id.action_about -> {
                showAboutFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Open About fragment */
    private fun showAboutFragment() {
        binding.characterFragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(
                binding.characterFragmentContainer.id,
                AboutFragment(),
                ABOUT_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()

        supportActionBar?.hide()
    }

    /** Restore fragment after rotation */
    private fun restoreFragmentIfNeeded(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        val aboutFragment = supportFragmentManager.findFragmentByTag(ABOUT_FRAGMENT_TAG)
        if (aboutFragment != null) {
            binding.characterFragmentContainer.visibility = View.VISIBLE
            supportActionBar?.hide()
        }
    }


    /** Navigate to inventory screen */
    private fun navigateToInventory() {
        val intent = Intent(
            this,
            CharacterInventoryActivity::class.java
        ).apply {
            putExtra("database_name", databaseName)
            putExtra("save_slot", saveSlot)
        }
        startActivity(intent)
    }

    /** Update weight UI */
    @SuppressLint("DefaultLocale")
    private fun updateWeight(items: List<Item>, coins: Coins?) {
        val weight = calculateWeight(items, coins)
        binding.characterOverviewTotalWeight.text = String.format("%.1f", weight)
    }

    /** Calculate total weight */
    private fun calculateWeight(items: List<Item>, coins: Coins?): Float {
        val itemsWeight = items.sumOf { it.weight }.toFloat()
        val coinWeight = coins?.let {
            val totalCoins = it.copper + it.silver + it.gold + it.platinum
            totalCoins / 50f
        } ?: 0f
        return itemsWeight + coinWeight
    }
}
