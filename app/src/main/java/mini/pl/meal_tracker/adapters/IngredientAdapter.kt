package mini.pl.meal_tracker.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import mini.pl.meal_tracker.R


class IngredientAdapter(var ingredients: MutableList<String>, val context: Context) : RecyclerView.Adapter<IngredientAdapter.IngredientHolder>() {
    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): IngredientHolder {
        return IngredientHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_edit, parent, false))
    }

    override fun onBindViewHolder(holder: IngredientHolder?, position: Int) {
        holder?.bindIngredient(ingredients[position])
    }



    fun addItem(ingredient: String) {
        ingredients.add(ingredient)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        ingredients.removeAt(position)
        notifyDataSetChanged()
    }

    inner class IngredientHolder(v: View): RecyclerView.ViewHolder(v) {
        val ingredientText = v.findViewById<TextView>(R.id.ingredientItemId)

        fun bindIngredient(ingredient: String) {
            ingredientText.setText(ingredient, TextView.BufferType.EDITABLE)
        }
    }

}