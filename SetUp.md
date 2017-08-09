The objective of the project is to extend the pathway search or Reactome, particularly to include post translational modifications and peptides as additional  input.

Reactome provides a Graph core library to interact with the contents of the knowledgebase directly. Therefore, it is useful to set up a project with those dependencies. 

# Reactome Graph Core Project set up

Follow the Reactome tutorial called ["Quickstart Maven Archetype"](https://github.com/reactome/graph-archetype).

A reference for Maven Archetype is [here]("https://maven.apache.org/guides/introduction/introduction-to-archetypes.html").

#### Steps:

* Verify prerequisites:
  * Install JDK 8.
  * Install Maven 3:
    * Download the compressed binaries.

    Example:
    __apache-maven-3.5.0-src.zip__
    * Extract to the desired location. 
    
    Example: __"C:\Program Files\"__
    * Add Maven bin directory to the __path__ system variable. 
    
    Example: __"C:\Program Files\apache-maven-3.5.0\bin"__ 
    * Make sure the __JAVA_HOME__ variable is set up. Use __mvn -v__ to check. 
    
    Example:
    ~~~~    
    $ mvn -v
    Apache Maven 3.5.0 (ff8f5e7444045639af65f6095c62210b5713f426; 2017-04-03T20:39:06+01:00)
    Maven home: C:\Program Files\apache-maven-3.5.0
    Java version: 1.8.0_111, vendor: Oracle Corporation
    Java home: C:\Program Files\Java\jdk1.8.0_111\jre
    Default locale: en_GB, platform encoding: Cp1252
    OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"  
    ~~~~
* Get the archetype for the project in one of the following ways:
  * [Clone repository and install](https://github.com/reactome/graph-archetype#install-archetype-locally). 
  * Generate the archetype (recommended):
    * [Generate in Command line](https://github.com/reactome/graph-archetype#create-a-project)

    * Or [generate with an IDE](https://github.com/reactome/graph-archetype#create-a-new-project-in-intellij)

    #### [Generate archetype in Command line](https://github.com/reactome/graph-archetype#create-a-project)
      * Go to the desired location of the project.
      * Execute the command: 
        ~~~~
        mvn archetype:generate \
        -DarchetypeGroupId=org.reactome.maven.archetypes \
        -DarchetypeArtifactId=graph-archetype \
        -DarchetypeVersion=1.0.0-SNAPSHOT \
        -DgroupId=no.uib \
        -DartifactId=ProjectName \
        -Dversion=0.0.1 \
        -DarchetypeRepository=http://www.ebi.ac.uk/Tools/maven/repos/content/repositories/pst-release
        ~~~~
      * The output is simmilar to this:
        ~~~~
        [INFO] Scanning for projects...
        [INFO]
        [INFO] ------------------------------------------------------------------------
        [INFO] Building Maven Stub Project (No POM) 1
        [INFO] ------------------------------------------------------------------------
        [INFO]
        [INFO] >>> maven-archetype-plugin:3.0.1:generate (default-cli) > generate-sources @ standalone-pom >>>
        [INFO]
        [INFO] <<< maven-archetype-plugin:3.0.1:generate (default-cli) < generate-sources @ standalone-pom <<<
        [INFO]
        [INFO]
        [INFO] --- maven-archetype-plugin:3.0.1:generate (default-cli) @ standalone-pom ---
        [INFO] Generating project in Interactive mode
        [WARNING] Archetype not found in any catalog. Falling back to central repository.
        [WARNING] Add a repsoitory with id 'archetype' in your settings.xml if archetype's repository is elsewhere.
        [INFO] Using property: groupId = no.uib
        [INFO] Using property: artifactId = GoldenEagle
        [INFO] Using property: version = 1.0.0
        [INFO] Using property: package = no.uib
        Confirm properties configuration:
        groupId: no.uib
        artifactId: GoldenEagle
        version: 1.0.0
        package: no.uib
        Y: :
        [INFO] ----------------------------------------------------------------------------
        [INFO] Using following parameters for creating project from Archetype: graph-archetype:1.0.0-SNAPSHOT
        [INFO] ----------------------------------------------------------------------------
        [INFO] Parameter: groupId, Value: no.uib
        [INFO] Parameter: artifactId, Value: GoldenEagle
        [INFO] Parameter: version, Value: 1.0.0
        [INFO] Parameter: package, Value: no.uib
        [INFO] Parameter: packageInPathFormat, Value: no/uib
        [INFO] Parameter: package, Value: no.uib
        [INFO] Parameter: version, Value: 1.0.0
        [INFO] Parameter: groupId, Value: no.uib
        [INFO] Parameter: artifactId, Value: GoldenEagle
        [INFO] Project created from Archetype in dir: C:\tests\GoldenEagle\GoldenEagle
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESS
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 6.702 s
        [INFO] Finished at: 2017-08-09T14:35:24+01:00
        [INFO] Final Memory: 16M/224M
        [INFO] ------------------------------------------------------------------------
        ~~~~

    #### [Generate archetype with an IDE](https://github.com/reactome/graph-archetype#create-a-new-project-in-intellij)

    In the IDE select create new project from archetype an use the following parameters as indicated in the [tutorial](https://github.com/reactome/graph-archetype#create-a-project):
    * Group Id: __org.reactome.maven.archetypes__
    * Artifact Id: __graph-archetype__
    * Version: __1.0.0__
    * Repository: __http://www.ebi.ac.uk/Tools/maven/repos/content/repositories/pst-release__

    Note: After adding the archetype to Intellij, it is necessary to choose the 1.0.0-SNAPSHOT version.

* Create and IDE Maven project from __pom.xml__ in the archetype folder.

Then the project is ready to be buit and run.

### Add other dependencies

* Compomics