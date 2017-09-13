package org.reactome.server.analysis.core.importer.query;

import org.reactome.server.analysis.core.importer.EntitiesBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReferenceEntityIdentifiers {

    private Long referenceEntity;
    private List<String> secondaryIdentifiers;
    private List<String> geneNames;
    private List<String> otherIdentifiers;
    private List<String> xrefs;

    public ReferenceEntityIdentifiers() {
    }

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
        List<XRef> rtn = new ArrayList<>();
        for (String xref : xrefs) {
            String[] aux = xref.split(EntitiesBuilder.splitter);
            rtn.add(new XRef(aux[0], aux[1]));
        }
        return rtn;
    }

    public void setXrefs(List<String> xrefs) {
        this.xrefs = xrefs;
    }
}
