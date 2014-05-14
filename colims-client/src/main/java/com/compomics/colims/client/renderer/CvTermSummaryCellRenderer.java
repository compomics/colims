package com.compomics.colims.client.renderer;

import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.model.CvTerm;
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
public class CvTermSummaryCellRenderer<T extends CvTerm> extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        CvTermSummaryListModel<T> cvTermSummaryListModel = (CvTermSummaryListModel) list.getModel();

        String labelText;
        
        //get the selected cvTermType
        CvTermType cvTermType = (CvTermType) cvTermSummaryListModel.getElementAt(index);
        //check if the CV term is single or multiple
        if (cvTermSummaryListModel.isSingleCvTerm(cvTermType)) {
            T t = cvTermSummaryListModel.getSingleCvTerms().get(cvTermType);
            if (t != null) {
                labelText = cvTermType.toString() + " (1/1)";
            } else {
                labelText = cvTermType.toString() + " (0/1)";
            }
        } else {
            List<T> cvTerms = cvTermSummaryListModel.getMultiCvTerms().get(cvTermType);
            labelText = cvTermType.toString() + " (" + cvTerms.size() + ")";
        }

        setText(labelText);

        return this;
    }
}
