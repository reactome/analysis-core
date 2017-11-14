package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.MapList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Sanchez <luis.sanchez@uib.no>
 */

/**
 * Two analysis identifers can have the same id or expression values, if they differ in the set of ptms.
 */
public class AnalysisIdentifier implements Comparable<AnalysisIdentifier> {

    private String id;
    private List<Double> exp;

    /**
     * Notice that this constructor initializes also the ptm set as empty. Then when comparing to AnalysisIdentifiers
     * it will also check that the sets contain the same elements.
     *
     * @param id
     */
    public AnalysisIdentifier(String id) {
        this.id = id;
        this.exp = new LinkedList<Double>();
        this.ptms = new MapList<>();
    }

    public AnalysisIdentifier(String id, List<Double> exp) {
        this.id = id;
        this.exp = exp;
        this.ptms = new MapList<>();
    }

    public AnalysisIdentifier(AnalysisIdentifier aux) {
        this(aux.getId());
        for (Double ev : aux.getExp()) {
            this.add(ev);
        }
    }

    public boolean add(Double value) {
        return this.exp.add(value);
    }

    public String getId() {
        return id;
    }

    public List<Double> getExp() {
        return exp;
    }

    /**
     * The data structure to store the ptm set for a proteoform. Keys are psi mod identifiers (five digits).
     * Values are lists of numbers for the coordinates of the ptms.
     * Map implemented as TreeMap to be sorted.
     */
    private MapList<String, Long> ptms;

    public MapList<String, Long> getPtms() {
        return ptms;
    }

    public void setPtms(MapList<String, Long> ptms) {
        this.ptms = ptms;
    }

    /**
     * Add a post-translational modification to the current identifier.
     * @param psiModId
     * @param coordinate
     */
    public void addPtm(String psiModId, Long coordinate){
        ptms.add(psiModId, coordinate);
    }

    @Override
    public boolean equals(Object o) {
//        System.out.println("equals: " + this + " and " + o.toString());
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisIdentifier that = (AnalysisIdentifier) o;

        //noinspection RedundantIfStatement
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        // Verify the number of ptms is equal
        if (ptms != null ? ptms.size() != that.getPtms().size() : that.getPtms() != null) return false;

        // Verify the ptms are all equal
        for (Map.Entry<String, List<Long>> mod : ptms.entrySetInternal()) {

            if (!that.getPtms().containsKey(mod.getKey())) return false;
            if( mod.getValue().size() != that.getPtms().getElements(mod.getKey()).size()) return false;
            if (!mod.getValue().containsAll(that.getPtms().getElements(mod.getKey()))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
//        System.out.println("hashCode: " + id);

        return id != null ? id.hashCode() : 0;

//        int hash = id != null ? id.hashCode() : 0;
//        hash += (this.ptms != null ? this.ptms.hashCode() : 0);
//        return hash;
    }

    @Override
    public String toString() {
        return this.id + "[" + this.ptms.toString() + "]";
    }

    @Override
    public int compareTo(AnalysisIdentifier o) {
        return this.id.compareTo(o.id);
    }
}