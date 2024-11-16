package consteval.plugin.interpreter

import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl

import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.util.dump

import org.jetbrains.kotlin.name.Name

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity

fun MessageCollector.reportWarning(message: String) = report(CompilerMessageSeverity.WARNING, message)

typealias Value = IrConst<*>

private sealed class InterpreterException: Exception() {
	
	class NonConstOperation(val reason: String) : InterpreterException()
	class StepLimitExsceeded(): InterpreterException()
	class Return(val value: Value?) : InterpreterException()
	class BreakContinue(val statement: IrBreakContinue) : InterpreterException()
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
class Interpreter(
	val logger: MessageCollector,
	val irBuiltIns: IrBuiltIns,
	val maxSteps: UInt,
	val prefix: String,
) : IrElementVisitor<Value, Scope> {
	var steps = 0U

	fun interpret(exp: IrExpression): Value? {
		steps = 0U
		try {
			return exp.accept(this, Scope())
		} catch (e: InterpreterException.NonConstOperation) {
			logger.reportWarning("Was not able to evaluate const eval function: ${e.reason}")
			return null
		} catch (e: InterpreterException.StepLimitExsceeded) {
			logger.reportWarning("Step limit exsceeded while evaluating const function")
			return null
		} catch (e: InterpreterException) {
			// The ir is semantically correct, therefore other exceptions should not occur
			return null
		}
	}

	override fun visitElement(element: IrElement, data: Scope): Value {
		logger.reportWarning("Don't know how to interpret expression ${element}:\n${element.dump()}")
		
		throw InterpreterException.NonConstOperation("")
	}

	override fun visitConst(expression: IrConst<*>, data: Scope): Value {
		// That's the first appearance of this line
		// Perhaps there is a cleaner way, but this will do for a test
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		return expression
	}
	
	override fun visitReturn(expression: IrReturn, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		
		val value = expression.value.accept(this, data)
		throw InterpreterException.Return(value)
	}

	override fun visitVariable(declaration: IrVariable, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		val value = declaration.initializer?.accept(this, data)
			?: with(declaration) {
				IrConstImpl.defaultValueForType(startOffset, endOffset, type)
			}
		data[declaration.symbol.owner.name] = value
		return value
	}

	override fun visitGetValue(expression: IrGetValue, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		val value = data[expression.symbol.owner.name]
		if (value == null)
			throw InterpreterException.NonConstOperation("non-constant value")
		return value
	}

	override fun visitSetValue(expression: IrSetValue, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		return expression.value.accept(this, data)
			.also { data[expression.symbol.owner.name] = it }
	}

	override fun visitWhen(expression: IrWhen, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		for (branch in expression.branches) {
			val pred = branch.condition.accept(this, data)
			// Kind should not be anything but Boolean, as the ir was type checked
			// Leaving it just in case
			if (pred.kind == IrConstKind.Boolean && pred.value as Boolean)
				return branch.result.accept(this, Scope(data))
		}
		return with(expression) {
			IrConstImpl.defaultValueForType(startOffset, endOffset, type)
		}
	}

	override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Scope) = when (val op = expression.operator) {
		IrTypeOperator.IMPLICIT_COERCION_TO_UNIT -> {
			if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
			expression.argument.accept(this, data)
			with (expression) {
				IrConstImpl.defaultValueForType(startOffset, endOffset, typeOperand)
			}
		}
		else -> {
			logger.reportWarning("Type operation not supported: $op")
			throw InterpreterException.NonConstOperation("unsupported type operation")
		}
	}

	override fun visitStringConcatenation(expression: IrStringConcatenation, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		// There is probably a better guess for the size of the string
		// than a character for each part
		val stringBuilder = StringBuilder(expression.arguments.size)
		val concatenatedString = expression.arguments.fold(stringBuilder) { res, part ->
			val concatPart = part.accept(this, data)

			val stringPart = when (concatPart.kind) {
				IrConstKind.String	-> (concatPart.value as String)
				IrConstKind.Int		-> (concatPart.value as Int).toString()
				IrConstKind.Boolean -> (concatPart.value as Boolean).toString()
				IrConstKind.Char	-> (concatPart.value as Char).toString()
				IrConstKind.Double	-> (concatPart.value as Double).toString()
				IrConstKind.Float	-> (concatPart.value as Float).toString()
				IrConstKind.Long	-> (concatPart.value as Long).toString()
				IrConstKind.Null	-> (concatPart.value as Nothing?).toString()
				IrConstKind.Byte	-> (concatPart.value as Byte).toString()
				IrConstKind.Short	-> (concatPart.value as Short).toString()
			}
			res.append(stringPart)
		}.toString()
		
		return with(expression) {
			IrConstImpl.string(startOffset, endOffset, irBuiltIns.stringType, concatenatedString)
		}
	}

