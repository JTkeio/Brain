import kotlin.random.Random

class Brain(dimensions:Array<Int>, ranges:Array<Int>) {
    val dimensions = dimensions //each entry is a new input, the value represents their possible values 0-n
    val ranges = ranges //each entry is a new output, the value represents their possible values 0-n
    val numberOfNeurons = multiplyArray(dimensions)
    val brain = Array<Any>(numberOfNeurons){0}

    fun multiplyArray(array: Array<Int>): Int {
    var initial = 1
    for (v in 0 until array.size) {
        initial *= array[v]
    }
    return initial
} //multiplies every integer in an array, converts an any-dimensional size into its one-dimensional size

    fun getLinear(dimensionalAddress: Array<Int>, tempDimensions: Array<Int>): Int {
        if (dimensionalAddress.size != tempDimensions.size) {
            println("Invalid dimensional address")
            return -1
        }
        var linearAddress = 0
        var tempNumberOfNeurons= multiplyArray(tempDimensions)

        for (x in 0 until tempDimensions.size) {
            tempNumberOfNeurons /= tempDimensions[x]
            linearAddress += tempNumberOfNeurons*dimensionalAddress[x]
        }
        return linearAddress
    } //converts any multi-dimensional coordinate into an equivalent one-dimensional coordinate

    fun getDimensional(linearAddress: Int, tempDimensions: Array<Int>): Array<Int> {
        var tempNumberOfNeurons = multiplyArray(tempDimensions)

        if (linearAddress > tempNumberOfNeurons || linearAddress < 0) {
            println("Invalid linear address")
            return if (tempDimensions.size == 1) (arrayOf(0,0)) else (arrayOf(0))
        }

        val dimensionalAddress = Array(tempDimensions.size){0}
        var tempLinearAddress = linearAddress

        for (z in 0 until tempDimensions.size) {
            tempNumberOfNeurons /= tempDimensions[z]
            while (tempLinearAddress >= tempNumberOfNeurons) {
                dimensionalAddress[z] += 1
                tempLinearAddress -= tempNumberOfNeurons
            }
        }
        return dimensionalAddress
    } //converts any linear coordinate into an equivalent multi-dimensional coordinate

    fun pushNeuron(dimensionalAddress:Array<Int>, values: Array<Int>): Int {
        val tempAddress = getLinear(dimensionalAddress, dimensions)

        if (values.size == ranges.size) {
            for (g in 0..values.size - 1) {
                if ((values[g] > ranges[g]) || (values[g] < 0)) {
                    println("Extraneous proposed values!")
                    return -1
                }
            }
            brain[tempAddress] = values
            return 1
        }
        println("Too many or too few values!")
        return -2
    } //pushes whatever you want into the brain at the place you want, given the proper constraints

    fun pullNeuron(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (dimensionalAddress.size != dimensions.size) {
            print("Invalid dimensional address")
            return arrayOf()
        }
        val neuron = brain[getLinear(dimensionalAddress, dimensions)]

        if (neuron is Int) {
            val newNeuron = generateNeuronProximityAverage(dimensionalAddress, searchGranularity)
            brain[getLinear(dimensionalAddress, dimensions)] = newNeuron
            return newNeuron //The neuron does not exist, construct it and send it through! TODO more options + user-made algorithms
        } else {
            return neuron as Array<Int> //The neuron exists, send it through!
        }

    } //C1: no neuron exists, create one from surrounding neurons \/ C2: neuron exists, return data

    fun generateNeuronProximityAverage(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        val neuronTypes = Array<Int>(2){0}
        val amalgamNeuron = Array<Int>(ranges.size){0}
        val bubbleArray = Array<Int>(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray
            for (r in 0 until searchArray.size) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }
            val searchAddress = getLinear(dimensionalAddress, dimensions) + getLinear(searchArray, dimensions)
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }
            val searchNeuron = brain[searchAddress] //overlay the seed with searchArray to move the search neuron
            if (searchNeuron is Int) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                for (l in 0 until (searchNeuron as Array<*>).size) {
                    amalgamNeuron[l] += searchNeuron[l] as Int //add the full neuron to amalgam so it can be averaged later
                }
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int, from the tally

        if (Random.nextDouble(0.0, neuronTypes.sum().toDouble()) < neuronTypes[0]/neuronTypes.sum()) { //a bit of randomness based on the % good/bad neurons gathered
            for (p in 0 until amalgamNeuron.size) {
                amalgamNeuron[p] /= if (neuronTypes[1] != 0) (neuronTypes[1]) else (1) //amalgam neuron is just a summation so far, but it should be an average
            }
            return amalgamNeuron //there were enough good neurons around this one, so it will become an average of them
        } else {
            return generateNeuronRandom() //there weren't many good neurons around this one, make a random one to test
        }
    } //my algorithm for generating neurons based on previous information

    fun generateNeuronRandom(): Array<Int> {
        val newNeuron = Array(ranges.size){0}
        for (h in 0 until ranges.size) {
            newNeuron[h] = Random.nextInt(0,ranges[h]+1) //fencepost issue
        }
        return newNeuron
    } //generates a random return value for the given neuron

    fun print(){
        for (u in 0 until numberOfNeurons) {
            val tempNeuron = brain[u]
            if (tempNeuron is Int) {print("$tempNeuron ")} else if (tempNeuron is Array<*>){
                for (w in tempNeuron) {
                    print(w.toString()+",")
                }
                print(" ")
            }


            val tempArray = getDimensional(u, dimensions)
            var tempSpacer: Boolean
            for (t in 0 until tempArray.size) {
                tempSpacer = true
                for (r in t until tempArray.size) {
                    tempSpacer = tempSpacer and (tempArray[r]+1==dimensions[r])
                }
                if (tempSpacer){println()}
            }
        }
    } //"displays" the brain (mostly for debug :)
}

fun main() {
    val lmao = Brain(arrayOf(10,30), arrayOf(1))
    for (hj in 0 until lmao.numberOfNeurons) {
        lmao.pullNeuron(lmao.getDimensional(hj, lmao.dimensions), 1)
    }
    lmao.print()
}