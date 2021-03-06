package jtkeio.brain

import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// NOTE from brainboy:
        //This test demonstrates the effectiveness of Brain's algorithms. I recommend generateNeuronProximityAverageProbability. It seems to work best here.
        //That being said, generateNeuronProximityAverageAbsolute could be better in other cases :)
        //To demonstrate, a 2D graph is filled partially with correct information at random points, then geometryBrain guesses what should go at other random points based on
            //what it sees around it, and then goes in order through all remaining points and guesses just so that the graph isn't half empty
        //To change algorithm, change the function in the definition:   geometryBrain.searchAlgorithm = {da:Array<Int>, sg:Int) -> generateNeuronProximityAverageProbability(da,sg)}   //ln. 25
        //The code running Brain can also give it feedback by pushing correct neurons or erasing incorrect neurons during runtime. For accuracy's sake, I didn't bother with that.
        //Finally, Brain is capable of multiple outputs of any range (besides negative) but it doesn't make sense to do more than two here. That demo will come soon!
    //Have fun, and beware of text stretch factors :) main is at the bottom.

fun geometryTest(geometricEquation: (searchAddress: Array<Int>) -> Boolean, dimensions: Array<Int>, searchGranularity: Int, percentageInformation: Double, doPrint: Boolean): Double {
    //geometricEquation defines the shape to replicate
    //the length of dimensions determines the number of dimensions, the length of each element determines the size of the dimensions
    //searchGranularity is how wide one neuron will search for info. Bigger isn't always better! 0 is random
    //percentageInformation is how much information you give geometryBrain to start with as a decimal percentage. It's not entirely accurate
    //doPrint shows the information before and after the algorithm deals with it
    //geometryTest() returns its accuracy


    val geometryBrain = Brain(dimensions, arrayOf(2)) //don't change the arrayOf(2) :p it limits the outputs to 0 and 1 (the 2 represents the 2 values)
    geometryBrain.searchAlgorithm = {da:Array<Int>, sg:Int -> geometryBrain.generateNeuronProximityAverageProbability(da,sg)} //Specify which algorithm to use. Choose one from Brain, below, or write one yourself.
    //generateNeuronProximityAverageProbability(da, sg)
    //generateNeuronProximityAverageAbsolute(da, sg)
    //generateNeuronProximityPluralityProbability(da, sg)
    //generateNeuronProximityPluralityAbsolute(da, sg)


    //Insert Information
    for (i in 0 until (geometryBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons))
        geometryBrain.pushNeuron(tempAddress, if (geometricEquation(tempAddress)) (arrayOf(1)) else (arrayOf(0)))
    } //puts in the correct information at random points. The amount of random (but correct!) information is determined by percentageInformation

    //if (doPrint) {geometryBrain.printBinary()} //print the information that was inserted randomly


    //Guess Remaining Information
    for (j in 0 until geometryBrain.numberOfNeurons*10) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons))
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //arbitrarily looks at random points and guesses what should go there. Change the algorithm at   geometryBrain.searchAlgorithm

    for (k in 0 until geometryBrain.numberOfNeurons) {
        val tempAddress = geometryBrain.getDimensional(k)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //go through all remaining neurons and guess (pullNeuron() does nothing if the neuron in question is already full so there is no negative to running back through all neurons)

    if (doPrint) {geometryBrain.printBinary()} //print all of the brain, including what we just constructed


    //Determine Accuracy of Information
    var accuracy = 0
    for (l in 0 until geometryBrain.numberOfNeurons) {
        val searchAddress = geometryBrain.getDimensional(l)
        if ((geometryBrain.pullNeuron(searchAddress,0)[0] == if (geometricEquation(searchAddress)) (1) else (0))) {
            accuracy += 1
        }
    }
    return accuracy/geometryBrain.numberOfNeurons.toDouble() //prints the accuracy of geometryBrain as a decimal percentage
}





fun main() {
    //define the equation you want geometryBrain to replicate below, or choose one of these by uncommenting
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> YOUR EQUATION HERE}

    //2D
    val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> (searchAddress[0]-50).toFloat().pow(2) + (searchAddress[1]-50).toFloat().pow(2) < 1000} //a circle
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0] > 50} //horizontal line
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[1] > 50} //vertical line
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0] + searchAddress[1] < 100} //slanted line
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0] > 7*sin(searchAddress[1].toDouble()/4) + 50} //sine wave
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> (searchAddress[0] > 20) and (searchAddress[0] < 80) and (searchAddress[1] > 20) and (searchAddress[1] < 80)} //square
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0]%4>1} //stripes  --the worst by miles lol
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0] > 80 || searchAddress[1] > 85 || searchAddress[0] + searchAddress[1] < 50} //combination of lines

    //3D
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> searchAddress[0] + searchAddress[1] + searchAddress[2] > 30} //diagonally slanted line
    //val geometricEquation: (Array<Int>) -> Boolean = {searchAddress: Array<Int> -> (searchAddress[0]>2) and (searchAddress[0]<8) and (searchAddress[1]>2) and (searchAddress[1]<8) and (searchAddress[2]>2) and (searchAddress[2]<8)} //cube


    println(geometryTest(geometricEquation, arrayOf(100, 100), 7, 0.01, true))
}