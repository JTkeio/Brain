package jtkeio.brain

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.random.Random
import java.awt.image.BufferedImage

//each entry in dimensions is a new input channel, the value represents each channel's possible values 0 to n
//each entry in ranges is a new output channel, the value represents each channel's possible values 0 to n-1

class Brain(var dimensions: Array<Int>, var ranges: Array<Int>) {
    val numberOfNeurons = multiplyArray(dimensions)
    val brain = Array(numberOfNeurons){Array(ranges.size){-1}} //initialize brain with arrays fitting the constraints from ranges but filled with -1 to signify emptiness
    var searchAlgorithm = {dimensionalAddress:Array<Int>, searchGranularity:Int -> generateNeuronProximityAverage(dimensionalAddress,searchGranularity)} //define default search algorithm
    //please do write your own algorithms! just reassign this variable under your instance of Brain


    //Utilities
    fun multiplyArray(array: Array<Int>): Int {
    var initial = 1
    for (v in array) {
        initial *= v
    }
    return initial
} //multiplies every integer in an array, converts an any-dimensional size into its one-dimensional size

    fun getLinear(dimensionalAddress: Array<Int>, tempDimensions: Array<Int> = dimensions): Int {
        if (dimensionalAddress.size != tempDimensions.size) {
            println("Invalid dimensional address")
            return -1
        }
        var linearAddress = 0
        var tempNumberOfNeurons = multiplyArray(tempDimensions)

        for (x in tempDimensions.indices) {
            tempNumberOfNeurons /= tempDimensions[x]
            linearAddress += tempNumberOfNeurons*dimensionalAddress[x]
        }
        return linearAddress
    } //converts any multi-dimensional coordinate into an equivalent one-dimensional coordinate, default dimensions are Brain dimensions

    fun getDimensional(linearAddress: Int, tempDimensions: Array<Int> = dimensions): Array<Int> {
        var tempNumberOfNeurons = multiplyArray(tempDimensions)

        if (linearAddress > tempNumberOfNeurons || linearAddress < 0) {
            println("Invalid linear address")
            return if (tempDimensions.size == 1) (arrayOf(0,0)) else (arrayOf(0))
        }

        val dimensionalAddress = Array(tempDimensions.size){0}
        var tempLinearAddress = linearAddress

        for (z in tempDimensions.indices) {
            tempNumberOfNeurons /= tempDimensions[z]
            while (tempLinearAddress >= tempNumberOfNeurons) {
                dimensionalAddress[z] += 1
                tempLinearAddress -= tempNumberOfNeurons
            }
        }
        return dimensionalAddress
    } //converts any linear coordinate into an equivalent multi-dimensional coordinate, default dimensions are Brain dimensions

    fun getSpaces(linearAddress: Int, tempDimensions: Array<Int> = dimensions): Int {
        val tempArray = getDimensional(linearAddress)
        var tempSpacer: Boolean
        var spaceCount = 0
        for (t in tempArray.indices) {
            tempSpacer = true
            for (r in t until tempArray.size) {
                tempSpacer = tempSpacer and (tempArray[r] + 1 == tempDimensions[r])
            }
            if (tempSpacer) {spaceCount++}
        }
        return spaceCount
    } //returns the number of appropriate spaces behind the given address to show the proper dimensions


    //Information Handling
    fun print(){
        for (u in 0 until numberOfNeurons) {
            val tempNeuron = brain[u]

            if (tempNeuron[0]<0) {print("  ")} else {
                val tempArray = Array(tempNeuron.size){0}
                for (w in tempNeuron.indices) {
                    tempArray[w] = tempNeuron[w]
                }

                print(tempArray.joinToString(","))
                print(" ")
            }

            for (q in 0 until getSpaces(u)) {println()}
        }
    } //"displays" the brain.

    fun printBinary(){
        for (u in 0 until numberOfNeurons) {
            val tempNeuron = brain[u]
            if (tempNeuron[0]<0) {print("   ")} else {
                if (tempNeuron.size > 1) {println("no array size (ranges) greater than one allowed!!!")} else if (tempNeuron[0] > 0) {print("#")} else {print(". ")}
                print(" ")
            }

            for (q in 0 until getSpaces(u)) {println()}
        }
    } //also "displays" the brain, but only when there is only one value in ranges. 0 is empty and anything greater (ideally 1) is a #

