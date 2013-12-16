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
public class BinaryFileManagementPanel<T extends AbstractBinaryFile> extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(BinaryFileManagementPanel.class);
    public static final String ADDED = "added";
    public static final String REMOVED = "removed";
    //model
    /**
     * Keep a reference to the class type for new instance creation
     */
    private Class<T> type;
    private JFileChooser fileChooser;
    private BindingGroup bindingGroup;
    private ObservableList<T> binaryFileBindingList;

    /**
     * Creates new form BinaryFileManagementPanel
     */
    public BinaryFileManagementPanel(Class<T> type) {
        this.type = type;
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
                List<T> binaryFilesToAdd = new ArrayList<>();

                //in response to the button click, show open dialog 
                int returnVal = fileChooser.showOpenDialog(BinaryFileManagementPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for (File selectedFile : fileChooser.getSelectedFiles()) {
                        try {
                            T t = getBinaryFile(selectedFile);
                            binaryFilesToAdd.add(t);
                        } catch (IOException | InstantiationException | IllegalAccessException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
                if (!binaryFilesToAdd.isEmpty()) {
                    BinaryFileManagementPanel.this.firePropertyChange(ADDED, null, binaryFilesToAdd);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (binaryFileList.getSelectedIndex() != -1) {
                    List<T> binaryFilesToRemove = new ArrayList<>();
                    binaryFilesToRemove.addAll(binaryFileList.getSelectedValuesList());

                    BinaryFileManagementPanel.this.firePropertyChange(REMOVED, null, binaryFilesToRemove);
                }
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
    private List<T> getBinaryFiles() {
        List<T> binaryFiles = new ArrayList<>();
        for (T t : binaryFileBindingList) {
            binaryFiles.add(t);
        }

        return binaryFiles;
    }

    /**
     * Make a new T instance from the file input.
     *
     * @param file
     */
    private T getBinaryFile(File file) throws IOException, InstantiationException, IllegalAccessException {
        T t = type.newInstance();
        t.setFileName(file.getName());

        //get file as byte array
        byte[] bytes = FileUtils.readFileToByteArray(file);

        //gzip the byte array
        try (ByteArrayOutputStream zippedByteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(zippedByteArrayOutputStream);) {
            gZIPOutputStream.write(bytes);

            gZIPOutputStream.flush();
            gZIPOutputStream.finish();

            t.setContent(zippedByteArrayOutputStream.toByteArray());
        }

        return t;
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

        setBackground(new java.awt.Color(255, 255, 255));

        binaryFileListScrollPane.setOpaque(false);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
