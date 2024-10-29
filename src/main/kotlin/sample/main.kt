package sample

fun main() {
	println(evalAdd(3, 2))
	println(evalHello())
	println(evalLoop())
	println(evalNot(false))
	println(evalRec(12L))
}

fun evalAdd(a: Int, b: Int) = a + b

fun evalHello(name: String = "World!", age: Int = 10) = "Hello, $name. You are $age years old!"

fun evalRec(a: Long): Long = when (a) {
	0L -> 1L
	else -> a * evalRec(a - 1L)
}

fun evalNot(a: Boolean) = a.not()

fun notEval(a: Int): Int {
	fun evalFoo(): Int = 11
	fun evalIn(a: Int = 10) = a * 10 + a

	val foo = evalIn(evalFoo())
	
	return a * foo
}

fun evalLoop(): Int {
	var a = 0
	// for (i in 1..6) a += i

	var i = 0
	while (i < 7) {
		a += i
		i++
	}

	return a
}
