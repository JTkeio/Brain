package jtkeio.brain

import java.io.File
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random

//NOTE from brainboy:
    //This is just a more involved version of GeometryTest, and I would look there to start
    //One new concept is search granularity ramping, which lowers the search granularity as data is filled in
    //It can be ramped linearly, quadratically, cubically, or anything else depending on what integer you pass in
    //The higher the integer, the faster search granularity is ramped down
    //A ramp of 0 keeps a constant search granularity, the one you pass in
    //Ramping theoretically makes the final image more accurate (because you look more and more locally as you receive more and more local data), but it's another tradeoff of detail for noise, I find
    //The main is at the bottom again <3

fun imageEnhancementTest(image: BufferedImage, searchGranularity: Int, searchGranularityRamp: Int, percentageInformation: Double, outputFile: String = "") {
    val imageBrain = Brain(arrayOf(image.width, image.height), arrayOf(256, 256, 256))
    imageBrain.searchAlgorithm = {da, sg -> imageBrain.generateNeuronProximityPluralityProbability(da, sg)} //choose which algorithm imageBrain uses
    val startTime = System.currentTimeMillis() //This is a calculation-heavy program, let's time it


    //Insert Information
    for (a in 0 until (imageBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = imageBrain.getDimensional(Random.nextInt(0, imageBrain.numberOfNeurons))
        val tempColor = Color(image.getRGB(tempAddress[0], tempAddress[1]))
        imageBrain.pushNeuron(tempAddress, arrayOf(tempColor.red, tempColor.green, tempColor.blue))
        println("inserted $a")
    } //puts in the correct information at random points. The amount of random (but correct!) information is determined by percentageInformation

    imageBrain.printImage("${outputFile}_raw.jpg") //output the information that was inserted randomly


    //Guess Remaining Information
    val searchLength = imageBrain.numberOfNeurons*3
    for (b in 0 until searchLength) {
        val tempAddress = imageBrain.getDimensional(Random.nextInt(0, imageBrain.numberOfNeurons))
        imageBrain.pullNeuron(tempAddress, ceil( searchGranularity * ((searchLength-b) / searchLength.toDouble()).pow(searchGranularityRamp)).toInt()) //this expression is just searchGranularity if granularityRamp==0, otherwise the granularity is gradually lowered to the order of granularityRamp while imageBrain is filled
        println("guessed $b")
    } //guess color values at randomly ordered pixels using searchAlgorithm

    for (c in 0 until imageBrain.numberOfNeurons) {
        val tempAddress = imageBrain.getDimensional(c)
        imageBrain.pullNeuron(tempAddress, searchGranularity)
        println("filled $c")
    } //clean-up run that guarantees no pixel leaves empty

    imageBrain.printImage("$outputFile.jpg") //output the final image


    println("Enhancement of image took ${(System.currentTimeMillis()-startTime)/1000} seconds")
}



fun main() {
    val input = File("C:/Users/Jacob Tkeio/Desktop/mount.jpg")
    val image = ImageIO.read(input)
    imageEnhancementTest(image, 15, 1, .3,"C:/Users/Jacob Tkeio/Desktop/imageBrain")
}