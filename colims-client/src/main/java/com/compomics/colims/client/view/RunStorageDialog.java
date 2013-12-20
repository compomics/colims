/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view;

import com.compomics.colims.client.util.InputValidator;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jmol.export.dialog.FileChooser;
import com.compomics.colims.distributed.storage.enums.StorageType;
import com.compomics.colims.distributed.storage.incoming.ClientForStorageConnector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class RunStorageDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = Logger.getLogger(RunStorageDialog.class);
    private File fastaFile = new File(System.getProperty("user.home") + "/.compomics/dummy");
    private File sourceInputFile = new File(System.getProperty("user.home") + "/.compomics/dummy");
    private File mgfFile = new File(System.getProperty("user.home") + "/.compomics/dummy");
    private StorageType type = StorageType.PEPTIDESHAKER;
    private String userName;
    private long sampleID;
    private String instrument;
    private final InputValidator validator = new InputValidator();

    /**
     * Creates new form RunStorageDialog
     */
    public RunStorageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        buttonGroup1.add(jRbMaxQuant);
        buttonGroup1.add(jRbPeptideShaker);
        jRbPeptideShaker.setSelected(true);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getSampleID() {
        return sampleID;
    }

    public void setSampleID(long sampleID) {
        this.sampleID = sampleID;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }  
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblInputCheck = new javax.swing.JLayeredPane();
        btnCps = new javax.swing.JButton();
        btnMGF = new javax.swing.JButton();
        lbMgf = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRbPeptideShaker = new javax.swing.JRadioButton();
        lbInputSource = new javax.swing.JLabel();
        jRbMaxQuant = new javax.swing.JRadioButton();
        tfInputFolder = new javax.swing.JTextField();
        tfMgf = new javax.swing.JTextField();
        btnFasta = new javax.swing.JButton();
        tfFasta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnStore = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lbFastaCheck = new javax.swing.JLabel();
        lbMgfCheck = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        lblInputCheck.setBackground(new java.awt.Color(255, 255, 255));

        btnCps.setText("...");
        btnCps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCpsActionPerformed(evt);
            }
        });

        btnMGF.setText("...");
        btnMGF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMGFActionPerformed(evt);
            }
        });

        lbMgf.setText("MGF");

        jLabel3.setText("Fasta");

        jRbPeptideShaker.setText("PeptideShaker");
        jRbPeptideShaker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRbPeptideShakerActionPerformed(evt);
            }
        });

        lbInputSource.setText("Peptideshaker file");

        jRbMaxQuant.setText("MaxQuant");
        jRbMaxQuant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRbMaxQuantActionPerformed(evt);
            }
        });

        btnFasta.setText("...");
        btnFasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFastaActionPerformed(evt);
            }
        });

        tfFasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfFastaActionPerformed(evt);
            }
        });

        jLabel1.setText("Type ");

        btnStore.setText("Store");
        btnStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStoreActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("*");

        lbFastaCheck.setForeground(new java.awt.Color(255, 51, 51));
        lbFastaCheck.setText("*");

        lbMgfCheck.setForeground(new java.awt.Color(255, 51, 51));
        lbMgfCheck.setText("*");

        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("*");

        jLabel7.setText("mandatory file or folder");

        javax.swing.GroupLayout lblInputCheckLayout = new javax.swing.GroupLayout(lblInputCheck);
        lblInputCheck.setLayout(lblInputCheckLayout);
        lblInputCheckLayout.setHorizontalGroup(
            lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblInputCheckLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblInputCheckLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnStore))
                    .addGroup(lblInputCheckLayout.createSequentialGroup()
                        .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lblInputCheckLayout.createSequentialGroup()
                                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(lbInputSource)
                                    .addComponent(lbMgf))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbMgfCheck)
                                    .addComponent(jLabel2)
                                    .addComponent(lbFastaCheck)))
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lblInputCheckLayout.createSequentialGroup()
                                .addComponent(jRbPeptideShaker)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRbMaxQuant)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(lblInputCheckLayout.createSequentialGroup()
                                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfFasta, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                                    .addComponent(tfInputFolder)
                                    .addComponent(tfMgf, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnMGF)
                                    .addComponent(btnCps)
                                    .addComponent(btnFasta))))))
                .addContainerGap())
        );

        lblInputCheckLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, lbInputSource, lbMgf});

        lblInputCheckLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCps, btnFasta, btnMGF});

        lblInputCheckLayout.setVerticalGroup(
            lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblInputCheckLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRbPeptideShaker)
                    .addComponent(jRbMaxQuant))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfFasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFasta)
                    .addComponent(lbFastaCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbInputSource)
                    .addComponent(tfInputFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCps)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbMgf)
                        .addComponent(lbMgfCheck))
                    .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfMgf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnMGF)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(lblInputCheckLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStore)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)))
        );

        lblInputCheckLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tfFasta, tfInputFolder, tfMgf});

        lblInputCheckLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCps, btnFasta, btnMGF});

        lblInputCheck.setLayer(btnCps, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(btnMGF, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(lbMgf, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jRbPeptideShaker, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(lbInputSource, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jRbMaxQuant, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(tfInputFolder, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(tfMgf, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(btnFasta, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(tfFasta, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(btnStore, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(lbFastaCheck, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(lbMgfCheck, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lblInputCheck.setLayer(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInputCheck))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInputCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRbMaxQuantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRbMaxQuantActionPerformed
        lbInputSource.setText("Quant folder");
        lbMgfCheck.setVisible(false);
        type = StorageType.MAX_QUANT;
    }//GEN-LAST:event_jRbMaxQuantActionPerformed

    private void jRbPeptideShakerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRbPeptideShakerActionPerformed
        lbInputSource.setText("Peptideshaker File");
        lbMgfCheck.setVisible(true);
        type = StorageType.PEPTIDESHAKER;
    }//GEN-LAST:event_jRbPeptideShakerActionPerformed

    private void tfFastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFastaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFastaActionPerformed

    private void btnFastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFastaActionPerformed
        JFileChooser fc = new FileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getAbsolutePath().toLowerCase().endsWith(".fasta"));
            }

            @Override
            public String getDescription() {
                return "The Fasta file used to run this project";
            }
        });
        fc.setCurrentDirectory(fastaFile.getParentFile());
        fc.showDialog(this, "Select");
        fastaFile = fc.getSelectedFile();
        tfFasta.setText(fastaFile.getAbsolutePath());
    }//GEN-LAST:event_btnFastaActionPerformed

    private void btnCpsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCpsActionPerformed
        if (jRbPeptideShaker.isSelected()) {
            JFileChooser fc = new FileChooser();
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getAbsolutePath().toLowerCase().endsWith(".cps"));
                }

                @Override
                public String getDescription() {
                    return "The cps file used to run this project";
                }
            });
            fc.setCurrentDirectory(sourceInputFile.getParentFile());
            fc.showDialog(this, "Select");
            sourceInputFile = fc.getSelectedFile();
        } else if (jRbMaxQuant.isSelected()) {
            //TODO ALLOW DIRECTORIES HERE
            JFileChooser fc = new FileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "The cps file used to run this project";
                }
            });
            fc.setCurrentDirectory(sourceInputFile.getParentFile());
            fc.showDialog(this, "Select");
            sourceInputFile = fc.getSelectedFile();
        }
        tfInputFolder.setText(sourceInputFile.getAbsolutePath());
    }//GEN-LAST:event_btnCpsActionPerformed

    private void btnMGFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMGFActionPerformed
        JFileChooser fc = new FileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getAbsolutePath().toLowerCase().endsWith(".mgf"));
            }

            @Override
            public String getDescription() {
                return "The mgf file used to run this project";
            }
        });
        fc.setCurrentDirectory(mgfFile.getParentFile());
        fc.showDialog(this, "Select");
        mgfFile = fc.getSelectedFile();
        tfMgf.setText(mgfFile.getAbsolutePath());
    }//GEN-LAST:event_btnMGFActionPerformed


    private void btnStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStoreActionPerformed
        try {
            //bundle all files in a tempfolder on a nas
            List<File> fileList = new ArrayList<File>();

            File fasta = new File(tfFasta.getText());
            File mgf = new File(tfMgf.getText());
            File inputSource = new File(tfInputFolder.getText());

            if (fasta.exists()) {
                fileList.add(fasta);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid fasta file... ",
                        "Error submitting to storagecontroller",
                        JOptionPane.ERROR_MESSAGE);
            }

            if (inputSource.exists()) {
                fileList.add(inputSource);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid input file or folder... ",
                        "Error submitting to storagecontroller",
                        JOptionPane.ERROR_MESSAGE);
            }

            if (mgf.exists()) {
                fileList.add(mgf);
            } else {
                if (!type.equals(StorageType.MAX_QUANT)) {
                    if (!tfMgf.getText().isEmpty()) {
                        int dialogButton = JOptionPane.YES_NO_OPTION;
                        int dialogResult = JOptionPane.showConfirmDialog(this, "An invalid mgf was selected for storage. Do you want to review this?", "Suspicious mgf file", dialogButton);
                        if (dialogResult == 0) {
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Invalid mgf file... ",
                                "Error submitting to storagecontroller",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            File storageFile = validator.putFilesInTempFolder(fileList, userName);
            //connect to the storage controller with the required parameters...
            ClientForStorageConnector connector = new ClientForStorageConnector();
            connector.storeFile(userName, storageFile.getAbsolutePath(), sampleID, instrument, type);
            JOptionPane.showMessageDialog(this,
                    "Your project has been planned to be stored in colims shortly.",
                    "Succesfully scheduled",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            LOGGER.error(ex);
            JOptionPane.showMessageDialog(this,
                    "An error occurred while submitting : " + ex.getMessage(),
                    "Error submitting to storagecontroller",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnStoreActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCps;
    private javax.swing.JButton btnFasta;
    private javax.swing.JButton btnMGF;
    private javax.swing.JButton btnStore;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRbMaxQuant;
    private javax.swing.JRadioButton jRbPeptideShaker;
    private javax.swing.JLabel lbFastaCheck;
    private javax.swing.JLabel lbInputSource;
    private javax.swing.JLabel lbMgf;
    private javax.swing.JLabel lbMgfCheck;
    private javax.swing.JLayeredPane lblInputCheck;
    private javax.swing.JTextField tfFasta;
    private javax.swing.JTextField tfInputFolder;
    private javax.swing.JTextField tfMgf;
    // End of variables declaration//GEN-END:variables

}
