# MuffinDB #

MuffinDB is a novel alternative to graph databases and graph searches that can scale of billions of triples.

The idea is simple. You use a graph to model entities and relationships. For example if Jill is a friend of Jack, you use two nodes of a graph to represent the two people and one named edge (friendOf) that connects the two nodes. This can be extended to model every other connected entities and the mutual relationships between them.

Now you need a way of persisting this graph, which may potentiall become billions of nodes and relationships and also a way to query this ("Give me all the friends of Jill who loves Football and living in Singapore"). Now this gets interesting. There are a lot of graph databases available, proprietary as well as those that use RDF as the data model. They support a variety of querying methods including SPARQL.

What I am offering here is a fresh approach to this challenge. The summary of the idea is as follws :

### 1. Convert the graph into a Map of adjacency lists ###

Our example (Jill is a friend of Jack) will give us this adjacency list :

friendOf:Jill = [ Jack ]

friendOf:Jill is the key (build using the predicate:subject pattern) and the value is a set that contains one entry Jack.
Let us say, we have one more piece of knowledge that says Jack and Joe are living in Singapore. That becomes

livesIn:Singapore = [ Jack, Joe ] 

### 2. Transduce the graph queries into Set Operations ###

To find "Friends of Jill that Live in Singapore", its just a just a "intersection" set operation that can be represented in the following form

( AND friendOf:Jill  livesIn:Singapore )

Please note that I use "prefix" notation which I find easy for writing parsers and also to stack more operatiosn recursively.

### 3. Select a shard nothing Big Data Repository ###

For scaling and performance, you may have to choose a Big Data DB. I prefer Redis. Its in-memory and supports native data structures like Sets. 

### 4. Refer to my Code ###

I have given a reference implementation where you can configure the number of shards (Berths) to break and hold your mega sets. I have also given a multithreaded implementation that will execute the operations in parallel. ## So its limitless ! ##

### 5. Other Operations ###

Apart from Set Operations, one important operation is the APPLY operation. It helps you to support magical queries such as this

"Give me all the friends of Jill who loves Football and living in Singapore and what are their preferred food"

( APPLY preferredFood: ( AND loves:Football ( AND friendOf:Jill  livesIn:Singapore ) ) )

The CFG of the query language that I support is :

GraphQuery -> SetOperationQuery
raphQuery -> ApplyOperationQuery
GraphQuery -> FilterOperationQuery
GraphQuery -> SchemaQuery
GraphQuery -> EdgeQuery

SetOperationQuery -> ( SET_OPERATION GraphQuery GraphQuery )
ApplyOperationQuery -> ( APPLY_OPERATION EDGE GraphQuery )
FilterOperationQuery -> ( FILTER_OPERATION pattern GraphQuery )
SchemaQuery -> \w+?\w+
EdgeQuery -> \w+:\w+

EDGE		 -> \w+:
SET_OPERATION -> AND|OR|DIFFERENCE
APPLY_OPERATION -> APPLY
FILTER_OPERATION -> FILTER
pattern -> \w+=\w+
