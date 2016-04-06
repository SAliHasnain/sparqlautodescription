# How self-Descriptive are SPARQL Endpoints ?
 This project computes how descriptive the endpoints could be (i.e., how much meta-data they expose) by running the complex SPARQL queries.
 
Access to hundreds of corpora on the Web of Data have been made available through SPARQL endpoints. However, previous results have shown that static descriptions of the content that these endpoints index are scarce, making the indexed dataset opaque for clients. On the one hand, SPARQL is a rich query language that can be used to gain insights into the data; with the advent of aggregates in SPARQL 1.1, powerful analytical queries can be used to summarise the content in arbitrary ways. However, it is not clear if the performance of (public) SPARQL endpoints is sufficient to answer complex analytical queries. We thus propose a set of queries that form a benchmark for the analytical features of SPARQL (1.1) and whose results additionally serve to describe the content of the endpoint.

##Public SPARQL Endpoints

List of distinct public SPARQL Endpoints listed at datahub and (BIO2RDF release 1,2,3 including mirrors) can be seen 
at: [Google Docs](https://docs.google.com/document/d/1cCpSwStoPJUd5EQeKst3JCPgvmL6vko906Sl37AqJKc/edit).

List of distinct live public SPARQL Endpoints that contributed our experiments can be seen at: [Google Docs](https://docs.google.com/document/d/1tOmuIuF84RmzV90esi80OQ4XSVqKff_1xTeKxPL4SwE/edit).

##Raw Datasets

Four Datasets namely (Drugbank, Kegg, Jamendo and Dbpedia (subset)) used during the local experiments can be downloaded at:[Google Drive](https://drive.google.com/a/insight-centre.org/file/d/0ByWkCXI5Qdo_elZ3ZjRpdGI0SzA/view).

##SPARQL Engines with Datasets

Four Engines used during Local Experiments namely (4-store, virtuoso, sesame, fuseki) with uploaded data(Drugbank, Kegg, Jamendo and Dbpedia (subset)) can be downloaded at: [Google Drive](https://drive.google.com/a/insight-centre.org/folderview?id=0ByWkCXI5Qdo_NFBhUEVGb09pcHM&usp=sharing#).

##Benchmark Queries

Query . Endpoint availability
```
SELECT * WHERE { ?s ?p ?o } LIMIT 1
```
Query . isSupported sparql1.1
```
SELECT (COUNT(*) as ?sparql1_1) WHERE { SELECT * WHERE { ?s ?p ?o } LIMIT 1 }
```
Query 1. totalNumberOfTriples (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:triples ?count } WHERE { SELECT (COUNT(*) AS ?count) WHERE { ?s ?p ?o } }
```
Query 2. totalDistinctClasses (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classes ?count} WHERE { SELECT (COUNT(DISTINCT ?o) AS ?count) WHERE { ?s a ?o } }
```
Query 3. totalDistinctProp (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:properties ?count } WHERE { SELECT (COUNT(DISTINCT ?p) AS ?count) WHERE { ?s ?p ?o } }
```
Query 4. totalDistinctSubjectNode (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:distinctSubjects ?count } WHERE { SELECT (COUNT(DISTINCT ?s ) AS ?count) 
WHERE { ?s ?p ?o } }
```
Query 5. totalDistinctObjectNode (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:distinctObjects ?count } WHERE { SELECT (COUNT(DISTINCT ?o ) AS ?count) 
WHERE { ?s ?p ?o } }
```
Query 6. listOfClassesInDataset (sparql1.0)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ] } WHERE { ?s a ?c }
```
Query 7. listOfPropertiesInDataset (sparql1.0)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:propertyPartition [ void:property ?p ] } WHERE { ?s ?p ?o }
```
Query 8. listOfPropertiesWithinClassInDataset (sparql1.0)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ; void:propertyPartition [ void:property ?p ] ] } WHERE { ?s a ?c ; ?p ?o . }
```
Query 9. numberOfTriplesPerClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ; void:triples ?count ] } WHERE { SELECT (COUNT(?p) AS ?count) ?c WHERE { ?s a ?c ; ?p ?o . } GROUP BY ?c }
```
Query 10. numberOfTriplesPerPropertyPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:propertyPartition [ void:property ?p ; void:triples ?count ] } WHERE { SELECT (COUNT(?o) AS ?count) ?p 
WHERE { ?s ?p ?o . } GROUP BY ?p }
```
Query 11. numberOfTriplesPerPropertyPartitionInsideClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ; void:propertyPartition [ void:property ?p ; void:triples ?count ] ] . } WHERE { SELECT (COUNT(?o) AS ?count) ?p 
WHERE { ?s a ?c ; ?p ?o . } GROUP BY ?c ?p }
```
Query 12.numberOfClassesPerClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:classPartition [ void:class ?c ; void:classes ?count ] } WHERE { SELECT (COUNT(DISTINCT ?c2) AS ?count) ?c 
WHERE { ?s a ?c ; a ?c2 . } GROUP BY ?c }
```
Query 13.numberOfPropertiesPerClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { 
<datasetUri> void:classPartition [ void:class ?c ; void:properties ?count ] } WHERE { SELECT (COUNT(DISTINCT ?p) AS ?count) ?c 
WHERE { ?s a ?c ; ?p ?o . } GROUP BY ?c }
```
Query 14.numberOfSubjectsPerClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { 
<datasetUri> void:classPartition [ void:class ?c ; void:distinctSubjects ?count ] } WHERE { SELECT (COUNT(DISTINCT ?s) AS ?count) ?c 
WHERE { ?s a ?c . } GROUP BY ?c }
```
Query 15.numberOfSubjectsPerPropertyPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:propertyPartition
[ void:property ?p ; void:distinctSubjects ?count ] 
} WHERE { SELECT (COUNT(DISTINCT ?s) AS ?count) ?p WHERE { ?s ?p ?o . } GROUP BY ?p }
```
Query 16.numberOfSubjectsPerPropertyPartitionInsideClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { 
<datasetUri> void:classPartition [ void:class ?c ; void:propertyPartition [ void:distinctSubjects ?count ] ] } WHERE { SELECT (COUNT(DISTINCT ?s) AS ?count) ?c ?p 
WHERE { ?s a ?c ; ?p ?o . } GROUP BY ?c ?p }
```
Query 17.numberOfObjectsPerClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { 
<datasetUri> void:classPartition [ void:class ?c ; void:distinctObjects ?count ] } WHERE { SELECT (COUNT(DISTINCT ?o) AS ?count) ?c 
WHERE { ?s a ?c ; ?p ?o } GROUP BY ?c }
```
Query 18.numberOfObjectsPerPropertyPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { <datasetUri> void:propertyPartition [ void:property ?p ; void:distinctSubjects ?count ] } WHERE { SELECT (COUNT(DISTINCT ?o) AS ?count) ?p WHERE { ?s ?p ?o . } GROUP BY ?p }
```
Query 19.numberOfObjectsPerPropertyPartitionInsideClassPartition (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT { 
<datasetUri> void:classPartition 
[ void:class ?c ; void:propertyPartition [ void:distinctObjects ?count ; void:property ?p ] ] } WHERE { SELECT (COUNT(DISTINCT ?o) AS ?count) ?c ?p WHERE { ?s a ?c ; ?p ?o . } GROUP BY ?c ?p }
```
Query 20.numberOfDistinctSubjectIRIs (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctIRIReferenceSubjects ?count } WHERE { SELECT (COUNT(DISTINCT ?s ) AS ?count) WHERE { ?s ?p ?o . FILTER(isIri(?s))} }
```
Query 21.numberOfDistinctSubjectBlankNodes (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctBlankNodeSubjects ?count } WHERE { SELECT (COUNT(DISTINCT ?s) AS ?count) WHERE { ?s ?p ?o . FILTER(isBlank(?s))} }
```
Query 22.numberOfDistinctObjectIRIs (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctIRIReferenceObjects ?count } WHERE { SELECT (COUNT(DISTINCT ?o ) AS ?count) WHERE { ?s ?p ?o . FILTER(isIri(?o))} }
```
Query 23.numberOfDistinctObjectLiteralNodes (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctLiterals ?count } WHERE { SELECT (COUNT(DISTINCT ?o ) AS ?count) 
WHERE { ?s ?p ?o . FILTER(isLiteral(?o))} }
```
Query 24.numberOfDistinctObjectBlankNodes (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctBlankNodeObjects ?count } WHERE { SELECT (COUNT(DISTINCT ?o ) AS ?count) 
WHERE { ?s ?p ?o . FILTER(isBlank(?o))} }
```
Query 25.numberOfDistinctBlankNodes (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctBlankNodes ?count } WHERE { SELECT (COUNT(DISTINCT ?b ) AS ?count) 
WHERE { 
{ ?s ?p ?b } UNION { ?b ?p ?o } FILTER(isBlank(?b)) } }
```
Query 26.numberOfDistinctIRIs (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctIRIReferences ?count } WHERE { SELECT (COUNT(DISTINCT ?u ) AS ?count) 
WHERE { 
{ ?u ?p ?o } UNION { ?s ?u ?o } UNION { ?s ?p ?u } FILTER(isIri(?u)) } }
```
Query 27.distinctRDFNodes (sparql1.1)
```
PREFIX vext: <http://ldf.fi/void-ext#> CONSTRUCT { <datasetUri> vext:distinctRDFNodes ?count } WHERE { SELECT (COUNT(DISTINCT ?n ) AS ?count) 
WHERE { 
{ ?n ?p ?o } UNION { ?s ?n ?o } UNION { ?s ?p ?n } } }
```
Query 28.schemaMapSubjectType (sparql1.1)
```
PREFIX void: <http://rdfs.org/ns/void#> PREFIX sad: <http://vocab.deri.ie/prefix#> CONSTRUCT { <datasetUri> void:propertyPartition [ void:property ?p ; sad:subjectTypes [ sad:subjectClass ?sType ; sad:distinctMembers ?count ] ] } WHERE { SELECT (COUNT(?s) AS ?count) ?p ?sType WHERE { ?s ?p ?o ; a ?sType . } GROUP BY ?p ?sType }
```
Query 29.schemaMapObjectType (sparql1.1)
```
PREFIX void: http://rdfs.org/ns/void# PREFIX sad: http://vocab.deri.ie/prefix# CONSTRUCT { void:propertyPartition [ void:property ?p ; sad:objectTypes [ sad:objectClass ?oType ; sad:distinctMembers ?count ] ] } WHERE { SELECT (COUNT(?o) AS ?count) ?p ?oType WHERE { ?s ?p ?o . ?o a ?oType . } GROUP BY ?p ?oType
```
} 
