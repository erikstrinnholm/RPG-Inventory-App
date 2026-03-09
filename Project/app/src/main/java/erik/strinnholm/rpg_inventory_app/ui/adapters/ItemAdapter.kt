package erik.strinnholm.rpg_inventory_app.ui.adapters
import erik.strinnholm.rpg_inventory_app.data.model.Item
import erik.strinnholm.rpg_inventory_app.databinding.InventoryItemBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemAdapter using ViewBinding
 * Handles a list of items and selection/quantity logic.
 */
class ItemAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: ((Int) -> Unit)? = null,
    private val selectionListener: OnSelectedItemsListener) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    interface OnSelectedItemsListener { fun onSelectionCountChanged(hasSelection: Boolean) }

    private val selectedItems = mutableMapOf<Int, Int>() // position -> selected quantity
    private var isSelectMode = false
    private val items = mutableListOf<Item>()
    var originalItems = mutableListOf<Item>()
    private var isQuantityAscending = true
    private var isNameAscending = true
    private var isWeightAscending = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = InventoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    @SuppressLint("SetTextI18n")
    inner class ItemViewHolder(private val binding: InventoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, position: Int) {
            // Set values
            binding.inventoryItemQuantity.text = "${item.quantity}x"
            binding.inventoryItemName.text = item.name
            binding.inventoryItemTotalWeight.text = "${item.weight * item.quantity} lb."

            // Show/hide stepper based on select mode
            binding.inventoryItemStepper.visibility = if (isSelectMode) View.VISIBLE else View.GONE
            binding.stepperQuantityText.text = "${selectedItems[position] ?: 0}/${item.quantity}"

            // Highlight selected items
            itemView.isSelected = selectedItems.contains(position)

            // Item Click
            itemView.setOnClickListener {
                if (!isSelectMode) onItemClick(position)
            }

            // Long click for selection
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(position)
                incrementSelectedItem(position)
                true
            }

            // Stepper buttons
            binding.stepperDecrementButton.setOnClickListener {
                decrementSelectedItem(position)
            }
            binding.stepperIncrementButton.setOnClickListener {
                incrementSelectedItem(position)
            }
        }
    }

    /** ========== SELECTION HELPERS ========== */
    private fun incrementSelectedItem(position: Int) {
        val item = items[position]
        val current = selectedItems[position] ?: 0
        if (current < item.quantity) {
            selectedItems[position] = current + 1
            notifyItemChanged(position)
            notifySelectionCountChanged()
        }
    }
    private fun decrementSelectedItem(position: Int) {
        val current = selectedItems[position] ?: 0
        when {
            current <= 1 -> selectedItems.remove(position)
            else -> selectedItems[position] = current - 1
        }
        notifyItemChanged(position)
        notifySelectionCountChanged()
    }
    private fun notifySelectionCountChanged() {
        selectionListener.onSelectionCountChanged(selectedItems.isNotEmpty())
    }
    fun getSelectedItems(): Map<Int, Int> {
        val selectedMap = mutableMapOf<Int, Int>()
        for ((position, quantity) in selectedItems) {
            if (position in items.indices) {
                selectedMap[items[position].id] = quantity
            }
        }
        return selectedMap
    }


    /** ========== DATA MANAGEMENT ========== */
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Item>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun enableSelectMode(enable: Boolean) {
        isSelectMode = enable
        notifySelectionCountChanged()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        selectedItems.clear()
        for (i in items.indices) {
            selectedItems[i] = items[i].quantity
        }
        notifySelectionCountChanged()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clearSelection() {
        selectedItems.clear()
        notifySelectionCountChanged()
        notifyDataSetChanged()
    }


    /** ========== SORTING ========== */
    @SuppressLint("NotifyDataSetChanged")
    fun sortByQuantity() = sortItemsBy({ it.quantity }, isQuantityAscending) { isQuantityAscending = !isQuantityAscending }
    @SuppressLint("NotifyDataSetChanged")
    fun sortByName() = sortItemsBy({ it.name }, isNameAscending) { isNameAscending = !isNameAscending }
    @SuppressLint("NotifyDataSetChanged")
    fun sortByWeight() = sortItemsBy({ it.weight }, isWeightAscending) { isWeightAscending = !isWeightAscending }
    private fun <T : Comparable<T>> sortItemsBy(selector: (Item) -> T, ascending: Boolean, onToggle: () -> Unit) {
        val oldPositions = items.mapIndexed { idx, item -> idx to item }.toMap().toMutableMap()
        if (ascending) items.sortBy(selector) else items.sortByDescending(selector)
        updateSelectedItems(oldPositions)
        onToggle()
        notifyDataSetChanged()
    }
    private fun updateSelectedItems(oldPositions: Map<Int, Item>) {
        val newSelected = mutableMapOf<Int, Int>()
        for ((pos, quantity) in selectedItems) {
            val item = oldPositions[pos] ?: continue
            val newPos = items.indexOf(item)
            if (newPos != -1) newSelected[newPos] = quantity
        }
        selectedItems.clear()
        selectedItems.putAll(newSelected)
    }


    /** ========== FILTERING ========== */
    fun filterItems(query: String) {
        if (query.isEmpty()) {
            setItems(originalItems)
            return
        }
        val filtered = originalItems.filter { it.name.contains(query, ignoreCase = true) }
        setItems(filtered)
    }




    //unused code (ignore) ============================================================
    /*
    fun addItem(item: Item) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
    fun addItems(newItems: List<Item>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun clearItems() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }
    fun updateItem(position: Int, newItem: Item) {
        if (position in items.indices) {
            items[position] = newItem
            notifyItemChanged(position)
        }
    }
     */
}
