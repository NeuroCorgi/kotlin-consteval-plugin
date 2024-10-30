package consteval.plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.*
import org.jetbrains.kotlin.ir.util.dump

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension

import consteval.plugin.interpreter.Interpreter
import consteval.plugin.interpreter.Scope

class ConstEvalPlugin(
	private val logger: MessageCollector,
	private val level: String,
	private val dump: Boolean,
	private val stepLimit: UInt,
	private val prefix: String,
) : IrGenerationExtension {
	override fun generate(
		moduleFragment: IrModuleFragment,
		pluginContext: IrPluginContext
	) {
		val interpreter = Interpreter(logger, pluginContext.irBuiltIns, stepLimit, prefix)

		val constEvalPass = ConstEvalPass(logger, interpreter, prefix)
		val constPropPass = ConstPropPass(logger, interpreter)

		for (_i in 0..<1) {
			moduleFragment.transform(constEvalPass, null)
			moduleFragment.accept(constPropPass, null)
		}

		if (dump)
			logger.report(CompilerMessageSeverity.WARNING, moduleFragment.dump())
	}
}

internal class ConstEvalPass(
	private val logger: MessageCollector,
	private val interpreter: Interpreter,
	private val prefix: String,
) : IrElementTransformerVoid() {

	override fun visitCall(expression: IrCall): IrExpression {
		expression.transformChildren(this, null)

		val funcName = expression.symbol.owner.name
		if (!funcName.asString().startsWith(prefix) && !funcName.isSpecial) return expression

		val res = interpreter.interpret(expression)

		return res ?: expression
	}
}

internal class ConstPropPass(
	private val logger: MessageCollector,
	private val interpreter: Interpreter,
) : IrElementVisitorVoid {

	override fun visitElement(element: IrElement) =
		element.acceptChildrenVoid(this)

	override fun visitBlock(expression: IrBlock) {
		logger.report(CompilerMessageSeverity.WARNING, "Block visited")
		visitStatements(expression)
	}

	override fun visitBlockBody(body: IrBlockBody) {
		logger.report(CompilerMessageSeverity.WARNING, "Block body visited")
		visitStatements(body)
	}

	private fun visitStatements(block: IrStatementContainer) {
		var scope = Scope()

		for (statement in block.statements) {
			when (statement) {
				is IrVariable -> {
					val name = statement.symbol.owner.name
					statement.initializer
						?.let { interpreter.interpret(it, scope) }
						?.also { scope[name] = it }
				}
				is IrGetValue -> {
					val name = statement.symbol.owner.name
					if (name in scope) {
						statement.run {
							this = scope[name]!!
						}
					}
				}
				is IrLoop ->
					// Do not even try to const-eval loops
					break
				else -> break
			}
		}
	}
}

