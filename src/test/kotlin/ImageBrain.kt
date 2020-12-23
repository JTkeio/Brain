package jtkeio.brain
import java.io.File



fun grabColorBMP(path: String, color: String): Array<Int> {
    val a = File(path).readBytes()
    val numberOfPixels = (a.size-52)/3
    val colorPixels = Array(numberOfPixels){-1}

    val colorShift = when(color){
        "r", "R" -> 0
        "g", "G" -> 2
        "b", "B" -> 1
        else -> -0
    } //default to red

    for (i in 0 until numberOfPixels) {
        colorPixels[i] = a[3*i+53+colorShift].toInt()
    }
    return colorPixels
} //returns a list of values per color per pixels of a BMP

fun main() {
    grabColorBMP("C:/Users/Jacob Tkeio/Desktop/Programs/Kotlin Projects/Batman.bmp", "r")
}