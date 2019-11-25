[<img src=https://user-images.githubusercontent.com/6883670/31999264-976dfb86-b98a-11e7-9432-0316345a72ea.png height=75 />](https://reactome.org)

# Analysis Core

## What is Reactome Analysis Core?
Reactome Analysis Core creates the analysis intermediate file and it is also used as part of the AnalysisService (as a dependency).

### Creating The Analysis Intermediate File

To create the intermediate binary file, the command is as follows:

:warning:
Before creating the intermediate file, make sure this query ```MATCH (n:DBInfo) RETURN n``` returns ```reactome```

```console
java -jar analysis-core-jar-with-dependencies.jar \
      -h graph_db_host \
      -p graph_db_port \
      -u graph_db_user \
      -p graph_db_passwd \
      -o pathTO/analysis_vXX.bin
```

Add ```--verbose``` to see the building status on the screen.

Please note XX refers to the current Reactome release number. The analysis_vXX.bin file has to be copied in the 
corresponding "AnalysisService/input/" folder and then change the symlink of analysis.bin in that folder to point
to the new file.

Once the AnalysisService is restarted the new data will be used.

#### Recommendation
It is recommended to specify the initial and maximum memory allocation pool for the Java Virtual Machine

```console
-Xms2048M -Xmx5120M
```