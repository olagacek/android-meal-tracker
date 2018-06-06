package mini.pl.meal_tracker.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.data.ShopItem

class ShopItemAdapter(var items: MutableList<ShopItem> = ArrayList()) : RecyclerView.Adapter<ShopItemAdapter.ShopItemHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ShopItemHolder?, position: Int) {
        holder?.bindItem(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ShopItemHolder {
        val context = parent?.context
        val view = LayoutInflater.from(context)?.inflate(R.layout.shopelement, parent, false)
        return ShopItemHolder(view!!)
    }

    fun replaceIngredients(items: MutableList<ShopItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ShopItemHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val descriptionView = v.findViewById<TextView>(R.id.item_description)
        private val checkBox = v.findViewById<CheckBox>(R.id.item_completed)

        fun bindItem(shopItem: ShopItem) {
            descriptionView?.text = shopItem.description
            checkBox?.isChecked = shopItem.isChecked

            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                items[adapterPosition].isChecked = isChecked
            }
        }
    }
}