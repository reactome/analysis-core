# Extended Reactome Analysis tool

## Definitions

A __proteoform__ is a protein with a set of post translational modifications.

A __protein__ is identified by a standard protein identifier such as UniProt accession or Ensembl.
A _post-translational modification_(PTM) is uniquely identified by two elements: type and coordinate in the protein sequence.

The PTM __type__ is specified following the PSI-MOD ontology. No other types are supported. 

The PTM __coordinate__ is an integer number indicating the position in the protein amino acid sequence where the post-translational modification occur.

__Matching__ is deciding if an input proteoform corresponds to a proteoform in the database.
An input proteoform can be connected to 0 or more stored proteoforms. They can match perfectly or with a margin of error. 

A PTM __unkown coordinate__ in the input is specified by an integer 0 or null. 

A __perfect PTM match__ is when two PTMs have the exact same coordinate and type. 

A __perfect proteoform match__ is when an input proteoform and a stored proteoform fullfill these conditions: 
 - The protein accession is the same.
 - The isoform is the same. If no isoform is specified, the canonical is taken. 
 - For each PTM in the input, there is perfect PTM match in the stored proteoform.

Having an integer _n_. A __range__ for PTM coordinates is the set of protein sequence coordinates that fall in the closed interval [n-1, n+1].

An __imperfect match__ is when an input proteoform and a stored proteoform fullfill these conditions: 
- The protein accession is the same.
- The isoform is the same.
- At least one input PTM has no perfect match in the stored proteoform. 
For each PTM in the input, there is a PTM in the stored proteoform with:
    - The input PTM type is less specific than the stored PTM, using the PSI-MOD hierarchy (the input type is a parent of the stored type). 
    - The coordinates fulfill at least one of these conditions:
        - Either the input PTM coordinate falls in the range of the stored PTM coordinate.
        - Or the input PTM coordinate is unknown.

An input proteoform is not matched (not considered found) when at least one of the following conditions happen:
- The identifier is not found in Reactome.
- The specific isoform is not found in Reactome.
- One input PTM has no perfect or imperfect match:
    - At least one input PTM is more specific than the stored PTMs. In other words, the input PTM type is a child of the stored PTM type in the ontology hierarchy tree.
    - At least one input PTM with the same coordinate was not found in the stored proteoform.

# Knowledge annotated in Reactome

### How are subsequence ranges annotated in Reactome
Proteins are commonly processed with cleaving or modifications in order to perform their functions. Very often they do not stay complete the way they are right after translation. For example, the initial residue is removed, because it is just the starting signal of the protein and all of them share that initial methionine. Sometimes, also peptide regions have to be removed so that the protein can actually perform its task. In other situations, only a subsequence of the protein is necessary to perform the task, then it is necessary to specify the range of the subsequence with respect to the full sequence.

By default the sequences refer to the canonical sequence of the proteins. In case they refer to other variants of the sequence then, the isoform is specified. 

The subsequences of the proteins are represented by the class "EntityWithAccessionedSequence" (_ewas_) in Reactome. It always has "startCoordinate" and "endCoordinate" fields to specify the start and end amino acids in the full sequence of the protein. When the whole protein sequence is meant, then either the start and end are not specified or the coordinates of the start and end positions of the full sequence are annotated. In some cases, either the start or end coordinate are not known, therefore they are left blank. The display name of the _ewas_ will contain in parenthesis the subsequence range.


### How are Isoforms annotated in Reactome

The isoform number 1 is the main isoform of the protein, which is selected by UniProt. The isoforms annotated in Reactome are not updated regularly. Therefore, in case the isoform numbering changes in UniProt, the annotated isoforms in Reactome might diverge from the current ones.

When a protein has only one version then there is no need to specify the isoform. The default isoform is "-1", which is often ommited. When an isoform is specified, it means only that isoform can perform the function it is supposed to do. In those cases, even the isoform "-1" will be annotated. Otherwise, there is no need to annotate it. This applies also for the proteins having ine or more isoforms.

// TODO: Create a script to perform a check in Reactome if the coordinates of the subsequence ranges and full sequence coordinates match the ones of the actual sequences in UniProt. Extend to check also the ptm coordinates. In case there are some discrepancies, download the history of the protein from UniProt and check if the protein was really annotated according to the sequence by that time. OUTPUT the list of 
corrections needed to be made in order to keep the information matched.

