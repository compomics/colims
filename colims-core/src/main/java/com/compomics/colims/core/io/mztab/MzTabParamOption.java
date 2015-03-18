package com.compomics.colims.core.io.mztab;

/**
 * Created by Niels Hulstaert on 18/03/15.
 */
public class MzTabParamOption {

    private static final String OPEN = "[";
    private static final String CLOSE = "]";
    private static final String SEPARATOR = ", ";

    private String ontology;
    private String name;
    private String accession;
    private String value;

    /**
     * No arg-constructor.
     */
    public MzTabParamOption() {
        value = "";
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return OPEN +
                ontology + SEPARATOR +
                name + SEPARATOR +
                accession + SEPARATOR +
                value +
                CLOSE;
    }
}
