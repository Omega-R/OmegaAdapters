package com.omega_r.adapters

/**
 * Created by Anton Knyazev on 28.04.2019.
 */
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.omegar.libs.omegalaunchers.FragmentLauncher

/**
 * Created by Anton Knyazev on 27.04.2019.
 */
private const val KEY_LIST = "internalList"
private const val KEY_SUPER = "internalSuper"

@Suppress("unused")
class OmegaViewPagerAdapter(
    fm: FragmentManager,
    private val titleSetterBlock: ((Int) -> CharSequence?)? = null
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), Iterable<Fragment>,
    OmegaListableAdapter<FragmentLauncher> {

    override var list: List<FragmentLauncher> = emptyList()
        set(value) {
            if (field != value) {
                field = value.toList()
                notifyDataSetChanged()
            }
        }

    private lateinit var container: ViewGroup

    constructor(
        fm: FragmentManager,
        titleSetterBlock: ((Int) -> CharSequence?)? = null,
        vararg fragmentLauncher: FragmentLauncher
    ) : this(fm, titleSetterBlock) {
        list = fragmentLauncher.toList()
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        if (!this::container.isInitialized) {
            this.container = container
            // WORKAROUND: If container not initialized then getPageTitle return null.
            // This is force update pageTitle
            container.post {
                notifyDataSetChanged()
            }
        }
    }

    override fun getItem(position: Int) = list[position].createFragment()

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? {
        return titleSetterBlock?.invoke(position)
    }

    fun getCurrentFragment(position: Int) = instantiateItem(container, position) as Fragment

    override fun saveState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelableArray(KEY_LIST, list.toTypedArray())
        bundle.putParcelable(KEY_SUPER, super.saveState())
        return bundle
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state is Bundle) {
            state.classLoader = loader
            @Suppress("UNCHECKED_CAST")
            list = state.getParcelableArray(KEY_LIST)?.map { it as FragmentLauncher } ?: emptyList()
            super.restoreState(state.getParcelable(KEY_SUPER), loader)
        } else {
            super.restoreState(state, loader)
        }
    }

    override fun iterator(): Iterator<Fragment> = IteratorImpl()

    private open inner class IteratorImpl : Iterator<Fragment> {
        /** the index of the item that will be returned on the next call to [next]`()` */
        protected var index = 0

        override fun hasNext(): Boolean = index < list.size

        override fun next(): Fragment {
            if (!hasNext()) throw NoSuchElementException()
            return getCurrentFragment(index++)
        }
    }

}