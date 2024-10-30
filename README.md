# ConstEval plugin

A Kotlin compiler plugin for compile time function evaluation.

## Build and Plugin Options

The project is built with:
```shell
./gradlew build
```

The plugin supports the following options:
- `stepLimit` - maximum amount of steps underlying interpreter can take to evaluate a function, default `10000`
- `prefix` - string prefix to mark functions for compile-time evaluation, default "eval"
- `level` - logging level of the plugin, default "warning", possible values:
  - "warning"
  - "info"
- `dump` - whether to dump resulting IR or not, default `true`

## Details

Plugin supports evaluation of functions containing:
- operations on constant types,
- calls to extensions on constant types
- calls to other compile-time evaluatable functions
- while and do-while loops
- formatted strings

## Implementation

The interpreter is a simple tree-walk interpreter. The interpreter leaves out some safety checks relying on the fact that IR is semantically and type correct. The interpreter eagerly tries to interpret all marked functions and aborts evaluation if non const operation is encountered.

## Improvements

- Add constant propagation pass
- Pre-check functions if they are even viable for const evaluation,
- Perform canonicalization of arithmetic expression, and const-fold them,
- Add support for broader amount of types: collection, classes with fields of constant types, etc,
- Rewrite with stack-less interpreter to safe on recursion,
- Change structure of the plugin to unite evaluation and propagation (this will allow propagating constants after loops),
- Generalize to symbolic evaluation.
