package com.omega_r.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.INVALID_POSITION
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.annotation.LayoutRes
import com.omega_r.libs.omegatypes.Text

/**
 * Created by Anton Knyazev on 2019-07-02.
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class OmegaSpinnerAdapter<M>(
    context: Context,
    @LayoutRes res: Int = android.R.layout.simple_spinner_item,
    list: List<M> = emptyList()
) : ArrayAdapter<CharSequence>(context, res, emptyList()), OmegaListableAdapter<M>,
    SpinnerAdapter {

    override var list: List<M> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var nonSelectedItem: M? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var hasStableId: Boolean? = null
        get() {
            if (field == null) {
                field = (list.firstOrNull() is OmegaIdentifiable<*>)
            }
            return field
        }

    private var viewPosition: Int = -1

    abstract fun getItemName(item: M, isDropDown: Boolean): CharSequence

    override fun getItem(position: Int): CharSequence? {
        val isDropDown = if (position == viewPosition) {
            viewPosition = -1
            false
        } else true
        return when {
            nonSelectedItem == null -> getItemName(list[position], isDropDown)
            position == 0 -> getItemName(nonSelectedItem!!, isDropDown)
            else -> getItemName(list[position - 1], isDropDown)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        viewPosition = position
        return super.getView(position, convertView, parent)
    }

    override fun getItemId(position: Int): Long {
        val newPosition = position + (if (nonSelectedItem == null) 0 else 1)
        return when (val item = list.getOrNull(newPosition)) {
            is OmegaIdentifiable<*> -> item.idAsLong
            else -> super.getItemId(position)
        }
    }

    fun setHasStableId(hasStableId: Boolean) {
        this.hasStableId = hasStableId
    }

    override fun hasStableIds(): Boolean {
        return hasStableId!!
    }

    override fun getCount(): Int = list.size + (if (nonSelectedItem == null) 0 else 1)

    fun setSelection(spinner: Spinner, item: M?) {
        if (item == null) {
            setSelection(spinner, 0)
            return
        }
        for (i in list.indices) {
            val position = if (nonSelectedItem == null) i else i + 1
            val listItem = list[i]
            if (listItem == item ||
                (hasStableId!!
                        && item is OmegaIdentifiable<*>
                        && listItem is OmegaIdentifiable<*>
                        && item.id == listItem.id)
            ) {
                setSelection(spinner, position)
                return
            }
        }
    }

    private fun setSelection(spinner: Spinner, position: Int) {
        if (spinner.selectedItemPosition == position) return
        spinner.setSelection(position)
    }

    fun getSelection(spinner: Spinner): M? {
        val position = getSelectionPosition(spinner)
        return if (position >= 0) list[position] else null
    }

    fun getSelectionPosition(spinner: Spinner): Int {
        val position = spinner.selectedItemPosition
        nonSelectedItem?.let {
            if (position <= 0) return INVALID_POSITION
            return position - 1
        }
        return position
    }

    class TextAdapter(
        context: Context,
        res: Int = android.R.layout.simple_spinner_item,
        list: List<Text> = emptyList()
    ) :
        OmegaSpinnerAdapter<Text>(context, res, list) {

        override fun getItemName(
            item: Text,
            isDropDown: Boolean
        ): CharSequence =
            item.getCharSequence(context) ?: ""

    }

    class StringAdapter(
        context: Context,
        res: Int = android.R.layout.simple_spinner_item,
        list: List<String> = emptyList()
    ) :
        OmegaSpinnerAdapter<String>(context, res, list) {

        override fun getItemName(item: String, isDropDown: Boolean): CharSequence = item
    }

    class CustomAdapter<M>(
        context: Context,
        res: Int = android.R.layout.simple_spinner_item,
        private val converter: (Context, item: M, isDropDown: Boolean) -> CharSequence,
        list: List<M> = emptyList()
    ) :
        OmegaSpinnerAdapter<M>(context, res, list) {

        override fun getItemName(item: M, isDropDown: Boolean): CharSequence =
            converter(context, item, isDropDown)
    }


}