    fun printImage(address: String){
        if (address.contains(".") && dimensions.size>=2 && ranges.size>=3) { //there must be an address, brain must be at least 2D (like an image), and ranges must have at least 3 values (RGB)
            val outputFile = File(address)
            val outputImage = BufferedImage(dimensions[0], dimensions[1], 1) //1 means RGB colorspace

            for (l in 0 until dimensions[1]) {
                for (k in 0 until dimensions[0]) {
                    var outputColor = pullNeuron(arrayOf(k, l), -1) //pulling data directly from every x,y coordinate in brain
                    if (outputColor[0] < 0) {outputColor = Array(ranges.size){0}} //outputs black if there is no data (0,0,0)

                    outputImage.setRGB(k, l, Color(outputColor[0], outputColor[1], outputColor[2]).rgb) //puts RGB at x,y in outputImage
                }
            }

            ImageIO.write(outputImage, address.split(".")[1], outputFile)

        } else {
            print("printImage failed: ")
            when {
                !address.contains(".") -> println("the address must contain file type information in the form of a file type extension, like .jpg")
                dimensions.size<2 -> println("there must be at least two dimensions in order to construct an image")
                ranges.size<3 -> println("there must be at least three value ranges in order to pull RGB data to construct an image")
            }
        }
    } //also "displays" the brain, but only when there are three values in ranges and the brain is two dimensonal. 0,0,0 is black and 255,255,255 is white

    fun store(address: String) {
        val file = File(address)
        val stream = file.writer()
        stream.write(dimensions.toList().joinToString(",") + "\n") //writes the dimensions of the brain on their own line
        stream.write(ranges.toList().joinToString(",") + "\n") //writes the ranges of the brain on their own line

        val neuronList = Array<String>(numberOfNeurons){""} //compile all neurons into an array of strings
        for (tempIndex in 0 until numberOfNeurons) {
            neuronList[tempIndex] = brain[tempIndex].joinToString(",") //convert every neuron from an array of integers to strings with comma separators and add them to neuronList
        }

        stream.write(neuronList.joinToString(" ")) //write every neuron to the file with spaces in between
        stream.close()
    } //writes brain to a file to allow for long-term development, multiple sources for a single brain, or just storing what a particular brain has learned

    fun read(address: String) {
        val file = File(address)
        val lines = file.readLines()
        val readDimensions = lines[0].split(",").map{str -> str.toInt()}.toTypedArray()
        val readRanges = lines[1].split(",").map{str -> str.toInt()}.toTypedArray()
        val readBrain = lines[2].split(" ").map{it.split(",").map{it.toInt()}.toTypedArray()}.toTypedArray()

        this.dimensions = readDimensions
        this.ranges = readRanges
        for (i in readBrain.indices) {
            this.brain[i] = readBrain[i] //copy each neuron one by one
        }
    } //reads brain from a file directly into this one


    //Brain Interaction (choose algorithm with the searchAlgorithm variable)
    fun pushNeuron(dimensionalAddress:Array<Int>, values: Array<Int>): Int {
        val tempAddress = getLinear(dimensionalAddress)
        if (tempAddress<0 || tempAddress>numberOfNeurons) {
            println("invalid dimensional address when pushing a neuron")
            return -3
        }

        if (values.size == ranges.size) {
            for (g in values.indices) {
                if (values[g] > ranges[g]-1) { //fencepost issue
                    println("Extraneous proposed values when pushing a neuron")
                    return -1
                }
            }
            brain[tempAddress] = values
            return 1
        }
        println("Too many or too few values when pushing a neuron")
        return -2
    } //pushes whatever you want into the brain at the place you want, given the proper constraints [ADDED ABILITY TO PUSH NEGATIVE NUMBERS FOR DATA ERASURE]

