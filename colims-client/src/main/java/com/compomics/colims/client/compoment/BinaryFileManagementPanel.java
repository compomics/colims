package com.compomics.colims.client.compoment;

import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.BinaryFile;
import com.compomics.colims.model.enums.BinaryFileType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jmol.export.dialog.FileChooser;

/**
 * The binary file management view.
 *
 * @author Niels Hulstaert
 * @param <T> the BinaryFile subclass
 */
public class BinaryFileManagementPanel<T extends BinaryFile> extends javax.swing.JPanel {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(BinaryFileManagementPanel.class);

    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String FILE_TYPE_CHANGE = "file type change";
    //model
    /**
     * Keep a reference to the class type for new instance creation.
     */
    private Class<T> type;
    private final JFileChooser fileChooser = new FileChooser();
    private final JFileChooser exportDirectoryChooser = new FileChooser();
    private BindingGroup bindingGroup;
    private ObservableList<T> binaryFileBindingList;
    private int previouslySelectedIndex = -1;

    /**
     * Creates new form BinaryFileManagementPanel.
     */
    public BinaryFileManagementPanel() {
        initComponents();
    }

    /**
     * populate the binary file list.
     *
     * @param binaryFiles the list of binary files.
     */
    public void populateList(final List<T> binaryFiles) {
        if (type == null) {
            throw new IllegalArgumentException("The class type has not been set.");
        }

        clear();
        binaryFileBindingList.addAll(binaryFiles);
    }

    /**
     * Init the component.
     *
     * @param type the class type
     */
    public void init(final Class<T> type) {
        this.type = type;

        //fill combobox
        for (BinaryFileType binaryFileType : BinaryFileType.values()) {
            binaryFileTypeComboBox.addItem(binaryFileType);
        }

        //select only files
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //select multiple file
        fileChooser.setMultiSelectionEnabled(Boolean.FALSE);

        //select only directories
        exportDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //select multiple file
        exportDirectoryChooser.setMultiSelectionEnabled(Boolean.FALSE);

        //init bindings
        bindingGroup = new BindingGroup();

        binaryFileBindingList = ObservableCollections.observableList(new ArrayList());
        JListBinding binaryFileListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, binaryFileBindingList, binaryFileList);
        bindingGroup.addBinding(binaryFileListBinding);

        bindingGroup.bind();

        //add action listeners
        binaryFileList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = binaryFileList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        //set selected item in combobox
                        binaryFileTypeComboBox.setSelectedItem(binaryFileBindingList.get(selectedIndex).getBinaryFileType());
                    }

                    previouslySelectedIndex = selectedIndex;
                }
            }
        });

        binaryFileTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int selectedIndex = binaryFileList.getSelectedIndex();
                if (selectedIndex != -1) {
                    if (previouslySelectedIndex == selectedIndex) {
                        T binaryFileToUpdate = binaryFileBindingList.get(selectedIndex);
                        binaryFileToUpdate.setBinaryFileType((BinaryFileType) binaryFileTypeComboBox.getSelectedItem());

                        //update GUI
                        binaryFileList.updateUI();

                        BinaryFileManagementPanel.this.firePropertyChange(FILE_TYPE_CHANGE, null, binaryFileToUpdate);
                    }
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (binaryFileList.getSelectedIndex() != -1) {
                    T binaryFileToExport = (T) binaryFileList.getSelectedValue();
                    //in response to the button click, show open dialog
                    int returnVal = exportDirectoryChooser.showOpenDialog(BinaryFileManagementPanel.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            File exportDirectory = exportDirectoryChooser.getSelectedFile();
                            if (exportDirectory.isDirectory()) {
                                exportBinaryFile(exportDirectory, binaryFileToExport);
                            }
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(BinaryFileManagementPanel.this, "Please select an attachment to export.", "Attachment selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                T binaryFileToAdd = null;

                //in response to the button click, show open dialog
                int returnVal = fileChooser.showOpenDialog(BinaryFileManagementPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        binaryFileToAdd = getBinaryFile(fileChooser.getSelectedFile());
                    } catch (IOException | InstantiationException | IllegalAccessException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
                if (binaryFileToAdd != null) {
                    binaryFileBindingList.add(binaryFileToAdd);

                    //set selected index
                    binaryFileList.setSelectedIndex(binaryFileBindingList.size() - 1);

                    BinaryFileManagementPanel.this.firePropertyChange(ADD, null, binaryFileToAdd);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (binaryFileList.getSelectedIndex() != -1) {
                    T binaryFileToRemove = (T) binaryFileList.getSelectedValue();

                    binaryFileBindingList.remove(binaryFileToRemove);

                    //set selected index
                    if (!binaryFileBindingList.isEmpty()) {
                        binaryFileList.setSelectedIndex(0);
                    }

                    BinaryFileManagementPanel.this.firePropertyChange(REMOVE, null, binaryFileToRemove);
                } else {
                    JOptionPane.showMessageDialog(BinaryFileManagementPanel.this, "Please select an attachment to delete.", "Attachment selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     * Clear the file list
     */
    private void clear() {
        previouslySelectedIndex = -1;
        binaryFileBindingList.clear();
    }

    /**
     * Make a new T instance from the file input.
     *
     * @param file
     */
    private T getBinaryFile(File file) throws IOException, InstantiationException, IllegalAccessException {
        T binaryFile = type.newInstance();
        binaryFile.setFileName(file.getName());
        binaryFile.setBinaryFileType(BinaryFileType.TEXT);

        binaryFile.setContent(IOUtils.readAndZip(file));

        return binaryFile;
    }

    /**
     * Export the binary file to file
     *
     * @param exportDirectory
     * @param binaryFile
     */
    private void exportBinaryFile(File exportDirectory, T binaryFile) throws IOException {
        IOUtils.unzipAndWrite(binaryFile.getContent(), new File(exportDirectory, binaryFile.getFileName()));
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
        exportButton = new javax.swing.JButton();
        binaryFileTypeComboBox = new javax.swing.JComboBox();
        typeLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        binaryFileListScrollPane.setOpaque(false);

        binaryFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        binaryFileListScrollPane.setViewportView(binaryFileList);

        deleteButton.setText("delete");
        deleteButton.setToolTipText("delete the selected attachment");
        deleteButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteButton.setPreferredSize(new java.awt.Dimension(80, 25));

        exportButton.setText("export");
        exportButton.setToolTipText("export the selected attachment");
        exportButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportButton.setPreferredSize(new java.awt.Dimension(80, 25));

        typeLabel.setText("type");

        addButton.setText("add");
        addButton.setToolTipText("add an attachment");
        addButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryFileListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(binaryFileTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(typeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(binaryFileTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(binaryFileListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList binaryFileList;
    private javax.swing.JScrollPane binaryFileListScrollPane;
    private javax.swing.JComboBox binaryFileTypeComboBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
