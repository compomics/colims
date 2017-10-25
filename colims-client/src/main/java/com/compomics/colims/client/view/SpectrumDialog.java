/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view;

import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.Peptide;
import com.compomics.util.gui.export.graphics.ExportGraphicsDialog;
import com.compomics.util.io.filefilters.MgfFileFilter;
import com.compomics.util.preferences.LastSelectedFolder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This dialog shows a peptide to spectrum match (PSM).
 *
 * @author Iain
 * @author Niels Hulstaert
 */
public class SpectrumDialog extends javax.swing.JDialog implements ActionListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumDialog.class);

    private static final String SUGGESTED_MGF_NAME = "spectrum_%s.mgf";
    /**
     * The MGF file export chooser.
     */
    private final JFileChooser mgfExportChooser = new JFileChooser();
    /**
     * The Peptide instance.
     */
    private final Peptide peptide;

    /**
     * Dialog constructor.
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     * @param peptide the Peptide instance
     */
    public SpectrumDialog(java.awt.Frame parent, boolean modal, Peptide peptide) {
        super(parent, modal);
        this.peptide = peptide;
        initComponents();

        init();
    }

    public JPanel getSpectrumPanel() {
        return spectrumPanel;
    }

    public JPanel getSecondarySpectrumPlotsPanel() {
        return secondarySpectrumPlotsPanel;
    }

    public JMenuItem getMgfExportMenuItem() {
        return mgfExportMenuItem;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String menuItemLabel = e.getActionCommand();
        LastSelectedFolder lastSelectedFolder = new LastSelectedFolder();

        if (menuItemLabel.equals(closeMenuItem.getText())) {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (menuItemLabel.equals(mgfExportMenuItem.getText())) {
            mgfExportChooser.setSelectedFile(new File(System.getProperty("user.home"), String.format(SUGGESTED_MGF_NAME, peptide.getSpectrum().getTitle()).trim()));
            int returnVal = mgfExportChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File mgfExportFile = mgfExportChooser.getSelectedFile();

                try {
                    IOUtils.write(peptide.getSpectrum().getSpectrumFiles().get(0).getContent(), mgfExportFile);
                    GuiUtils.showMessageDialog(this.getContentPane(), "MGF export confirmation", "The spectrum was successfully exported to " + mgfExportFile.getPath(), HEIGHT);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    GuiUtils.showMessageDialog(this.getContentPane(), "MGF export problem", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (menuItemLabel.equals(spectrumExportMenuItem.getText())) {
            new ExportGraphicsDialog(this, GuiUtils.getNormalIcon(), GuiUtils.getNormalIcon(), true, spectrumPanel, lastSelectedFolder);
        } else if (menuItemLabel.equals(spectrumAndPlotsExportMenuItem.getText())) {
            new ExportGraphicsDialog(this, GuiUtils.getNormalIcon(), GuiUtils.getNormalIcon(), true, spectrumSplitPane, lastSelectedFolder);
        }
    }

    /**
     * Initialize the dialog components.
     */
    private void init() {
        mgfExportChooser.setMultiSelectionEnabled(false);
        mgfExportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mgfExportChooser.setFileFilter(new MgfFileFilter());
        mgfExportChooser.setDialogTitle("MGF export");
        mgfExportChooser.setApproveButtonText("Save");
        mgfExportChooser.setApproveButtonToolTipText("Save this spectrum as an MGF file.");

        //add action listeners
        closeMenuItem.addActionListener(this);
        mgfExportMenuItem.addActionListener(this);
        spectrumExportMenuItem.addActionListener(this);
        spectrumAndPlotsExportMenuItem.addActionListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spectrumMainPanel = new javax.swing.JPanel();
        spectrumParentPanel = new javax.swing.JPanel();
        spectrumSplitPane = new javax.swing.JSplitPane();
        secondarySpectrumPlotsPanel = new javax.swing.JPanel();
        spectrumOuterPanel = new javax.swing.JPanel();
        spectrumPaddingPanel = new javax.swing.JPanel();
        spectrumPanel = new javax.swing.JPanel();
        spectrumMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        closeMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        mgfExportMenuItem = new javax.swing.JMenuItem();
        figureExportMenu = new javax.swing.JMenu();
        spectrumExportMenuItem = new javax.swing.JMenuItem();
        spectrumAndPlotsExportMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Spectrum dialog");
        setMinimumSize(new java.awt.Dimension(400, 400));

        spectrumMainPanel.setBackground(new java.awt.Color(255, 255, 255));
        spectrumMainPanel.setOpaque(false);
        spectrumMainPanel.setPreferredSize(new java.awt.Dimension(600, 400));

        spectrumParentPanel.setBackground(new java.awt.Color(255, 255, 255));

        spectrumSplitPane.setBackground(new java.awt.Color(255, 255, 255));
        spectrumSplitPane.setDividerLocation(80);
        spectrumSplitPane.setDividerSize(0);
        spectrumSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spectrumSplitPane.setPreferredSize(new java.awt.Dimension(800, 500));

        secondarySpectrumPlotsPanel.setOpaque(false);
        secondarySpectrumPlotsPanel.setLayout(new javax.swing.BoxLayout(secondarySpectrumPlotsPanel, javax.swing.BoxLayout.LINE_AXIS));
        spectrumSplitPane.setTopComponent(secondarySpectrumPlotsPanel);

        spectrumOuterPanel.setBackground(new java.awt.Color(255, 255, 255));

        spectrumPaddingPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout spectrumPaddingPanelLayout = new javax.swing.GroupLayout(spectrumPaddingPanel);
        spectrumPaddingPanel.setLayout(spectrumPaddingPanelLayout);
        spectrumPaddingPanelLayout.setHorizontalGroup(
            spectrumPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        spectrumPaddingPanelLayout.setVerticalGroup(
            spectrumPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        spectrumPanel.setBackground(new java.awt.Color(255, 255, 255));
        spectrumPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout spectrumOuterPanelLayout = new javax.swing.GroupLayout(spectrumOuterPanel);
        spectrumOuterPanel.setLayout(spectrumOuterPanelLayout);
        spectrumOuterPanelLayout.setHorizontalGroup(
            spectrumOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spectrumPaddingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spectrumPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
        );
        spectrumOuterPanelLayout.setVerticalGroup(
            spectrumOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumOuterPanelLayout.createSequentialGroup()
                .addComponent(spectrumPaddingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spectrumPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
        );

        spectrumSplitPane.setRightComponent(spectrumOuterPanel);

        javax.swing.GroupLayout spectrumParentPanelLayout = new javax.swing.GroupLayout(spectrumParentPanel);
        spectrumParentPanel.setLayout(spectrumParentPanelLayout);
        spectrumParentPanelLayout.setHorizontalGroup(
            spectrumParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spectrumSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
        );
        spectrumParentPanelLayout.setVerticalGroup(
            spectrumParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumParentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spectrumSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout spectrumMainPanelLayout = new javax.swing.GroupLayout(spectrumMainPanel);
        spectrumMainPanel.setLayout(spectrumMainPanelLayout);
        spectrumMainPanelLayout.setHorizontalGroup(
            spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 788, Short.MAX_VALUE)
            .addGroup(spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spectrumParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        spectrumMainPanelLayout.setVerticalGroup(
            spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 565, Short.MAX_VALUE)
            .addGroup(spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spectrumParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileMenu.setText("File");

        closeMenuItem.setText("Close");
        fileMenu.add(closeMenuItem);

        spectrumMenuBar.add(fileMenu);

        exportMenu.setText("Export");

        mgfExportMenuItem.setText("To MGF...");
        exportMenu.add(mgfExportMenuItem);

        figureExportMenu.setText("To Figure");

        spectrumExportMenuItem.setText("Spectrum...");
        figureExportMenu.add(spectrumExportMenuItem);

        spectrumAndPlotsExportMenuItem.setText("Spectrum And Plots...");
        figureExportMenu.add(spectrumAndPlotsExportMenuItem);

        exportMenu.add(figureExportMenu);

        spectrumMenuBar.add(exportMenu);

        setJMenuBar(spectrumMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(spectrumMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(spectrumMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu figureExportMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem mgfExportMenuItem;
    private javax.swing.JPanel secondarySpectrumPlotsPanel;
    private javax.swing.JMenuItem spectrumAndPlotsExportMenuItem;
    private javax.swing.JMenuItem spectrumExportMenuItem;
    private javax.swing.JPanel spectrumMainPanel;
    private javax.swing.JMenuBar spectrumMenuBar;
    private javax.swing.JPanel spectrumOuterPanel;
    private javax.swing.JPanel spectrumPaddingPanel;
    private javax.swing.JPanel spectrumPanel;
    private javax.swing.JPanel spectrumParentPanel;
    private javax.swing.JSplitPane spectrumSplitPane;
    // End of variables declaration//GEN-END:variables
}
