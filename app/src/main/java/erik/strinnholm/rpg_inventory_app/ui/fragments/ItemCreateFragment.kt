package erik.strinnholm.rpg_inventory_app.ui.fragments

import erik.strinnholm.rpg_inventory_app.data.model.Item
import erik.strinnholm.rpg_inventory_app.viewmodel.ItemViewModel
import erik.strinnholm.rpg_inventory_app.ui.inventory.CharacterInventoryActivity
import erik.strinnholm.rpg_inventory_app.databinding.FragmentItemCreateBinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels



/** Fragment where the user can create a new Item entry in the database. */
class ItemCreateFragment : Fragment() {
    private var _binding: FragmentItemCreateBinding? = null
    private val binding get() = _binding!!

    // ViewModel to interact with the Room database
    private val itemViewModel: ItemViewModel by activityViewModels()

    /** Called at start */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemCreateBinding.inflate(inflater, container, false)
        setupCreateItemButton()
        setupCloseButton()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as? CharacterInventoryActivity)
            ?.setFragmentContainerVisibility(View.GONE)
    }

    /** Sets up the "Create Item" button with validation. */
    private fun setupCreateItemButton() {
        binding.buttonCreateItem.setOnClickListener {
            val itemName = binding.createItemName.text.toString()
            val itemDescription = binding.createItemDescription.text.toString()
            val itemWeightString = binding.createItemWeight.text.toString()

            // Validation
            if (itemName.isEmpty() || itemWeightString.isEmpty()) {
                Toast.makeText(context, "Please enter item name and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemWeight = itemWeightString.toDoubleOrNull()
            if (itemWeight == null) {
                Toast.makeText(context, "Invalid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newItem = Item(
                name = itemName,
                description = itemDescription,
                weight = itemWeight,
                quantity = 1
            )
            itemViewModel.insert(newItem)
            Toast.makeText(context, "Item Created: $itemName", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }


    /** Sets up the "Close" button to dismiss the fragment. */
    private fun setupCloseButton() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
