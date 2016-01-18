package com.compomics.colims.distributed.io.maxquant.parsers;

/**
 * This class represents is used to identify a spectrum while parsing the MaxQuant spectrum information.
 * <p/>
 * Created by Niels Hulstaert on 18/01/16.
 */
public class SpectrumKey {

    /**
     * The id column from the msms.txt file.
     */
    private Long msmsId;
    /**
     * Key to link the msms.txt entry to the spectrum peaks in the apl file.
     */
    private String aplKey;

    public Long getMsmsId() {
        return msmsId;
    }

    public void setMsmsId(Long msmsId) {
        this.msmsId = msmsId;
    }

    public String getAplKey() {
        return aplKey;
    }

    public void setAplKey(String aplKey) {
        this.aplKey = aplKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpectrumKey that = (SpectrumKey) o;

        if (msmsId != null ? !msmsId.equals(that.msmsId) : that.msmsId != null) return false;
        return aplKey != null ? aplKey.equals(that.aplKey) : that.aplKey == null;

    }

    @Override
    public int hashCode() {
        int result = msmsId != null ? msmsId.hashCode() : 0;
        result = 31 * result + (aplKey != null ? aplKey.hashCode() : 0);
        return result;
    }
}
