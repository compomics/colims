package com.compomics.colims.client.compoment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author niels
 */
public class DualList<T extends Comparable> extends javax.swing.JPanel {
    public static final String CHANGED = "changed";
        
    //model
    private BindingGroup bindingGroup;
    private ObservableList<T> availableItemBindingList;
    private ObservableList<T> addedItemBindingList;
    private int maximumAmountOfAddedItems;

    /**
     * Constructor
     */
    public DualList() {
        initComponents();

        //init the component
        init();
    }

    /**
     * Populate the available and added items lists. This method removes the
     * added items from the availabe items.
     *
     * @param availableItems the available items
     * @param addedItems the added items
     */
    public void populateLists(List<T> availableItems, List<T> addedItems) {
        populateLists(availableItems, addedItems, Integer.MAX_VALUE);
    }
    
    /**
     * Populate the available and added items lists. This method removes the
     * added items from the availabe items
     *
     * @param availableItems the available items
     * @param addedItems the added items
     * @param maximumAmountOfAddedItems the maximum amount of added items
     */
    public void populateLists(List<T> availableItems, List<T> addedItems, int maximumAmountOfAddedItems) {
        this.maximumAmountOfAddedItems = maximumAmountOfAddedItems;
        
        availableItemBindingList.clear();
        //check for added items in the available items list
        for (T availableItem : availableItems) {
            if (!addedItems.contains(availableItem)) {
                availableItemBindingList.add(availableItem);
            }
        }

        addedItemBindingList.clear();
        addedItemBindingList.addAll(addedItems);
        
        checkButtonStates();
    }

    /**
     * Get the added items as a list
     *
     * @return the added items list
     */
    public List<T> getAddedItems() {
        List<T> addedItems = new ArrayList<>();
        for (T addedItem : addedItemBindingList) {
            addedItems.add(addedItem);
        }
        
        return addedItems;
    }

    /**
     * Clear the dual list; clear the available and added items.
     */
    public void clear() {
        availableItemBindingList.clear();
        addedItemBindingList.clear();
    }  
        
    /**
     * Init the component.
     */
    private void init() {
        //set default value
        maximumAmountOfAddedItems = Integer.MAX_VALUE;
        
        //init bindings
        bindingGroup = new BindingGroup();

        availableItemBindingList = ObservableCollections.observableList(new ArrayList());
        JListBinding availableItemBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, availableItemBindingList, availableItemList);
        bindingGroup.addBinding(availableItemBinding);

        addedItemBindingList = ObservableCollections.observableList(new ArrayList());
        JListBinding addedItemBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, addedItemBindingList, addedItemList);
        bindingGroup.addBinding(addedItemBinding);

        bindingGroup.bind();

        //add action listeners
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                List selectedItems = availableItemList.getSelectedValuesList();

                for (Object selectedObject : selectedItems) {
                    T selectedItem = (T) selectedObject;                    
                    
                    //add to addedItemBindingList and sort
                    addedItemBindingList.add(selectedItem);
                    Collections.sort(addedItemBindingList);
                    //remove from availableItemBindingList
                    availableItemBindingList.remove(selectedItem);

                    //check button states
                    checkButtonStates();
                                        
                    DualList.this.firePropertyChange(CHANGED, false, true);
                }
            }
        });

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                List selectedItems = addedItemList.getSelectedValuesList();

                for (Object selectedObject : selectedItems) {
                    T selectedItem = (T) selectedObject;
                    
                    //add to availableItemBindingList and sort
                    availableItemBindingList.add(selectedItem);
                    Collections.sort(availableItemBindingList);
                    //remove from addedItemBindingList
                    addedItemBindingList.remove(selectedItem);

                    //check button states
                    checkButtonStates();
                    
                    DualList.this.firePropertyChange(CHANGED, false, true);
                }
            }
        });
    }        

    /**
     * Change the addItemButton and removeItemButton enabled states. If there
     * are no more available items or the maximum number of items has been reached, the addItemButton is disabled and if there
     * are no added items, the removeItemButton is disabled.
     */
    private void checkButtonStates() {
        boolean addItemButtonEnabled = true;
        boolean removeItemButtonEnabled = true;
        if(availableItemBindingList.isEmpty() || addedItemBindingList.size() == maximumAmountOfAddedItems){
            addItemButtonEnabled = false;
        }
        if(addedItemBindingList.isEmpty()){
            removeItemButtonEnabled = false;
        }        
        
        addItemButton.setEnabled(addItemButtonEnabled);
        removeItemButton.setEnabled(removeItemButtonEnabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        availableScrollPane = new javax.swing.JScrollPane();
        availableItemList = new javax.swing.JList();
        addedScrollPane = new javax.swing.JScrollPane();
        addedItemList = new javax.swing.JList();
        buttonParentPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        addItemButton = new javax.swing.JButton();
        removeItemButton = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        availableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Available"));
        availableScrollPane.setOpaque(false);

        availableScrollPane.setViewportView(availableItemList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(availableScrollPane, gridBagConstraints);

        addedScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Added"));
        addedScrollPane.setOpaque(false);

        addedScrollPane.setViewportView(addedItemList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(addedScrollPane, gridBagConstraints);

        buttonParentPanel.setOpaque(false);
        buttonParentPanel.setLayout(new java.awt.GridBagLayout());

        buttonPanel.setOpaque(false);

        addItemButton.setText(">>");
        addItemButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addItemButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addItemButton.setPreferredSize(new java.awt.Dimension(80, 25));

        removeItemButton.setText("<<");
        removeItemButton.setMaximumSize(new java.awt.Dimension(80, 25));
        removeItemButton.setMinimumSize(new java.awt.Dimension(80, 25));
        removeItemButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removeItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonParentPanel.add(buttonPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(buttonParentPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JList addedItemList;
    private javax.swing.JScrollPane addedScrollPane;
    private javax.swing.JList availableItemList;
    private javax.swing.JScrollPane availableScrollPane;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel buttonParentPanel;
    private javax.swing.JButton removeItemButton;
    // End of variables declaration//GEN-END:variables
}
