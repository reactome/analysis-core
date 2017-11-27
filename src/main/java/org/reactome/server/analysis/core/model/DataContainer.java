package org.reactome.server.analysis.core.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Contains the different data structures for the binary data and also provides
 * methods to initialize the data structure after loading from file and to
 * "prepare" the data structure for serialising
 * <p/>
 * PLEASE NOTE
 * The pathway location map is kept separately because at some point splitting
 * or cloning the pathway hierarchies will be needed, so keeping a map will
 * help to perform this task making it easier and faster.
 * Linking from the physical entity graph nodes to the pathway hierarchy is an
 * option that improves the binary time but makes the splitting or cloning
 * tasks MORE difficult and slow
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DataContainer implements Serializable {
    //A double link hierarchy tree with the pathways for each species
    Map<SpeciesNode, PathwayHierarchy> pathwayHierarchies;

    public DataContainer(Map<SpeciesNode, PathwayHierarchy> pathwayHierarchies) {
        this.pathwayHierarchies = pathwayHierarchies;
    }

}