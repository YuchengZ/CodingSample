val inputFile = sc.textFile("s3://f15-p42/twitter-graph.txt")

// map with user has follower
val lines = inputFile.distinct().map(line => (line.split(" ")(1), 1))
// mao with user not follower
val lines2 = inputFile.distinct().map(line => (line.split(" ")(0), 0))

// connect two maps
val total = lines ++ lines2
val count = total.reduceByKey(_ + _).collectAsMap()

for ((k,v) <- count) printf("%s\t%d\n", k, v)
