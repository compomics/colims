package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 * Object for shoehorning these values into a peptide assumption
 */
public class PeptidePosition implements UrParameter {

    private String pre, post;
    private Integer start, end;

    public Integer getStart() {
        return start;
    }
    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() { return end; }
    public void setEnd(Integer end) { this.end = end; }

    public String getPre() { return pre; }
    public void setPre(String pre) { this.pre = pre; }

    public String getPost() { return post; }
    public void setPost(String post) { this.post = post; }

    @Override
    public String getFamilyName() {
        return "maxquantpeptide";
    }

    @Override
    public int getIndex() {
        return 666;
    }
}
