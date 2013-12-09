package com.compomics.colims.client.view;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author niels
 */
public class HomePanel_old extends javax.swing.JPanel {

    /**
     * Creates new form HomePanel
     */
    public HomePanel_old() {
        initComponents();
    }

    public JList getAnalyticalRunJList() {
        return analyticalRunJList;
    }

    public JList getExperimentJList() {
        return experimentJList;
    }

    public JList getProjectJList() {
        return projectJList;
    }

    public JList getSampleJList() {
        return sampleJList;
    }

    public JTable getSpectrumJTable() {
        return spectrumJTable;
    }

    public JPanel getSpectrumDetailPanel() {
        return spectrumDetailPanel;
    }        
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        topPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        analyticalRunJList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        sampleJList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        projectJList = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        experimentJList = new javax.swing.JList();
        spectrumPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        spectrumJTable = new javax.swing.JTable();
        spectrumDetailPanel = new javax.swing.JPanel();

        jScrollPane2.setViewportView(jEditorPane1);

        setOpaque(false);

        topPanel.setOpaque(false);

        jScrollPane1.setOpaque(false);

        analyticalRunJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Analytical Runs"));
        jScrollPane1.setViewportView(analyticalRunJList);

        jScrollPane4.setOpaque(false);

        sampleJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Samples"));
        jScrollPane4.setViewportView(sampleJList);

        jScrollPane5.setOpaque(false);

        projectJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Projects"));
        jScrollPane5.setViewportView(projectJList);

        jScrollPane6.setOpaque(false);

        experimentJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Experiments"));
        jScrollPane6.setViewportView(experimentJList);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        spectrumPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Spectra"));
        spectrumPanel.setOpaque(false);

        jScrollPane7.setOpaque(false);

        spectrumJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(spectrumJTable);

        spectrumDetailPanel.setOpaque(false);
        spectrumDetailPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout spectrumPanelLayout = new javax.swing.GroupLayout(spectrumPanel);
        spectrumPanel.setLayout(spectrumPanelLayout);
        spectrumPanelLayout.setHorizontalGroup(
            spectrumPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spectrumPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectrumDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
                .addContainerGap())
        );
        spectrumPanelLayout.setVerticalGroup(
            spectrumPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spectrumDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spectrumPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spectrumPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList analyticalRunJList;
    private javax.swing.JList experimentJList;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JList projectJList;
    private javax.swing.JList sampleJList;
    private javax.swing.JPanel spectrumDetailPanel;
    private javax.swing.JTable spectrumJTable;
    private javax.swing.JPanel spectrumPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
