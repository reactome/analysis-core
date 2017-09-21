package org.reactome.server.analysis.core.importer.query;

import java.util.Collections;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ReferenceEntityIdentifiers {

    private Long referenceEntity;
    private List<String> secondaryIdentifiers;
    private List<String> geneNames;
    private List<String> otherIdentifiers;
    private List<XRef> xrefs;

    public ReferenceEntityIdentifiers() { }

    public Long getReferenceEntity() {
        return referenceEntity;
    }

    public void setReferenceEntity(Long referenceEntity) {
        this.referenceEntity = referenceEntity;
    }

    public List<String> getSecondaryIdentifiers() {
        return secondaryIdentifiers == null ? Collections.EMPTY_LIST : secondaryIdentifiers;
    }

    public void setSecondaryIdentifiers(List<String> secondaryIdentifier) {
        this.secondaryIdentifiers = secondaryIdentifier ;
    }

    public List<String> getGeneNames() {
        return geneNames == null ? Collections.EMPTY_LIST : geneNames;
    }

    public void setGeneNames(List<String> geneName) {
        this.geneNames = geneName;
    }

    public List<String> getOtherIdentifiers() {
        return otherIdentifiers == null ? Collections.EMPTY_LIST : otherIdentifiers;
    }

    public void setOtherIdentifiers(List<String> otherIdentifiers) {
        this.otherIdentifiers = otherIdentifiers;
    }

    public List<XRef> getXrefs() {
        return xrefs;
    }

    public void setXrefs(List<XRef> xrefs) {
        this.xrefs = xrefs;
    }
}
