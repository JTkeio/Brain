package jtkeio.brain
import java.io.File

val BMPInfo = arrayOf(66, 77, 102, 117, 0, 0, 0, 0, 0, 0, 54, 0, 0, 0, 40, 0, 0, 0, 100, 0, 0, 0, 100, 0, 0, 0, 1, 0, 24, 0, 0, 0, 0, 0, 48, 117, 0, 0, -60, 14, 0, 0, -60, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0)
//these are the first 52 bytes of a 100x100 BMP file

fun grabColorBMP(path: String, color: String): Array<Int> {
    val image = File(path).readBytes() //read the BMP file by bytes
    val numberOfPixels = (image.size-52)/3 //BMP files are 52 + 3*numberOfPixels
    val colorPixels = Array(numberOfPixels){-1} //initialize the map of one color channel, one spot for each pixel

    val colorShift = when(color){
        "r", "R" -> 0
        "g", "G" -> 2
        "b", "B" -> 1
        else -> -0
    } //translate the input string into the shift required for that color, default to red

    for (i in 0 until numberOfPixels) {
        colorPixels[i] = image[3*i+54+colorShift].toInt() //pixel values start on 53, incrementing by 3 grabs the next value of the same color, colorShift just moves which color is grabbed
    } //map one color's data into colorPixels

    return colorPixels
} //returns a list of values per color per pixel of a BMP file



fun main() {
    grabColorBMP("C:/Users/Jacob Tkeio/Desktop/Programs/Kotlin Projects/Batman.bmp", "r")
}