    fun pullNeuron(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (dimensionalAddress.size != dimensions.size) {
            print("invalid dimensional address when pulling a neuron")
            return emptyArray()
        }
        val neuronIndex = getLinear(dimensionalAddress)

        if (neuronIndex<0 || neuronIndex>numberOfNeurons) {
            println("invalid dimensional address when pulling a neuron")
            return emptyArray()
        }
        val searchNeuron = brain[neuronIndex]

        if (searchNeuron[0]<0 && searchGranularity>-1) {
            val newNeuron = searchAlgorithm(dimensionalAddress, searchGranularity) //The neuron does not exist, construct it using the given algorithm
            pushNeuron(dimensionalAddress, newNeuron) //put the new neuron back into the brain
            return newNeuron //send it through!
        } else {
            return searchNeuron //The neuron exists or we just want the data, so send it through!
        }

    } //just grabs data if searchGranularity < 0 or   //C1: no neuron exists, create one from surrounding neurons \/ C2: neuron exists, return data


    //Learning Algorithms (per neuron)
    fun generateNeuronRandom(): Array<Int> {
        val newNeuron = Array(ranges.size){0}
        for (h in ranges.indices) {
            newNeuron[h] = Random.nextInt(0, ranges[h])
        }
        return newNeuron
    } //generates a random return value for the given neuron

    fun generateNeuronProximityAverageProbability(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val amalgamNeuron = Array(ranges.size){0}
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray
            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                for (l in searchNeuron.indices) {
                    amalgamNeuron[l] += searchNeuron[l] //add the full neuron to amalgam so it can be averaged later
                }
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int, from the tally

        if (Random.nextDouble(0.0, neuronTypes.sum().toDouble()) > neuronTypes[0]/neuronTypes.sum().toDouble()) { //a bit of randomness based on the % good/bad neurons gathered
            for (p in amalgamNeuron.indices) {
                amalgamNeuron[p] = (amalgamNeuron[p].toDouble() / if (neuronTypes[1] != 0) (neuronTypes[1]) else (1) ).roundToInt() //amalgam neuron is just a summation so far, but it should be an average. DO NOT divide by 0
            }
            return amalgamNeuron //there were enough good neurons around this one, so it will become an average of them
        } else {
            return generateNeuronRandom() //there weren't many good neurons around this one, make a random one to test
        }
    } //my original algorithm for generating neurons based on previous information by creating an average of all seen neurons (or having a chance of returning a random neuron if there wasn't enough data)

    fun generateNeuronProximityAverageAbsolute(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val amalgamNeuron = Array(ranges.size){0}
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray

            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                for (l in searchNeuron.indices) {
                    amalgamNeuron[l] += searchNeuron[l] //add the full neuron to amalgam so it can be averaged later
                }
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int, from the tally

        if (neuronTypes[0]<=neuronTypes[1]) { //removed probability, an equal amount of full and empty neurons defers to the full ones
            for (p in amalgamNeuron.indices) {
                amalgamNeuron[p] = ( amalgamNeuron[p].toDouble() / if (neuronTypes[1] != 0) (neuronTypes[1]) else (1) ).roundToInt() //amalgam neuron is just a summation so far, but it should be an average. DO NOT divide by 0
            }
            return amalgamNeuron //there were enough good neurons around this one, so it will become an average of them
        } else {
            return generateNeuronRandom() //there weren't many good neurons around this one, make a random one to test
        }
    } //the same as generateNeuronProximityAverageRandom (without the chance in choosing if there was enough data or not)

    fun generateNeuronProximityAverage(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val amalgamNeuron = Array(ranges.size){0}
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray

            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                for (l in searchNeuron.indices) {
                    amalgamNeuron[l] += searchNeuron[l] //add the full neuron to amalgam so it can be averaged later
                }
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int, from the tally

        if (neuronTypes[1]!=0) {
            for (p in amalgamNeuron.indices) {
                amalgamNeuron[p] = (amalgamNeuron[p].toDouble() / if (neuronTypes[1] != 0) (neuronTypes[1]) else (1)).roundToInt() //amalgam neuron is just a summation so far, but it should be an average. DO NOT divide by 0
            }
            return amalgamNeuron //complete average of all neurons found, no extra steps
        } else {
            return generateNeuronRandom() //unless there were no neurons found, then return a randomly generated neuron
        }
    } //the same as generateNeuronProximityAverageAbsolute (without any random neurons at all, just the average)

