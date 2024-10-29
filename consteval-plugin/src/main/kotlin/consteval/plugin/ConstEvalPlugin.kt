package sample.plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.util.dump

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension

import sample.plugin.interpreter.Interpreter

class ConstEvalPlugin(
	private val logger: MessageCollector,
	private val dump: Boolean,
	private val stepLimit: UInt,
	private val prefix: String,
) : IrGenerationExtension {
	override fun generate(
		moduleFragment: IrModuleFragment,
		pluginContext: IrPluginContext
	) {
		val interpreter = Interpreter(logger, pluginContext.irBuiltIns, stepLimit, prefix)
		
		moduleFragment.transform(ConstEvalPass(logger, interpreter, prefix), null)

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
		
		val funcName = expression.symbol.owner.name.identifier
		if (!funcName.startsWith(prefix)) return expression

		val res = interpreter.interpret(expression)

		return res ?: expression
	}
}

