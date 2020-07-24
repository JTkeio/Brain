package jtkeio.brain
import kotlin.math.pow
import kotlin.random.Random


fun main() {
    println(geometryTest(arrayOf(25, 50), 3, 0.2, true))
}


fun geometryTest(dimensions: Array<Int>, searchGranularity: Int, percentageInformation: Double, doPrint: Boolean): Double {

    // NOTE from brainboy:
        //This test demonstrates the effectiveness of Brain's algorithms. I recommend generateNeuronProximityAverageAbsolute. It seems to work best here.
        //That being said, generateNeuronProximityAverageProbability could be better when some amount of variance is expected of a neural net
        //To demonstrate, a 2D graph is filled partially with correct information at random points, then geometryBrain guesses what should go at other random points based on
            //what it sees around it, and then goes in order through all remaining points just so that the graph isn't half empty
        //The algorithm must be changed in the Brain class file, but I'm working on a way to set it like a variable <3
        //The code running Brain can also give it feedback by pushing correct neurons or erasing incorrect neurons during runtime. For accuracy's sake, I didn't bother with that.
        //Finally, Brain is capable of multiple outputs of any range (besides negative) but it doesn't make sense to do more than two here. That demo will come soon!
    //Have fun, and beware of text stretch factors :)

    val geometryBrain = Brain(dimensions, arrayOf(1)) //here you can define what your dimensions are in y to x order to fit your screen, but don't change the arrayOf(1) :p
    val searchGranularity = searchGranularity //how wide one neuron will search for info. Bigger isn't always better! 0 is random
    val percentageInformation = percentageInformation //how much information you give geometryBrain to start with as a decimal percentage. It's not entirely accurate

    fun geometricEquation(searchAddress: Array<Int>): Boolean {
        //define the equation you want geometryBrain to replicate here, or choose one of these

        return (searchAddress[0]-12).toFloat().pow(2) + (searchAddress[1]-25).toFloat().pow(2) > 30 //a circle
        //return searchAddress[0] > 12 //horizontal line
        //return searchAddress[1] > 12 //vertical line
        //return searchAddress[0] + searchAddress[1] > 100 //slanted line
    }

    for (i in 0 until (geometryBrain.numberOfNeurons*percentageInformation).toInt()) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons), geometryBrain.dimensions)
        geometryBrain.pushNeuron(tempAddress, if (geometricEquation(tempAddress)) (arrayOf(1)) else (arrayOf(0)))
    } //puts in the correct information at random points. The amount of random (but correct!) information is determined by percentageInformation

    if (doPrint) {geometryBrain.printBinaryImage()} //print the information that was inserted randomly

    for (j in 0 until geometryBrain.numberOfNeurons*2) {
        val tempAddress = geometryBrain.getDimensional(Random.nextInt(0, geometryBrain.numberOfNeurons), geometryBrain.dimensions)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //arbitrarily looks at random points and guesses what should go there. For now, the algorithm being used has to be change in the Brain class file.

    for (k in 0 until geometryBrain.numberOfNeurons) {
        val tempAddress = geometryBrain.getDimensional(k, geometryBrain.dimensions)
        geometryBrain.pullNeuron(tempAddress, searchGranularity)
    } //go through all remaining neurons and guess (pullNeuron() does nothing if the neuron in question is already full so there is no negative to running back through all neurons)

    if (doPrint) {geometryBrain.printBinaryImage()} //print all of the brain, including what we just constructed

    var accuracy = 0
    for (l in 0 until geometryBrain.numberOfNeurons) {
        val searchAddress = geometryBrain.getDimensional(l, geometryBrain.dimensions)
        if ((geometryBrain.pullNeuron(searchAddress,0)[0] == if (geometricEquation(searchAddress)) (1) else (0))) {
            accuracy += 1
        }
    }
    return accuracy/geometryBrain.numberOfNeurons.toDouble() //prints the accuracy of geometryBrain as a decimal percentage
}