    fun generateNeuronProximityPluralityProbability(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val valueMap: MutableMap<Array<Int>, Int> = mutableMapOf() //valueMap will hold a count of each type of neuron under the Array<Int> matching the dimensionalAddress passed in
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray
            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                valueMap.put(searchNeuron, valueMap.getOrDefault(searchNeuron, 0) + 1) //create or grab and increment the value for the given neuron
                 //add the neuron to valueMap
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int (-1), from the tally

        if (Random.nextDouble(0.0, neuronTypes.sum().toDouble()) > neuronTypes[0]/neuronTypes.sum().toDouble()) { //a bit of randomness based on the % good/bad neurons gathered
            return (valueMap.maxByOrNull{it.value} ?: return generateNeuronRandom()).key //this was the most-seen neuron, return it (or return a random one if no neuron was seen)
        } else {
            return generateNeuronRandom() //there weren't many good neurons around this one, make a random one to test
        }
    } //generateNeuronProximityPlurality returns the most-seen neuron around it instead of making a new neuron as an average (or has a chance of returning a random neuron if there wasn't enough data)

    fun generateNeuronProximityPluralityAbsolute(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val valueMap: MutableMap<Array<Int>, Int> = mutableMapOf() //valueMap will hold a count of each type of neuron under the Array<Int> matching the dimensionalAddress passed in
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray
            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                valueMap.put(searchNeuron, valueMap.getOrDefault(searchNeuron, 0) + 1) //create or grab and increment the value for the given neuron
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int (-1), from the tally

        if (neuronTypes[1]>=neuronTypes[0]) { //removed probability, an equal amount of full and empty neurons defers to the full ones
            return (valueMap.maxByOrNull{it.value} ?: return generateNeuronRandom()).key //this was the most-seen neuron, return it (or return a random one if no neuron was seen)
        } else {
            return generateNeuronRandom() //there weren't many good neurons around this one, make a random one to test
        }
    } //the same as generateNeuronPluralityProbability (without the chance in choosing if there was enough data or not)

    fun generateNeuronProximityPlurality(dimensionalAddress: Array<Int>, searchGranularity: Int): Array<Int> {
        if (searchGranularity < 1) { return generateNeuronRandom() }
        val neuronTypes = Array(2){0}
        val valueMap: MutableMap<Array<Int>, Int> = mutableMapOf() //valueMap will hold a count of each type of neuron under the Array<Int> matching the dimensionalAddress passed in
        val bubbleArray = Array(dimensions.size){(2*searchGranularity)+1} //bubble refers to the shape :) this defines the size and dimensions of the search bubble

        for (k in 0 until multiplyArray(bubbleArray)) {
            val searchArray = getDimensional(k, bubbleArray) //searchArray is constructed with coordinates for each point as defined by bubbleArray
            for (r in searchArray.indices) {
                searchArray[r]-=searchGranularity //the coordinates need to be shifted so they are centered on the seed
            }

            val searchAddress = getLinear(dimensionalAddress) + getLinear(searchArray) //overlay the seed with searchArray to move the search neuron
            if (searchAddress<0 || searchAddress>numberOfNeurons-1) {
                continue //check for extraneous searches :P fencepost issue on numberOfNeurons
            }

            val searchNeuron = brain[searchAddress]
            if (searchNeuron[0]<0) {
                neuronTypes[0]+=1 //this neuron is empty
            } else {
                neuronTypes[1]+=1 //this neuron is full
                valueMap.put(searchNeuron, valueMap.getOrDefault(searchNeuron, 0) + 1) //create or grab and increment the value for the given neuron
            }
        }
        neuronTypes[0]-=1 //remove the seed (dimensionalAddress), which is always an Int (-1), from the tally

        return (valueMap.maxByOrNull{it.value} ?: return generateNeuronRandom()).key //complete plurality of all neurons found, no extra steps (unless there was no plurality, then return a randomly generated neuron)
    }
}