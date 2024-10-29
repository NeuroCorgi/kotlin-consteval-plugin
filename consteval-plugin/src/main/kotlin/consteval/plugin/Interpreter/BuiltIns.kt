package sample.plugin.interpreter

import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.impl.*

import org.jetbrains.kotlin.name.Name

fun Interpreter.interpretBuiltIn(func: IrFunction, args: Scope): Value? = when (func.name.identifier) {
	"EQEQ" -> {
		val lhs = args[Name.identifier("arg0")]
		val rhs = args[Name.identifier("arg1")]

		if (lhs == null || rhs == null) {
			null
		} else if (lhs.kind == rhs.kind) {
			when (lhs.kind) {
				IrConstKind.Int ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Int) == (rhs.value as Int))
				IrConstKind.String ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as String) == (rhs.value as String))
				IrConstKind.Char ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Char) == (rhs.value as Char))
				IrConstKind.Double ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Double) == (rhs.value as Double))
				IrConstKind.Float ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Float) == (rhs.value as Float))
				IrConstKind.Long ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Long) == (rhs.value as Long))
				IrConstKind.Byte ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Byte) == (rhs.value as Byte))
				IrConstKind.Short ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Short) == (rhs.value as Short))
				IrConstKind.Boolean ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Boolean) == (rhs.value as Boolean))
				IrConstKind.Null ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Nothing?) == (rhs.value as Nothing?))
			}
		} else {
			null
		}
	}
	"less" -> {
		val lhs = args[Name.identifier("arg0")]
		val rhs = args[Name.identifier("arg1")]

		if (lhs == null || rhs == null) {
			null
		} else if (lhs.kind == rhs.kind) {
			when (lhs.kind) {
				IrConstKind.Int ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Int) < (rhs.value as Int))
				IrConstKind.String ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as String) < (rhs.value as String))
				IrConstKind.Char ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Char) < (rhs.value as Char))
				IrConstKind.Double ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Double) < (rhs.value as Double))
				IrConstKind.Float ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Float) < (rhs.value as Float))
				IrConstKind.Long ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Long) < (rhs.value as Long))
				IrConstKind.Byte ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Byte) < (rhs.value as Byte))
				IrConstKind.Short ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Short) < (rhs.value as Short))
				IrConstKind.Boolean ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Boolean) < (rhs.value as Boolean))
				else -> null
			}
		} else {
			null
		}
	}
	"greater" -> {
		val lhs = args[Name.identifier("arg0")]
		val rhs = args[Name.identifier("arg1")]

		if (lhs == null || rhs == null) {
			null
		} else if (lhs.kind == rhs.kind) {
			when (lhs.kind) {
				IrConstKind.Int ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Int) > (rhs.value as Int))
				IrConstKind.String ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as String) > (rhs.value as String))
				IrConstKind.Char ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Char) > (rhs.value as Char))
				IrConstKind.Double ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Double) > (rhs.value as Double))
				IrConstKind.Float ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Float) > (rhs.value as Float))
				IrConstKind.Long ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Long) > (rhs.value as Long))
				IrConstKind.Byte ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Byte) > (rhs.value as Byte))
				IrConstKind.Short ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Short) > (rhs.value as Short))
				IrConstKind.Boolean ->
					IrConstImpl.boolean(lhs.startOffset, rhs.endOffset, irBuiltIns.booleanType, (lhs.value as Boolean) > (rhs.value as Boolean))
				else -> null
			}
		} else {
			null
		}
	}
	"plus" -> {
		val lhs = args[Name.identifier("\$this")]
		val rhs = args[Name.identifier("other")]

		if (lhs == null || rhs == null) {
			null
		} else {
			when (lhs.kind) {
				IrConstKind.Char ->
					when (rhs.kind) {
						IrConstKind.Int ->
							IrConstImpl.char(lhs.startOffset, rhs.endOffset, irBuiltIns.charType, (lhs.value as Char).plus(rhs.value as Int))
						else -> null
					}
				IrConstKind.Byte ->
					when (rhs.kind) {
						IrConstKind.Int ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Byte).plus(rhs.value as Int))
						IrConstKind.Short ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Byte).plus(rhs.value as Short))
						IrConstKind.Long ->
							IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Byte).plus(rhs.value as Long))
						IrConstKind.Float ->
							IrConstImpl.float(lhs.startOffset, rhs.endOffset, irBuiltIns.floatType, (lhs.value as Byte).plus(rhs.value as Float))
						IrConstKind.Double ->
							IrConstImpl.double(lhs.startOffset, rhs.endOffset, irBuiltIns.doubleType, (lhs.value as Byte).plus(rhs.value as Double))
						IrConstKind.Byte ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Byte).plus(rhs.value as Byte))
						else -> null
					}
				IrConstKind.Int ->
					when (rhs.kind) {
						IrConstKind.Int ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Int).plus(rhs.value as Int))
						IrConstKind.Short ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Int).plus(rhs.value as Short))
						IrConstKind.Long ->
							IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Int).plus(rhs.value as Long))
						IrConstKind.Float ->
							IrConstImpl.float(lhs.startOffset, rhs.endOffset, irBuiltIns.floatType, (lhs.value as Int).plus(rhs.value as Float))
						IrConstKind.Double ->
							IrConstImpl.double(lhs.startOffset, rhs.endOffset, irBuiltIns.doubleType, (lhs.value as Int).plus(rhs.value as Double))
						IrConstKind.Byte ->
							IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Int).plus(rhs.value as Byte))
						else -> null
					}
				IrConstKind.Short ->
					IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Short).plus(rhs.value as Short))
				IrConstKind.Long ->
					IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Long).plus(rhs.value as Long))
				IrConstKind.String ->
					IrConstImpl.string(lhs.startOffset, rhs.endOffset, irBuiltIns.stringType, (lhs.value as String).plus(rhs.value as String))
				else -> null
			}
		}
	}
	"minus" -> {
		val lhs = args[Name.identifier("\$this")]
		val rhs = args[Name.identifier("other")]

		if (lhs == null || rhs == null) {
			null
		} else {
			when (lhs.kind) {
				IrConstKind.Int ->
					IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Int).minus(rhs.value as Int))
				IrConstKind.Short ->
					IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Short).minus(rhs.value as Short))
				IrConstKind.Long ->
					when (rhs.kind) {
						IrConstKind.Int ->
							IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Long).minus(rhs.value as Int))
						IrConstKind.Long ->
							IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Long).minus(rhs.value as Long))
						else -> null
					}
				else -> null
			}
		}
	}
	"times" -> {
		val lhs = args[Name.identifier("\$this")]
		val rhs = args[Name.identifier("other")]

		if (lhs == null || rhs == null) {
			null
		} else if (lhs.kind == rhs.kind) {
			when (lhs.kind) {
				IrConstKind.Int ->
					IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.intType, (lhs.value as Int).times(rhs.value as Int))
				IrConstKind.Short ->
					IrConstImpl.int(lhs.startOffset, rhs.endOffset, irBuiltIns.shortType, (lhs.value as Short).times(rhs.value as Short))
				IrConstKind.Long ->
					IrConstImpl.long(lhs.startOffset, rhs.endOffset, irBuiltIns.longType, (lhs.value as Long).times(rhs.value as Long))
				else -> null
			}
		} else {
			null
		}
	}
	"inc" -> args[Name.identifier("\$this")]?.run {
		when (kind) {
			IrConstKind.Int -> IrConstImpl.int(startOffset, endOffset, irBuiltIns.intType, (value as Int).inc())
			IrConstKind.Long -> IrConstImpl.long(startOffset, endOffset, irBuiltIns.longType, (value as Long).inc())
			IrConstKind.Short -> IrConstImpl.short(startOffset, endOffset, irBuiltIns.shortType, (value as Short).inc())
			else -> null
		}
	}
	"not" -> args[Name.identifier("\$this")]?.run {
		if (kind == IrConstKind.Boolean) {
			IrConstImpl.boolean(startOffset, endOffset, irBuiltIns.booleanType, (value as Boolean).not())
		} else {
			null
		}
	}
	else -> null
}
