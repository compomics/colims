package com.compomics.colims.client.compoment;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <T> the object class that is used in a DualList instance.
 * @author Niels Hulstaert
 */
public class DualList<T> extends javax.swing.JPanel {

    public static final String CHANGED = "changed";
    //model
    /**
     * The BindingGroup instance.
     */
    private transient BindingGroup bindingGroup;
    /**
     * The list of available items.
     */
    private ObservableList<T> availableItemBindingList;
    /**
     * The list of added items.
     */
    private ObservableList<T> addedItemBindingList;
    /**
     * The maximum amount of added items value.
     */
    private int maximumAmountOfAddedItems;
    /**
     * the Comparator instance used for comparing T instances.
     */
    private Comparator<? super T> comparator;

    /**
     * Constructor.
     */
    public DualList() {
        initComponents();
    }

    /**
     * Populate the available and added items lists. This method removes the added items from the availabe items.
     *
     * @param availableItems the available items
     * @param addedItems     the added items
     */
    public void populateLists(List<T> availableItems, List<T> addedItems) {
        populateLists(availableItems, addedItems, Integer.MAX_VALUE);
    }

    /**
     * Populate the available and added items lists. This method removes the added items from the availabe items list.
     *
     * @param availableItems            the available items
     * @param addedItems                the added items
     * @param maximumAmountOfAddedItems the maximum amount of added items
     */
    public void populateLists(List<T> availableItems, List<T> addedItems, int maximumAmountOfAddedItems) {
        if (comparator == null) {
            throw new IllegalArgumentException("The dual list component has not been initialized.");
        }

        this.maximumAmountOfAddedItems = maximumAmountOfAddedItems;

        availableItemBindingList.clear();
        //check for added items in the available items list
        availableItemBindingList.addAll(availableItems.stream().filter(availableItem -> !addedItems.contains(availableItem)).collect(Collectors.toList()));

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
        return addedItemBindingList.stream().collect(Collectors.toList());
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
     *
     * @param comparator the Comparator used for comparing T instances.
     */
    public void init(Comparator<? super T> comparator) {
        this.comparator = comparator;

        //set default value
        maximumAmountOfAddedItems = Integer.MAX_VALUE;

        //init bindings
        bindingGroup = new BindingGroup();

        availableItemBindingList = ObservableCollections.observableList(new ArrayList<>());
        JListBinding availableItemBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, availableItemBindingList, availableItemList);
        bindingGroup.addBinding(availableItemBinding);

        addedItemBindingList = ObservableCollections.observableList(new ArrayList<>());
        JListBinding addedItemBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, addedItemBindingList, addedItemList);
        bindingGroup.addBinding(addedItemBinding);

        bindingGroup.bind();

        //add action listeners
        addItemButton.addActionListener(ae -> {
            List<T> oldAddedItems = getAddedItems();

            List<T> selectedItems = availableItemList.getSelectedValuesList();
            if (!selectedItems.isEmpty()) {
                for (T selectedItem : selectedItems) {
                    //add to addedItemBindingList
                    addedItemBindingList.add(selectedItem);
                    //remove from availableItemBindingList
                    availableItemBindingList.remove(selectedItem);
                }
                //sort
                sort(addedItemBindingList);

                //check button states
                checkButtonStates();

                List<T> newAddedItems = getAddedItems();
                //notify listeners
                DualList.this.firePropertyChange(CHANGED, oldAddedItems, newAddedItems);
            }
        });

        removeItemButton.addActionListener(ae -> {
            List<T> oldAddedItems = getAddedItems();

            List<T> selectedItems = addedItemList.getSelectedValuesList();
            if (!selectedItems.isEmpty()) {
                for (T selectedItem : selectedItems) {
                    //add to availableItemBindingList and sort
                    availableItemBindingList.add(selectedItem);
                    //remove from addedItemBindingList
                    addedItemBindingList.remove(selectedItem);
                }
                //sort
                sort(availableItemBindingList);

                //check button states
                checkButtonStates();

                List<T> newAddedItems = getAddedItems();
                //notify listeners
                DualList.this.firePropertyChange(CHANGED, oldAddedItems, newAddedItems);
            }
        });
    }

    /**
     * Change the addItemButton and removeItemButton enabled states. If there are no more available items or the maximum
     * number of items has been reached, the addItemButton is disabled and if there are no added items, the
     * removeItemButton is disabled.
     */
    private void checkButtonStates() {
        boolean addItemButtonEnabled = true;
        boolean removeItemButtonEnabled = true;
        if (availableItemBindingList.isEmpty() || addedItemBindingList.size() == maximumAmountOfAddedItems) {
            addItemButtonEnabled = false;
        }
        if (addedItemBindingList.isEmpty()) {
            removeItemButtonEnabled = false;
        }

        addItemButton.setEnabled(addItemButtonEnabled);
        removeItemButton.setEnabled(removeItemButtonEnabled);
    }

    /**
     * Sort the list using the comparator.
     *
     * @param listToSort the list to sort
     */
    private void sort(ObservableList<T> listToSort) {
        Collections.sort(listToSort, comparator);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        availableScrollPane = new javax.swing.JScrollPane();
        availableItemList = new javax.swing.JList<T>();
        addedScrollPane = new javax.swing.JScrollPane();
        addedItemList = new javax.swing.JList<T>();
        buttonParentPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        addItemButton = new javax.swing.JButton();
        removeItemButton = new javax.swing.JButton();

        setOpaque(false);

        availableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Available"));

        availableScrollPane.setViewportView(availableItemList);

        addedScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Added"));

        addedScrollPane.setViewportView(addedItemList);

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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonParentPanel.add(buttonPanel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(availableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonParentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addedScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(availableScrollPane)
                        .addComponent(buttonParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addedScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JList<T> addedItemList;
    private javax.swing.JScrollPane addedScrollPane;
    private javax.swing.JList<T> availableItemList;
    private javax.swing.JScrollPane availableScrollPane;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel buttonParentPanel;
    private javax.swing.JButton removeItemButton;
    // End of variables declaration//GEN-END:variables
}
