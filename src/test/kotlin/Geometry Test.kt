package jtkeio.brain
import kotlin.random.Random

// NOTE from brainboy:
    //This test demonstrates the effectiveness of Brain's algorithms. I recommend generateNeuronProximityAverageAbsolute. It seems to work best here.
    //That being said, generateNeuronProximityAverageProbability could be better when some amount of variance is expected of a neural net
    //The algorithm must be changed in the Brain class file, but I'm working on a way to set it like a variable <3
    //Finally, Brain is capable of multiple outputs of any range (besides negative) but it doesn't make sense to do more than two here. That demo will come soon!
//Have fun :)

fun main() {
    val geometryBrain = Brain(arrayOf(15,60), arrayOf(1)) //here you can define what your dimensions are in y to x order to fit your screen, but don't change the arrayOf(1) :p
    val searchGranularity = 1 //how wide one neuron will search for info. Bigger isn't always better!
    val percentageInformation = .5 //how much information you give geometryBrain to start with as a decimal percentage
    fun geometricEquation(searchAddress: Array<Int>): Boolean {
        return searchAddress[0]>7 //define the equation you want geometryBrain to replicate here
    }

    for (i in 0 until (geometryBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons), geometryBrain.dimensions)
        geometryBrain.pushNeuron(tempAddress, if (geometricEquation(tempAddress)) (arrayOf(1)) else (arrayOf(0)))
    } //puts in the correct information at random points. The amount of random (but correct!) points is determined by percentageInformation

    for (j in 0 until geometryBrain.numberOfNeurons) {
        val tempAddress = geometryBrain.getDimensional(j, geometryBrain.dimensions)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //go through all remaining neurons and guess what should go there. For now, the algorithm being used has to be change in the Brain class file.
        //a real application would not scan through this so linearly, but this gets the job done

    geometryBrain.printBinaryImage() //"#" is true, " " is false. Also try geometryBrain.print() to see the raw data.
}