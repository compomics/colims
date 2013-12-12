package com.compomics.colims.client.compoment;

import com.compomics.colims.model.AbstractBinaryFile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.swing.JFileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Niels Hulstaert
 */
public class BinaryFileManagementPanel extends javax.swing.JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(BinaryFileManagementPanel.class);
    private static final String ADDED = "added";
    private static final String REMOVED = "removed";
    //model
    private JFileChooser fileChooser;
    private BindingGroup bindingGroup;
    private ObservableList<AbstractBinaryFile> binaryFileBindingList;

    /**
     * Creates new form BinaryFileManagementPanel
     */
    public BinaryFileManagementPanel() {
        initComponents();
    }
    
    public void init() {
        //select only files
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //select multiple file
        fileChooser.setMultiSelectionEnabled(Boolean.TRUE);

        //init bindings
        bindingGroup = new BindingGroup();
        
        binaryFileBindingList = ObservableCollections.observableList(new ArrayList());
        JListBinding binaryFileListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, binaryFileBindingList, binaryFileList);
        bindingGroup.addBinding(binaryFileListBinding);
        
        bindingGroup.bind();
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<AbstractBinaryFile> binaryFiles = new ArrayList<>();
                
                //in response to the button click, show open dialog 
                int returnVal = fileChooser.showOpenDialog(BinaryFileManagementPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {                    
                    for (File selectedFile : fileChooser.getSelectedFiles()) {
                        try {
                            AbstractBinaryFile binaryFile = getBinaryFile(selectedFile);
                            binaryFiles.add(binaryFile);
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);                            
                        }
                    }                    
                }
                if(!binaryFiles.isEmpty()){
                    BinaryFileManagementPanel.this.firePropertyChange(ADDED, null, binaryFiles);
                }                                                
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BinaryFileManagementPanel.this.firePropertyChange(REMOVED, null, true);
            }
        });
    }

    /**
     * Clear the file list
     */
    public void clear() {
        binaryFileBindingList.clear();
    }

    /**
     * Get the binary files as a list
     *
     * @return
     */
    private List<AbstractBinaryFile> getBinaryFiles() {
        List<AbstractBinaryFile> binaryFiles = new ArrayList<>();
        for (AbstractBinaryFile binaryFile : binaryFileBindingList) {
            binaryFiles.add(binaryFile);
        }
        
        return binaryFiles;
    }

    /**
     * Make a new AbstractBinaryFile instance from the file input.
     *
     * @param file
     */
    private AbstractBinaryFile getBinaryFile(File file) throws IOException {
        AbstractBinaryFile binaryFile = new AbstractBinaryFile();
        binaryFile.setFileName(file.getName());

        //get file as byte array
        byte[] bytes = FileUtils.readFileToByteArray(file);

        //gzip the byte array
        try (ByteArrayOutputStream zippedByteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(zippedByteArrayOutputStream);) {
            gZIPOutputStream.write(bytes);
            
            gZIPOutputStream.flush();
            gZIPOutputStream.finish();
            
            binaryFile.setContent(zippedByteArrayOutputStream.toByteArray());
        }        
        
        return binaryFile;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        binaryFileListScrollPane = new javax.swing.JScrollPane();
        binaryFileList = new javax.swing.JList();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

        binaryFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        binaryFileListScrollPane.setViewportView(binaryFileList);

        deleteButton.setText("delete");
        deleteButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteButton.setPreferredSize(new java.awt.Dimension(80, 25));

        addButton.setText("add");
        addButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryFileListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(binaryFileListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList binaryFileList;
    private javax.swing.JScrollPane binaryFileListScrollPane;
    private javax.swing.JButton deleteButton;
    // End of variables declaration//GEN-END:variables
}