	override fun visitCall(expression: IrCall, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		val func = expression.symbol.owner

		val visitor = this
		val args = Scope(data).apply {
			expression.dispatchReceiver
				?.accept(visitor, data)
				?.also { put(Name.special("<this>"), it) }
			expression.extensionReceiver
				?.accept(visitor, data)
				?.also { put(Name.special("<this>"), it) }
			
			for (i in 0..<expression.valueArgumentsCount) {
				val arg =
					(expression.getValueArgument(i)
						 ?: func.valueParameters[i].defaultValue?.expression)
					?.accept(visitor, data)
				
				if (arg != null) {
					put(func.valueParameters[i].name, arg)
				} else {
					// If argument is not constant, abort function call
					throw InterpreterException.NonConstOperation("non-const argument")
				}
			}
		}
		
		return func.accept(this, args)
	}

	override fun visitExpressionBody(body: IrExpressionBody, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		return body.expression.accept(this, data)
	}

	override fun visitBlockBody(body: IrBlockBody, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		return interpretStatements(body, data)
	}

	override fun visitBlock(expression: IrBlock, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		return interpretStatements(expression, data)
	}

	private fun interpretStatements(block: IrStatementContainer, scope: Scope): Value {
		var res: Value? = null
		try {
			for (statement in block.statements) {
				res = statement.accept(this, scope)
			}
		} catch (ret : InterpreterException.Return) {
			res = ret.value
		}
		return res!!
	}

	override fun visitBreak(jump: IrBreak, data: Scope): Value =
		throw InterpreterException.BreakContinue(jump)

	override fun visitContinue(jump: IrContinue, data: Scope): Value =
		throw InterpreterException.BreakContinue(jump)

	override fun visitWhileLoop(loop: IrWhileLoop, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		
		var res: Value = with(loop) {
			IrConstImpl.defaultValueForType(startOffset, endOffset, type)
		}
		
		val body = loop.body
		if (body == null) return res

		res = interpretWhileLoop(loop, Scope(data)) ?: res
		
		return res
	}

	override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		
		var res: Value = with(loop) {
			IrConstImpl.defaultValueForType(startOffset, endOffset, type)
		}
		
		val body = loop.body
		if (body == null) return res
		
		res = body.accept(this, data)
		res = interpretWhileLoop(loop, data) ?: res
		
		return res		
	}

	private fun interpretWhileLoop(loop: IrLoop, data: Scope): Value? {
		var res: Value? = null
		val body = loop.body!!
		
		var pred = loop.condition.accept(this, data)
		while (
			pred.kind == IrConstKind.Boolean &&
			pred.value as Boolean
		) {
			try {
				res = body.accept(this, data)
			} catch (bc: InterpreterException.BreakContinue) {
				if (loop.label != bc.statement.label) throw bc
				when (bc.statement) {
					is IrBreak -> break
					is IrContinue -> continue
				}
			}
			pred = loop.condition.accept(this, data)
		}
		return res
	}

	override fun visitFunction(declaration: IrFunction, data: Scope): Value {
		if (steps++ > maxSteps) throw InterpreterException.StepLimitExsceeded()
		
		val builtInRes = interpretBuiltIn(declaration, data)
		if (builtInRes != null) return builtInRes
		
		if (!declaration.name.asString().startsWith(prefix) && !declaration.name.isSpecial)
			throw InterpreterException.NonConstOperation("non-const function called")

		return declaration.body?.accept(this, data) ?: with(declaration) {
			IrConstImpl.defaultValueForType(startOffset, endOffset, returnType)
		}
	}
}
