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

    /**
     * No-arg constructor.
     */
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
     * @param cvParamType the CV param type
     * @param t the T instance
     */
    public void updateSingleCvParam(final CvParamType cvParamType, final T t) {
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
    public void updateMultiCvParam(final CvParamType cvParamType, final List<T> cvParams) {
        multiCvParams.put(cvParamType, cvParams);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Update the model with EnumMaps for the single and multiple CV params.
     *
     * @param singleCvParams the map of single CV params
     * @param multiCvParams the map of multi CV params
     */
    public void update(final EnumMap<CvParamType, T> singleCvParams, final EnumMap<CvParamType, List<T>> multiCvParams) {
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
    public boolean isSingleCvParam(final CvParamType cvParamType) {
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
    public Object getElementAt(final int index) {
        Object element;
        if (singleCvParamKeys == null || multiCvParamKeys == null) {
            throw new IllegalStateException("Keys array cannot be null.");
        }
        if (index < singleCvParamKeys.length) {
            element = singleCvParamKeys[index];
        } else {
            element = multiCvParamKeys[index - singleCvParamKeys.length];
        }
        return element;
    }
}
