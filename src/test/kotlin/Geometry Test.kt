package jtkeio.brain
import kotlin.math.pow
import kotlin.random.Random

// NOTE from brainboy:
    //This test demonstrates the effectiveness of Brain's algorithms. I recommend generateNeuronProximityAverageAbsolute. It seems to work best here.
    //That being said, generateNeuronProximityAverageProbability could be better when some amount of variance is expected of a neural net
    //To demonstrate, a 2D graph is filled partially with correct information at random points, then geometryBrain guesses what should go at other random points based on
        //what it sees around it, and then goes in order through all remaining points just so that the graph isn't half empty
    //The algorithm must be changed in the Brain class file, but I'm working on a way to set it like a variable <3
    //The code running Brain can also give it feedback by pushing correct neurons or erasing incorrect neurons during runtime. For accuracy's sake, I didn't bother with that.
    //Finally, Brain is capable of multiple outputs of any range (besides negative) but it doesn't make sense to do more than two here. That demo will come soon!
//Have fun, and beware of text stretch factors :)

fun main() {
    val geometryBrain = Brain(arrayOf(30,30), arrayOf(1)) //here you can define what your dimensions are in y to x order to fit your screen, but don't change the arrayOf(1) :p
    val searchGranularity = 5 //how wide one neuron will search for info. Bigger isn't always better! 0 is random
    val percentageInformation = .5 //how much information you give geometryBrain to start with as a decimal percentage
    fun geometricEquation(searchAddress: Array<Int>): Boolean {
        //define the equation you want geometryBrain to replicate here, or choose one of these

        return (searchAddress[0]-15).toFloat().pow(2) + (searchAddress[1]-15).toFloat().pow(2) > 30 //a circle
        //return searchAddress[0]>15 //horizontal line
        //return searchAddress[1]>15 //vertical line
        //return searchAddress[0] + searchAddress[1] > 30 //slanted line
    }

    for (i in 0 until (geometryBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons), geometryBrain.dimensions)
        geometryBrain.pushNeuron(tempAddress, if (geometricEquation(tempAddress)) (arrayOf(1)) else (arrayOf(0)))
    } //puts in the correct information at random points. The amount of random (but correct!) information is determined by percentageInformation

    for (j in 0 until geometryBrain.numberOfNeurons) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons), geometryBrain.dimensions)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //arbitrarily looks at random points and guesses what should go there. For now, the algorithm being used has to be change in the Brain class file.

    for (k in 0 until geometryBrain.numberOfNeurons) {
        val tempAddress = geometryBrain.getDimensional(k, geometryBrain.dimensions)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //go through all remaining neurons and guess (pullNeuron() does nothing if the neuron in question is already full so there is no negative to running back through all neurons)

    geometryBrain.printBinaryImage() //"#" is true, " " is false. Also try geometryBrain.print() to see the raw data.
}