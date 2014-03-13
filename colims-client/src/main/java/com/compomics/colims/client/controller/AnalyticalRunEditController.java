package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunEditDialog;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("analyticalRunEditController")
public class AnalyticalRunEditController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunEditController.class);

    //model   
    private BindingGroup bindingGroup;
    private ObservableList<Instrument> instrumentBindingList;
    private AnalyticalRun analyticalRunToEdit;
    //view
    private AnalyticalRunEditDialog analyticalRunEditDialog;
    //parent controller
    @Autowired
    private SampleEditController sampleEditController;
    //services
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EventBus eventBus;

    public AnalyticalRunEditDialog getAnalyticalRunEditDialog() {
        return analyticalRunEditDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        analyticalRunEditDialog = new AnalyticalRunEditDialog(sampleEditController.getSampleEditDialog(), true);

        //set DateTimePicker format
        analyticalRunEditDialog.getDateTimePicker().setFormats(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
        analyticalRunEditDialog.getDateTimePicker().setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding instrumentComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, analyticalRunEditDialog.getInstrumentComboBox());
        bindingGroup.addBinding(instrumentComboBoxBinding);

        bindingGroup.bind();

        analyticalRunEditDialog.getUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //update analyticalRunToEdit with dialog input
                updateAnalyticalRunToEdit();

                //validate analytical run
                List<String> validationMessages = GuiUtils.validateEntity(analyticalRunToEdit);

                if (validationMessages.isEmpty()) {                    
                    analyticalRunService.update(analyticalRunToEdit);
                    int index = sampleEditController.getSelectedAnalyticalRunIndex();

                    MessageEvent messageEvent = new MessageEvent("analytical run persist confirmation", "Analytical run " + analyticalRunToEdit.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in analytical list in sample edit dialog
                    sampleEditController.setSelectedAnalyticalRun(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        analyticalRunEditDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                analyticalRunEditDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(sampleEditController.getSampleEditDialog(), analyticalRunEditDialog);
        analyticalRunEditDialog.setVisible(true);
    }

    /**
     * Update the analytical run edit dialog with the selected analytical run in
     * the analytical run overview table.
     *
     * @param analyticalRun the selected analytical run in the overview table
     */
    public void updateView(final AnalyticalRun analyticalRun) {
        analyticalRunToEdit = analyticalRun;

        analyticalRunEditDialog.getNameTextField().setText(analyticalRunToEdit.getName());
        if (analyticalRun.getStartDate() != null) {
            analyticalRunEditDialog.getDateTimePicker().setDate(analyticalRunToEdit.getStartDate());
        }

        //set the selected item in the instrument combobox        
        analyticalRunEditDialog.getInstrumentComboBox().setSelectedItem(analyticalRunToEdit.getInstrument());

        showView();
    }
    
    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(InstrumentChangeEvent instrumentChangeEvent) {
        instrumentBindingList.clear();
        instrumentBindingList.addAll(instrumentService.findAll());
    }

    /**
     * Update the instance fields of the selected analytical run in the
     * analytical runs table
     */
    private void updateAnalyticalRunToEdit() {
        analyticalRunToEdit.setName(analyticalRunEditDialog.getNameTextField().getText());
        if (analyticalRunEditDialog.getDateTimePicker().getDate() != null) {
            analyticalRunToEdit.setStartDate(analyticalRunEditDialog.getDateTimePicker().getDate());
        }
        if (analyticalRunEditDialog.getInstrumentComboBox().getSelectedIndex() != -1) {
            analyticalRunToEdit.setInstrument(instrumentBindingList.get(analyticalRunEditDialog.getInstrumentComboBox().getSelectedIndex()));
        }
    }

}
