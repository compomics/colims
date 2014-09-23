package com.compomics.colims.client.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.EnumMap;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Niels Hulstaert
 * @param <T>
 */
public class TypedCvParamSummaryListModel<T extends AuditableTypedCvParam> extends AbstractListModel {

    /**
     * The EnumMap containing the single CV params (key: cvParamType; value: a
     * single CV param or null).
     */
    private EnumMap<CvParamType, T> singleCvParams;
    /**
     * The EnumMap containing the multiple CV params (key: cvParamType; value: a
     * list of multiple CV params, can be empty).
     */
    private EnumMap<CvParamType, List<T>> multiCvParams;
    /**
     * The single CV param key array for indexing purposes.
     */
    private CvParamType[] singleCvParamKeys;
    /**
     * The multiple CV param key array for indexing purposes.
     */
    private CvParamType[] multiCvParamKeys;

    public TypedCvParamSummaryListModel() {
        singleCvParams = new EnumMap<>(CvParamType.class);
        multiCvParams = new EnumMap<>(CvParamType.class);
    }

    public EnumMap<CvParamType, T> getSingleCvParams() {
        return singleCvParams;
    }

    public EnumMap<CvParamType, List<T>> getMultiCvParams() {
        return multiCvParams;
    }

    /**
     * Update the model with EnumMaps for the given CV param type of a single CV
     * param. The CV param instance can be null.
     *
     * @param cvParamType
     * @param t
     */
    public void updateSingleCvParam(CvParamType cvParamType, T t) {
        singleCvParams.put(cvParamType, t);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Update the model with EnumMaps for the given CV param type of a multiple
     * CV param. The CV param list can be empty.
     *
     * @param cvParamType the CvParamType
     * @param cvParams the list of CV params
     */
    public void updateMultiCvParam(CvParamType cvParamType, List<T> cvParams) {
        multiCvParams.put(cvParamType, cvParams);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Update the model with EnumMaps for the single and multiple CV params.
     *
     * @param singleCvParams
     * @param multiCvParams
     */
    public void update(EnumMap<CvParamType, T> singleCvParams, EnumMap<CvParamType, List<T>> multiCvParams) {
        this.singleCvParams = singleCvParams;
        this.multiCvParams = multiCvParams;
        singleCvParamKeys = singleCvParams.keySet().toArray(new CvParamType[singleCvParams.size()]);
        multiCvParamKeys = multiCvParams.keySet().toArray(new CvParamType[multiCvParams.size()]);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Check if the given cvParamType can hold multiple CV param values or just
     * one.
     *
     * @param cvParamType the CvParamType
     * @return is it a single CV param
     */
    public boolean isSingleCvParam(CvParamType cvParamType) {
        boolean isSingleCvParam = false;
        if (singleCvParams.containsKey(cvParamType)) {
            isSingleCvParam = true;
        }
        return isSingleCvParam;
    }

    @Override
    public int getSize() {
        return singleCvParams.size() + multiCvParams.size();
    }

    @Override
    public Object getElementAt(int index) {
        Object element;
        if (index < singleCvParamKeys.length) {
            element = singleCvParamKeys[index];
        } else {
            element = multiCvParamKeys[index - singleCvParamKeys.length];
        }
        return element;
    }
}
