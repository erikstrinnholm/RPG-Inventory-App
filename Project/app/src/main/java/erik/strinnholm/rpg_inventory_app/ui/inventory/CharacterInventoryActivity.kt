package erik.strinnholm.rpg_inventory_app.ui.inventory

import erik.strinnholm.rpg_inventory_app.R
import erik.strinnholm.rpg_inventory_app.ui.adapters.ItemAdapter
import erik.strinnholm.rpg_inventory_app.ui.adapters.ItemAdapterSpacing
import erik.strinnholm.rpg_inventory_app.viewmodel.ItemViewModel
import erik.strinnholm.rpg_inventory_app.ui.fragments.ItemCreateFragment
import erik.strinnholm.rpg_inventory_app.ui.fragments.AboutFragment
import erik.strinnholm.rpg_inventory_app.databinding.ActivityCharacterInventoryBinding


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager


/**
 * Here the user can view the items in their inventory.
 * They can enter a select mode, by long clicking an
 * item in the ItemAdapter, there they can exit select mode, clear selection, select all items or delete selected items.
 *
 * They can sort the items shown in the ItemAdapter by clicking the sort buttons in the header.
 *
 * They can use the search bar in the toolbar to filter the items shown in the ItemAdapter.
 * They can click the (+) in the toolbar to start up a fragment to create items. (ItemCreateFragment)
 * (The user is sent here from the CharacterOverviewActivity)
 */
class CharacterInventoryActivity : AppCompatActivity(), ItemAdapter.OnSelectedItemsListener {
    private lateinit var itemViewModel: ItemViewModel
    private var databaseName: String = ""
    private var saveSlot: Int = -1
    private var isSelectMode = false
    private lateinit var adapter: ItemAdapter
    private lateinit var searchMenuItem: MenuItem
    private var _binding: ActivityCharacterInventoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCharacterInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        itemViewModel = ViewModelProvider(this)[ItemViewModel::class.java]

        // Fetch Intent data
        intent?.let {
            databaseName = it.getStringExtra("database_name") ?: ""
            saveSlot = it.getIntExtra("save_slot", -1)
        }

        if (databaseName.isNotEmpty() && saveSlot != -1) {
            itemViewModel.loadDatabase(databaseName, saveSlot)
        }

        // Setup UI
        initialVisibility()
        setUpToolBar()
        setupHeaderBar()
        setUpRecyclerView()
        setUpFooterBar1()
        setUpFooterBar2()
        setUpFooterBar3()
        setUpViewModelObserver()

