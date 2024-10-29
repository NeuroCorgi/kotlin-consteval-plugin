package sample.plugin.interpreter

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.ir.expressions.IrConst

class Scope(private val parent: Scope? = null) {
	private val mapping: MutableMap<Name, IrConst<*>>	= mutableMapOf()

	operator fun get(key: Name): IrConst<*>? =
		(mapping.get(key) ?: parent?.get(key))
	operator fun set(key: Name, value: IrConst<*>) =
		put(key, value)
	
	fun put(key: Name, value: IrConst<*>) {
		mapping[key] = value
	}

	override fun toString(): String {
		return "Scope<$mapping, parent=$parent>"
	}
}
