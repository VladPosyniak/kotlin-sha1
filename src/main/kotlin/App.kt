package main

//Основна функція для запуску програми
fun main(args: Array<String>) {
    val phrase:String = args[0];
    val sha1 = Sha1()
    val hash = sha1.getHash(phrase)
    println("Hash: $hash")
}