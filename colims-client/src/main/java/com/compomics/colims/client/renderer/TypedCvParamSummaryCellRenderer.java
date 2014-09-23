package com.compomics.colims.client.renderer;

import com.compomics.colims.client.model.TypedCvParamSummaryListModel;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Niels Hulstaert
 * @param <T>
 */
public class TypedCvParamSummaryCellRenderer<T extends AuditableTypedCvParam> extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        TypedCvParamSummaryListModel<T> typedCvParamSummaryListModel = (TypedCvParamSummaryListModel) list.getModel();

        String labelText;

        //get the selected cvParamType
        CvParamType cvParamType = (CvParamType) typedCvParamSummaryListModel.getElementAt(index);
        //check if the CV param is single or multiple
        if (typedCvParamSummaryListModel.isSingleCvParam(cvParamType)) {
            T t = typedCvParamSummaryListModel.getSingleCvParams().get(cvParamType);
            if (t != null) {
                labelText = cvParamType.toString() + " (1/1)";
            } else {
                labelText = cvParamType.toString() + " (0/1)";
            }
        } else {
            List<T> cvParams = typedCvParamSummaryListModel.getMultiCvParams().get(cvParamType);
            labelText = cvParamType.toString() + " (" + cvParams.size() + ")";
        }

        setText(labelText);

        return this;
    }
}
