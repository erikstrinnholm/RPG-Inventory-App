package erik.strinnholm.rpg_inventory_app.ui.adapters
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/** It adds spacing below each elements in a RecyclerView. */
class ItemAdapterSpacing(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spacing
    }
}
