package com.compomics.colims.client.renderer;

import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermProperty;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermSummaryCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        CvTermSummaryListModel cvTermSummaryListModel = (CvTermSummaryListModel) list.getModel();

        String labelText;
        
        //get the selected CvTermProperty
        CvTermProperty cvTermProperty = (CvTermProperty) cvTermSummaryListModel.getElementAt(index);
        //check if the CV term is single or multiple
        if (cvTermSummaryListModel.isSingleCvTerm(cvTermProperty)) {
            CvTerm cvTerm = cvTermSummaryListModel.getSingleCvTerms().get(cvTermProperty);
            if (cvTerm != null) {
                labelText = cvTermProperty.toString() + " (1/1)";
            } else {
                labelText = cvTermProperty.toString() + " (0/1)";
            }
        } else {
            List<? extends CvTerm> cvTerms = cvTermSummaryListModel.getMultipleCvTerms().get(cvTermProperty);
            labelText = cvTermProperty.toString() + " (" + cvTerms.size() + ")";
        }

        setText(labelText);

        return this;
    }
}
