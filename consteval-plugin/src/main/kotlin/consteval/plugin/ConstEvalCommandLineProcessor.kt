package sample.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@ExperimentalCompilerApi
@AutoService(CommandLineProcessor::class)
class ConstEvalCommandLineProcessor : CommandLineProcessor {
	companion object {
		private const val OPTION_STEP_LIMIT = "stepLimit"
		private const val OPTION_PREFIX		= "prefix"
		private const val OPTION_DUMP       = "dump"

		val ARG_STEP_LIMIT	= CompilerConfigurationKey<UInt>(OPTION_STEP_LIMIT)
		val ARG_PREFIX		= CompilerConfigurationKey<String>(OPTION_PREFIX)
		val ARG_DUMP		= CompilerConfigurationKey<Boolean>(OPTION_DUMP)
	}

	override val pluginId = "consteval.plugin"
	override val pluginOptions = listOf(
		CliOption(
			optionName = OPTION_STEP_LIMIT,
			valueDescription = "unsigned int",
			description = "Maximum allowed amount of steps to take during compile time",
			required = false
		),
		CliOption(
			optionName = OPTION_PREFIX,
			valueDescription = "string",
			description = "Prefix-marker for functions to be executed at compile time",
			required = false,
		),
		CliOption(
			optionName = OPTION_DUMP,
			valueDescription = "boolean",
			description = "Set true to dump ir after transformation, default=true",
			required=false
		)
	)

	override fun processOption(
		option: AbstractCliOption,
		value: String,
		configuration: CompilerConfiguration
	) = when (option.optionName) {
		OPTION_STEP_LIMIT	-> configuration.put(ARG_STEP_LIMIT, value.toUInt())
		OPTION_PREFIX		-> configuration.put(ARG_PREFIX, value)
		OPTION_DUMP			-> configuration.put(ARG_DUMP, value.toBoolean())
		else				-> throw IllegalArgumentException("Unexpected config option: ${option.optionName}")
	}
}
