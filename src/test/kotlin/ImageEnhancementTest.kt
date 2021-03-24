package jtkeio.brain

import java.io.File
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.random.Random

//NOTE from brainboy:
    //This is just a more involved version of GeometryTest, and I would look there to start
    //The main is at the bottom again <3

fun imageEnhancementTest(image: BufferedImage, searchGranularity: Int, percentageInformation: Double, outputFolder: String = "") {
    val imageBrain = Brain(arrayOf(image.width, image.height), arrayOf(256, 256, 256))
    imageBrain.searchAlgorithm = {da, sg -> imageBrain.generateNeuronProximityPluralityProbability(da, sg)} //choose which algorithm imageBrain uses
    val startTime = System.currentTimeMillis() //This is a calculation-heavy program, let's time it


    //Insert Information
    for (a in 0 until (imageBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = imageBrain.getDimensional(Random.nextInt(0, imageBrain.numberOfNeurons))
        val tempColor = Color(image.getRGB(tempAddress[0], tempAddress[1]))
        imageBrain.pushNeuron(tempAddress, arrayOf(tempColor.blue, tempColor.green, tempColor.red))
        println("inserted $a")
    } //puts in the correct information at random points. The amount of random (but correct!) information is determined by percentageInformation

    if (outputFolder.isNotEmpty()) {
        val outputImage = BufferedImage(image.width, image.height, image.type)

        for (p in 0 until image.height) {
            for (o in 0 until image.width) {
                var outputColor = imageBrain.pullNeuron(arrayOf(o, p), -1)
                if (outputColor[0]<0) {
                    outputColor = Array(imageBrain.ranges.size){0}
                }
                outputImage.setRGB(o, p, Color(outputColor[2], outputColor[1], outputColor[0]).rgb)
            }
        }
        ImageIO.write(outputImage, "jpg", File("$outputFolder/ImageBrain_Data.jpg"))
        println("base image sent")
    } //output the information that was inserted randomly



    //Guess Remaining Information
    for (b in 0 until imageBrain.numberOfNeurons*3) {
        val tempAddress = imageBrain.getDimensional(Random.nextInt(0, imageBrain.numberOfNeurons))
        imageBrain.pullNeuron(tempAddress, searchGranularity)
        println("guessed $b")
    } //guess color values at randomly ordered pixels using searchAlgorithm

    for (c in 0 until imageBrain.numberOfNeurons) {
        val tempAddress = imageBrain.getDimensional(c)
        imageBrain.pullNeuron(tempAddress, searchGranularity)
        println("filled $c")
    } //clean-up run that guarantees no pixel leaves empty

    if (outputFolder.isNotEmpty()) {
        val outputImage = BufferedImage(image.width, image.height, image.type)

        for (l in 0 until image.height) {
            for (k in 0 until image.width) {
                var outputColor = imageBrain.pullNeuron(arrayOf(k, l), -1)
                if (outputColor[0]<0) {outputColor = Array(imageBrain.ranges.size){0}}
                outputImage.setRGB(k, l, Color(outputColor[2], outputColor[1], outputColor[0]).rgb)
            }
        }
        ImageIO.write(outputImage, "jpg", File("$outputFolder/ImageBrain_Output.jpg"))
        println("final image sent")
    } //output the final image

    val runTime = (System.currentTimeMillis()-startTime)/1000 //total seconds, converted from milliseconds, that this function took to run
    println("Enhancement of image took $runTime seconds")
}





fun main() {
    val input = File("C:/Users/Jacob Tkeio/Desktop/bridge.jpg")
    val image = ImageIO.read(input)
    imageEnhancementTest(image, 5, .2, "C:/Users/Jacob Tkeio/Desktop")
}