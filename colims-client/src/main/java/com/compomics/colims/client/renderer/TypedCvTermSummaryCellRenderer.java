package com.compomics.colims.client.renderer;

import com.compomics.colims.client.model.TypedCvTermSummaryListModel;
import com.compomics.colims.model.TypedCvTerm;
import com.compomics.colims.model.enums.CvTermType;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Niels Hulstaert
 * @param <T>
 */
public class TypedCvTermSummaryCellRenderer<T extends TypedCvTerm> extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        TypedCvTermSummaryListModel<T> typedCvTermSummaryListModel = (TypedCvTermSummaryListModel) list.getModel();

        String labelText;
        
        //get the selected cvTermType
        CvTermType cvTermType = (CvTermType) typedCvTermSummaryListModel.getElementAt(index);
        //check if the CV term is single or multiple
        if (typedCvTermSummaryListModel.isSingleCvTerm(cvTermType)) {
            T t = typedCvTermSummaryListModel.getSingleCvTerms().get(cvTermType);
            if (t != null) {
                labelText = cvTermType.toString() + " (1/1)";
            } else {
                labelText = cvTermType.toString() + " (0/1)";
            }
        } else {
            List<T> cvTerms = typedCvTermSummaryListModel.getMultiCvTerms().get(cvTermType);
            labelText = cvTermType.toString() + " (" + cvTerms.size() + ")";
        }

        setText(labelText);

        return this;
    }
}
