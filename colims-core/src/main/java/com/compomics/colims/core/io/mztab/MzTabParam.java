package com.compomics.colims.core.io.mztab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Hulstaert on 18/03/15.
 */
public class MzTabParam {

    private String mzTabName;
    private String userFriendlyName;
    private List<MzTabParamOption> mzTabParamOptions = new ArrayList<>();

    public MzTabParam(String mzTabName) {
        this.mzTabName = mzTabName;
    }

    public String getMzTabName() {
        return mzTabName;
    }

    public void setMzTabName(String mzTabName) {
        this.mzTabName = mzTabName;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public List<MzTabParamOption> getMzTabParamOptions() {
        return mzTabParamOptions;
    }

    public void setMzTabParamOptions(List<MzTabParamOption> mzTabParamOptions) {
        this.mzTabParamOptions = mzTabParamOptions;
    }

    public void addOption(MzTabParamOption mzTabParamOption) {
        mzTabParamOptions.add(mzTabParamOption);
    }
}