# Overall analysis process

## How was it?

    ### Input

    ### Search and Matching

    ### Statistical Analysis

## How is it now?

    ### Input

    ### Search and Matching

    ### Statistical Analysis

## Input Formats

### Identifiers list

#### Gene name list
#### UniProt accession list
#### Gene NCBI / Entrez list
#### Small molecules (ChEBI)
#### Small molecules (KEGG)
#### Simple Proteoforms list

### Expression values
* Files can be in one line or multiple lines.
* File contains multiple columns. The first column in each row contains the identifier. All the other columns must contain
expression values as floating point numbers.


* Identifiers can be repeated in multiple lines?

#### Microarray data
#### Metabolomics data
#### Cancer Gene Census
#### Simple proteoforms list with expression values

### Other formats
#### PEFF
#### Protein Ontology
#### GPMDB


#### Simple proteoform list

Each line of the file corresponds to a single proteoform.
A line consists of two fields separated by ';'. First a Uniprot Accession and second a set of PTMs.
The second field is optional. Lacking a PTM set means a proteoform without modifications.
The PTM set specifies presents each PTM separated by a ','. 
Each PTM is specified using a modification identifier and an integer coordinate, separated by ':'(semicolon). 
The modification identifier is a 5 digit id from the Protein Modification Onthology [\[2\]](#references).
For example: "133:00046, which corresponds to [O-phospho-L-serine](https://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FMOD_00046) at the coordinate 133. 

Single proteoform examples:
- A single protein with no modifications
~~~~
P00519
~~~~
- A protein with one PTM. The two fields are separated by a ';'
~~~~
P16220;133:00046
~~~~
- A protein and a set of PTMs separated by ','. The PTMs can be ordered randomly.
~~~~
P62753;235:00000,236:00000,240:00000
~~~~
In case the PTM type is not known, the modification id used is "00000". For example: "00000:245".
In case the PTM coordinate is not known, the integer used is 0.

<br>File example:
~~~~
P10412-1
P10412
P56524;559:00916
P04637;370:00084,382:00084
P56524;246:00916,467:00916,632:00916
P12345-2;246:00916,467:00916,632:00916
Q1AAA9
O456A1
P4A123
A0A022YWF9
A0A022YWF9;246:00916,467:00916,632:00916
~~~~

#### Protein Ontology







#### PEFF (TODO)

#### GPMDB (TODO)

### Implementation (TODO)

The input type is decided using the first 5 lines of the file, without counting headers.

An Ensembl identifier will be mapped to UniProt accession. Ensembl is only allowed in the GPMDB format.

PTMS are stored as radix tree leaves attributes.
Save them as a sorted set ordered by mod type and then coordinate. 
Because PTMs with the same type appear in more than one coordinate, but, theoretically, 
one coordinate can not contain more than one PTM at once. 

Sort the PTMs in the input, by mod and then by coordinate.

First search for the protein accession in the radix tree.
Then find the

An unknown PTM coordinate is stored as null, to avoid counting the 0 in the range of near coordinates.

### Analysis

#### Input reader

### Matching

First, the proteins are filtered to only those with that UniProt accession.
Second, proteins are filtered by isoform.
Third, proteins are filtered by subsequence ranges.
Fourth, the set of ptms is matched.

How it is annotated in Reactome?
There are ReferenceEntity 

## Intermediate data structure

## Results analysis

# Improvements

* Use Google Guava Stopwatch class to measure input preprocessing performance.
    
    It is based on the System.nanoTime(), instead of System.currentTimeMillis(), 
    which measures the elapsed wall-clock time. In contrast, System.nanoTime() returns the current value of the most 
    precise available system timer, which is specifically developed to measure elapsed time.
    
 * Faster reading of expression input.
  
  Traverse the input fewer times. Currently, it is traversing all input at least 3 times using:
 split, replaceAll, and StringTokenizer. 
 
 * Added possibility to specify proteoforms.
    * Add support for PEFF format.
    * Added support for Protein Ontology (PRO) format.
    * Added support for a simple custom proteoform format.

# References
\[1\] [UniProt: the universal protein knowledgebase. Nucleic Acids Res. 45: D158-D169 (2017)](http://dx.doi.org/doi:10.1093/nar/gkw1099) <br>
\[2\] [The PSI-MOD community standard for representation of protein modification data. Nature Biotechnology 26, 864 - 866 (2008)](http://www.nature.com/nbt/journal/v26/n8/full/nbt0808-864.html) <br>


# Reactome queries

## Get all protein modification types from PSI-MOD annotated in Reactome.
~~~~
MATCH (n:PsiMod) 
WHERE n.databaseName = "MOD"
RETURN n.identifier, n.name
~~~~
## Number of times a ptm was annotated to a human protein sequence
~~~~
MATCH (re:ReferenceEntity)<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)
WHERE re.databaseName = 'UniProt' AND ewas.speciesName = 'Homo sapiens'
WITH re, ewas
MATCH (ewas)-[:hasModifiedResidue]->(mr:TranslationalModification)-[:psiMod]->(mod:PsiMod)
RETURN DISTINCT mod.identifier as Identifier, mod.name as Name, count(ewas) as AnnotationTimes
ORDER by AnnotationTimes DESC
~~~~
## Get all Reactome proteins
~~~~
MATCH (pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity)
WHERE pe.speciesName = "Homo sapiens" AND re.databaseName = "UniProt"
RETURN DISTINCT re.identifier as proteinAccession
ORDER BY proteinAccession
~~~~
## Get all Reactome proteins with isoform distinction
~~~~
MATCH (pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity)
WHERE pe.speciesName = "Homo sapiens" AND re.databaseName = "UniProt"
RETURN DISTINCT (CASE WHEN size(re.variantIdentifier) > 0 THEN re.variantIdentifier ELSE re.identifier END) as proteinAccession
ORDER BY proteinAccession
~~~~
## Get proteoforms of a protein
~~~~
MATCH (pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity{identifier:{id}})
WHERE pe.speciesName = "Homo sapiens" AND re.databaseName = "UniProt"
WITH DISTINCT pe, re
OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(mod:PsiMod)
WITH DISTINCT pe,
                (CASE WHEN size(re.variantIdentifier) > 0 THEN re.variantIdentifier ELSE re.identifier END) as proteinAccession,
                tm.coordinate as coordinate, 
                mod.identifier as type ORDER BY type, coordinate
WITH DISTINCT pe, 
				proteinAccession,
                COLLECT(type + ":" + CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE "null" END) AS ptms
RETURN DISTINCT proteinAccession,  
				(CASE WHEN pe.startCoordinate IS NOT NULL AND pe.startCoordinate <> -1 THEN pe.startCoordinate ELSE "null" END) as startCoordinate, 
                (CASE WHEN pe.endCoordinate IS NOT NULL AND pe.endCoordinate <> -1 THEN pe.endCoordinate ELSE "null" END) as endCoordinate, 
                ptms
~~~~
## Get the participants of a pathway
~~~~
MATCH (p:Pathway{stId:"R-HSA-2219528"})-[:hasEvent*]->(rle:ReactionLikeEvent),
      (rle)-[:input|output|catalystActivity|entityFunctionalStatus|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity),
      (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase)
      WHERE rd.displayName = "UniProt"
RETURN DISTINCT re.dbId
~~~~
## Get the reactions that belong to a pathway
Notice that this query allows duplicates, since 
~~~~
MATCH (p:Pathway{stId:"R-HSA-2219528"})-[:hasEvent*]->(rle:ReactionLikeEvent)
RETURN rle.stId, rle.displayName
~~~~
## Get the participants of a pathway, restricting only to humans and proteins
~~~~
MATCH (p:Pathway{stId:"R-HSA-2219528"})-[:hasEvent*]->(rle:ReactionLikeEvent),
      (rle)-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity),
      (pe)-[:referenceEntity]->(re:ReferenceEntity)-[:referenceDatabase]->(rd:ReferenceDatabase)
      WHERE p.speciesName = "Homo sapiens" AND rle.speciesName = "Homo sapiens" AND rd.displayName = "UniProt"
RETURN DISTINCT rle.stId, rle.displayName, re.identifier AS Identifier, rd.displayName AS Database
~~~~
hp