        // Restore fragment after rotation
        if (savedInstanceState != null) {
            val aboutFragment = supportFragmentManager.findFragmentByTag("ABOUT_FRAGMENT")
            val createFragment = supportFragmentManager.findFragmentByTag("CREATE_FRAGMENT")
            if (aboutFragment != null || createFragment != null) {
                binding.fragmentContainer.visibility = View.VISIBLE
                supportActionBar?.hide()
            }
        }
    }


    /**
     * Sets up listeners to update the ItemAdapter (recyclerView) when the itemViewModels item list changes.
     * It also set up a listener to update the coin-fields when the itemViewModels coins data changes.
     */
    private fun setUpViewModelObserver() {
        itemViewModel.allItems?.observe(this) { items ->
            items?.let {
                adapter.setItems(it)
                adapter.originalItems = it.toMutableList()
            }
        }

        itemViewModel.coins.observe(this) { coins ->
            coins?.let {
                binding.characterInventoryCopperCount.text = it.copper.toString()
                binding.characterInventorySilverCount.text = it.silver.toString()
                binding.characterInventoryGoldCount.text = it.gold.toString()
                binding.characterInventoryPlatinumCount.text = it.platinum.toString()
            }
        }
    }


    /**
     * Sets up the ItemAdapter RecyclerView
     * The ItemAdapter is initiated with 2 functions, and one listener.
     * It also adds some spacing between the RecyclerView Items with the ItemAdapterSpacing class.
     */
    private fun setUpRecyclerView() {
        adapter = ItemAdapter(
            onItemClick = { position -> onItemClick(position) },
            onItemLongClick = { position -> onItemLongClicked(position) },
            selectionListener = this
        )

        binding.itemListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemListRecyclerView.adapter = adapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        binding.itemListRecyclerView.addItemDecoration(ItemAdapterSpacing(spacingInPixels))
    }

    /**
     * This function is passed to the ItemAdapter, to activate when they click an ItemAdapter Recycler Item.
     * Not yet implemented, the idea is to open a fragment where the user can inspect the selected Item.
     */
    private fun onItemClick(position: Int) {
        if (!isSelectMode) {
            Toast.makeText(this, "Item Clicked - Not yet Implemented", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This function is passed to the ItemAdapter, to activate when they long-click an ItemAdapter Recycler Item.
     * When activated enters Select Mode (see enterSelectMode function)
     */

    private fun onItemLongClicked(position: Int) {
        if (!isSelectMode) enterSelectMode()
    }


    /** Sets the toolbar and adds a back button to it. */
    private fun setUpToolBar() {
        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val backIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.back_icon)
        backIcon?.setTint(ContextCompat.getColor(this, R.color.back_button))
        binding.topBar.navigationIcon = backIcon

        binding.topBar.setNavigationOnClickListener { finish() }
    }

    /**
     * Inflates the toolbar with the custom menu.
     * It also adds a search button/field functionality to query the itemAdapter to only show the items
     * matching the sub-string.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_character_inventory, menu)

        searchMenuItem = menu?.findItem(R.id.action_search)!!
        val searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                searchView.requestFocus()
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchView.setQuery("", false)
                searchView.clearFocus()
                adapter.filterItems("")
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { adapter.filterItems(it) }
                return true
            }
        })
        return true
    }

    /**
     * Sets the functionality for the toolbar menu items.
     *  action_new_item: Starts the ItemCreateFragment. Where the user can create a new Item.
     *  action_connect: (Not implemented)
     *  action_about: Starts the AboutFragment. Where the user can view credits/user instructions.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_item -> {
                val fragment = ItemCreateFragment()
                binding.fragmentContainer.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, "CREATE_FRAGMENT")
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.hide()
                true
            }
            R.id.action_connect -> true // TODO
            R.id.action_about -> {
                val fragment = AboutFragment()
                binding.fragmentContainer.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, "ABOUT_FRAGMENT")
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.hide()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    /**
     * Sets up the header bar.
     * It holds 3 buttons, that calls the ItemAdapter to sort the itemList by quantity, name or weight.
     */
    private fun setupHeaderBar() {
        binding.inventoryHeaderQuantity.setOnClickListener { adapter.sortByQuantity() }
        binding.inventoryHeaderName.setOnClickListener { adapter.sortByName() }
        binding.inventoryHeaderTotalWeight.setOnClickListener { adapter.sortByWeight() }
    }

    /**
     * Sets up the coins footer bar.
     * (The coins functionality is not yet implemented)
     */
    private fun setUpFooterBar1() {
        binding.inventoryFooter1Coins.setOnClickListener {
            Toast.makeText(this, "Coins Clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sets up the selection footer bar.
     * (Is shown when the user is in select mode)
     * It holds 3 buttons, that can exitSelectMode, clear item selection, or select all items in the ItemAdapter.
     */
    private fun setUpFooterBar2() {
        binding.buttonSelectExit.setOnClickListener { exitSelectMode() }
        binding.buttonSelectClear.setOnClickListener { adapter.clearSelection() }
        binding.buttonSelectAll.setOnClickListener { adapter.selectAll() }
    }

    /**
     * Sets up the selection footer bar.
     * (Is shown when the user is in select mode)
     * It holds 2 buttons, the send button (is yet to be implemented) and delete button that calls
     * the itemViewModel to delete the selected items.
     */
    private fun setUpFooterBar3() {
        binding.buttonSend.setOnClickListener {
            Toast.makeText(this, "SEND Clicked - NOT IMPLEMENTED!", Toast.LENGTH_SHORT).show()
        }
        binding.buttonDelete.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            if (selectedItems.isNotEmpty()) {
                itemViewModel.deleteSelectedItems(selectedItems.toMutableMap())
                adapter.clearSelection()
            }
        }
    }

    /**
     * Enters select mode.
     * Updates the UI visibility to show some more buttons related to selecting items.
     * Calls ItemAdapted to enable select mode.
     */
    private fun enterSelectMode() {
        isSelectMode = true
        binding.inventoryHeaderFiller.visibility = View.VISIBLE
        binding.inventoryFooter1Coins.visibility = View.GONE
        binding.inventoryFooter2Select.visibility = View.GONE       //TODO IMPLEMENT (HIDDEN NOW)
        binding.inventoryFooter3Options.visibility = View.VISIBLE
        adapter.enableSelectMode(true)
    }

    /**
     * Exits select mode.
     * Updates the UI visibility with initialVisibility()
     * Clears the ItemAdapter, so that no items are currently selected.
     */
    private fun exitSelectMode() {
        isSelectMode = false
        initialVisibility()
        adapter.clearSelection()
        adapter.enableSelectMode(false)
    }

    /**
     * Hides some of the UI that should only be visible if the user is in select mode.
     */
    private fun initialVisibility() {
        binding.inventoryHeaderFiller.visibility = View.GONE
        binding.inventoryFooter1Coins.visibility = View.GONE    //TODO IMPLEMENT (HIDDEN NOW)
        binding.inventoryFooter2Select.visibility = View.GONE
        binding.inventoryFooter3Options.visibility = View.GONE
    }
    /** Shows/Hides the fragment container */
    fun setFragmentContainerVisibility(visibility: Int) {
        binding.fragmentContainer.visibility = visibility
    }


    /** Listener that checks the ItemAdapter, if the user has selected Items, and if so enable/disable the send/delete buttons */
    override fun onSelectionCountChanged(hasSelection: Boolean) {
        binding.buttonSend.isEnabled = false     //TODO IMPLEMENT
        binding.buttonDelete.isEnabled = hasSelection
    }

    /** On Destroy */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
