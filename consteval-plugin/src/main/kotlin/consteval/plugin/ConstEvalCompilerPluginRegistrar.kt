package consteval.plugin

import com.google.auto.service.AutoService

import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension

@ExperimentalCompilerApi
@AutoService(CompilerPluginRegistrar::class)
class ConstEvalCompilerPluginRegistrar: CompilerPluginRegistrar() {
	
	override val supportsK2: Boolean
		get() = true

	override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
		val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
		val stepLimit = configuration.get(ConstEvalCommandLineProcessor.ARG_STEP_LIMIT, 10_000U)
		val prefix = configuration.get(ConstEvalCommandLineProcessor.ARG_PREFIX, "eval")
		val level = configuration.get(ConstEvalCommandLineProcessor.ARG_LEVEL, "warning")
		val dump = configuration.get(ConstEvalCommandLineProcessor.ARG_DUMP, true)

		IrGenerationExtension.registerExtension(ConstEvalPlugin(logger, level, dump, stepLimit, prefix))
	}
}
