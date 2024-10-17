

fun main(){
    var a : Long = 317121849
    var b : Float = 8.6f

    val c = "Hola"
    val d: Boolean = true
    var cadena = "Hola" + "Daniela"
    cadena += "Num cuenta: $a"
    var cadena2 = "La suma de $a + $b es ${a+b}"

    var cadena3 = "El \"total\" en el carrito es: \$$b"

    println(cadena)
    println(cadena2)
    println(cadena3)
}