/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Niels Hulstaert
 */
public class GuiUtils {

    /**
     * Private constructor to prevent initialization.
     */
    private GuiUtils() {
    }

    /**
     * Center the dialog on the parent component
     *
     * @param parentComponent the parent component
     * @param dialog the dialog
     */
    public static void centerDialogOnComponent(final Component parentComponent, final JDialog dialog) {
        Point topLeft = parentComponent.getLocationOnScreen();
        Dimension parentSize = parentComponent.getSize();

        Dimension dialogSize = dialog.getSize();

        int x;
        int y;

        if (parentSize.width > dialogSize.width) {
            x = ((parentSize.width - dialogSize.width) / 2) + topLeft.x;
        } else {
            x = topLeft.x;
        }

        if (parentSize.height > dialogSize.height) {
            y = ((parentSize.height - dialogSize.height) / 2) + topLeft.y;
        } else {
            y = topLeft.y;
        }

        dialog.setLocation(x, y);
    }

    /**
     * Get the name of the visible child component. Returns null if no
     * components are visible or the name is null. Use this method only when the
     * child components names are set.
     *
     * @param parentContainer the parent container
     * @return the visible component name
     */
    public static String getVisibleChildComponent(final Container parentContainer) {
        String visibleComponentName = null;

        for (Component component : parentContainer.getComponents()) {
            if (component.isVisible()) {
                visibleComponentName = component.getName();
                break;
            }
        }

        return visibleComponentName;
    }

    /**
     * Validate the entity validation annotations
     *
     * @param <T> the entity class
     * @param t the entity
     * @return the list of validation messages
     */
    public static <T> List<String> validateEntity(final T t) {
        List<String> validationMessages = new ArrayList<>();

        ValidatorFactory entityValidator = Validation.buildDefaultValidatorFactory();
        Validator validator = entityValidator.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        if (!constraintViolations.isEmpty()) {
            constraintViolations.stream().forEach((constraintViolation) -> {
                if (constraintViolation.getInvalidValue() != null && !constraintViolation.getInvalidValue().toString().isEmpty()) {
                    validationMessages.add(constraintViolation.getMessage() + ": " + constraintViolation.getInvalidValue());
                } else {
                    validationMessages.add(constraintViolation.getMessage());
                }
            });
        }

        return validationMessages;
    }

    /**
     * This method shows a message dialog with the given values. In case of an
     * error message, a JTextarea is shown.
     *
     * @param parentComponent the parent component
     * @param title the message dialog title
     * @param message the message dialog content
     * @param messageType the message dialog type
     */
    public static void showMessageDialog(final Component parentComponent, final String title, final String message, final int messageType) {
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            //add message to JTextArea
            JTextArea textArea = new JTextArea(message);
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            scrollPane.getViewport().setOpaque(false);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(parentComponent, scrollPane, title, messageType);
        } else {
            JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
        }
    }

    /**
     * Returns the normal icon.
     *
     * @return the normal icon
     */
    public static Image getNormalIcon() {
        return Toolkit.getDefaultToolkit().getImage(GuiUtils.class.getResource("/icons/colims_icon.png"));
    }
}
