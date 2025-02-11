package org.reactome.server.analysis.core.model;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DatabaseInfo {

    private String name;
    private Integer version;
    private Long checksum;

    public DatabaseInfo(String name, Integer version, Long checksum) {
        this.name = name;
        this.version = version;
        this.checksum = checksum;
    }

    public String getName() {
        return name;
    }

    public Integer getVersion() {
        return version;
    }

    public Long getChecksum() {
        return checksum;
    }
}
