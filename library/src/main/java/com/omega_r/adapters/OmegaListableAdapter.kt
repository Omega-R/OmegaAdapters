package com.omega_r.adapters

import kotlin.reflect.KProperty

/**
 * Created by Anton Knyazev on 2019-07-15.
 */
interface OmegaListableAdapter<T> {

    var list: List<T>

    private operator fun setValue(any: Any?, property: KProperty<*>, list: List<T>) {
        this.list = list
    }

    private operator fun getValue(any: Any?, property: KProperty<*>): List<T> = list

}