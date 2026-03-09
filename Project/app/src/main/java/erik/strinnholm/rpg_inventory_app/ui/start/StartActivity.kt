package erik.strinnholm.rpg_inventory_app.ui.start
import erik.strinnholm.rpg_inventory_app.ui.fragments.AboutFragment
import erik.strinnholm.rpg_inventory_app.ui.character.CharacterSelectionActivity
import erik.strinnholm.rpg_inventory_app.databinding.ActivityStartBinding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/**
 * Start Activity
 * Here the user can select weather they want to be a player (client) or dm (host)
 */
class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    companion object { private const val ABOUT_FRAGMENT_TAG = "ABOUT_FRAGMENT" }


    /** Called on activity start */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        restoreFragmentIfNeeded(savedInstanceState)
    }
    private fun setupListeners() = with(binding) {
        startDm.setOnClickListener { navigateToDm() }
        startPlayer.setOnClickListener { navigateToCharacterSelection() }
        startAbout.setOnClickListener { showAboutFragment() }
    }
    private fun navigateToDm() {
        // TODO: Implement DM flow
        Toast.makeText(this, "NOT YET IMPLEMENTED", Toast.LENGTH_SHORT).show()
    }
    private fun navigateToCharacterSelection() {
        startActivity(Intent(this, CharacterSelectionActivity::class.java))
    }
    private fun showAboutFragment() {
        binding.startFragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(
                binding.startFragmentContainer.id,
                AboutFragment(),
                ABOUT_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()
        supportActionBar?.hide()
    }
    private fun restoreFragmentIfNeeded(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        val aboutFragment = supportFragmentManager.findFragmentByTag(ABOUT_FRAGMENT_TAG)
        if (aboutFragment != null) {
            binding.startFragmentContainer.visibility = View.VISIBLE
            supportActionBar?.hide()
        }
    }
}
