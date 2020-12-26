package com.omega_r.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omegar.libs.omegalaunchers.FragmentLauncher

private const val KEY_PREFIX_FRAGMENT = "f#"

@Suppress("unused")
class OmegaViewPager2Adapter: FragmentStateAdapter, OmegaListableAdapter<FragmentLauncher>, Iterable<Fragment?> {

    private val fragmentManager: FragmentManager

    override var list: List<FragmentLauncher> = emptyList()
        set(value) {
            if (field != value) {
                field = value.toList()
                notifyDataSetChanged()
            }
        }

    constructor(fragmentActivity: FragmentActivity, vararg launchers: FragmentLauncher) : super(fragmentActivity) {
        fragmentManager = fragmentActivity.supportFragmentManager
        list = launchers.toList()
    }

    constructor(fragment: Fragment, vararg launchers: FragmentLauncher) : super(fragment) {
        fragmentManager = fragment.childFragmentManager
        list = launchers.toList()
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, vararg launchers: FragmentLauncher) : super(
        fragmentManager,
        lifecycle
    ) {
        this.fragmentManager = fragmentManager
        list = launchers.toList()
    }

    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position].createFragment()

    override fun iterator(): Iterator<Fragment?> = IteratorImpl()

    fun getCurrentFragment(position: Int): Fragment? {
        val itemId = getItemId(position)
        return fragmentManager.findFragmentByTag(KEY_PREFIX_FRAGMENT + itemId)
    }

    private open inner class IteratorImpl : Iterator<Fragment?> {
        /** the index of the item that will be returned on the next call to [next]`()` */
        protected var index = 0

        override fun hasNext(): Boolean = index < list.size

        override fun next(): Fragment? {
            if (!hasNext()) throw NoSuchElementException()
            return getCurrentFragment(index++)
        }
    }

}