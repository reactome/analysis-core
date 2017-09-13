package org.reactome.server.analysis.core.model;

import java.util.Objects;

public class Modification implements Comparable {

    private Long coordinate;
    private PsiModNode psiMod;

    public Modification(Long coordinate, PsiModNode psiMod) {
        this.coordinate = coordinate;
        this.psiMod = psiMod;
    }

    public Long getCoordinate() {
        return coordinate;
    }

    public PsiModNode getPsiMod() {
        return psiMod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Modification that = (Modification) o;

        if (coordinate != null ? !coordinate.equals(that.coordinate) : that.coordinate != null) return false;
        return psiMod != null ? psiMod.equals(that.psiMod) : that.psiMod == null;
    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + (psiMod != null ? psiMod.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        Modification that = (Modification) o;
        int rtn = Objects.compare(this.coordinate != null ? this.coordinate : -1, that.coordinate != null ? that.coordinate : -1, Long::compareTo);
        if(rtn == 0){
            return this.psiMod.compareTo(((Modification) o).psiMod);
        }
        return rtn;
    }
}
