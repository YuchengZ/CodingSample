//var inputFile = sc.textFile("s3://zhangyuchengproject42/input/twitter_test.txt")
var inputFile = sc.textFile("s3://f15-p42/twitter-graph.txt")
var distFile = inputFile.distinct()
var allVerx = distFile.flatMap(line => line.split(" ")).distinct()

// the initial ranks
var ranks = allVerx.map(vert => (vert.toLong, 1.0.toDouble))
// all the node
var N = allVerx.count()
// create link of neighbors
var links = distFile.map(line => (line.split(" ")(0).toLong, line.split(" ")(1).toLong)).groupByKey()

var i = 0
for (i <- 1 to 10) {
    // combine neibor and rank
    var total = links.join(ranks)
    // calculate the contributes
    var contribs = total.flatMap({case (node, (neighbors, rank)) => neighbors.map(
        neighbor => (neighbor, rank/neighbors.size))
        })
    // update ranks
    ranks = contribs.reduceByKey(_+_)
    
    // calculate loss
    var dMass = ranks.reduce((p1, p2) => (1, p1._2 + p2._2))._2
    var lMass = 1 * N -dMass
    // update ranks
    var b = 0.85
    ranks = ranks.mapValues(v => ((1-b) + b * (lMass/N + v)).toDouble)
}

var result = ranks.map(user => user._1+"\t"+user._2)
result.saveAsTextFile("s3://zhangyuchengproject42/output/task3")
