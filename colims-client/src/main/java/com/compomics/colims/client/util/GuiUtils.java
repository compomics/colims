/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.util;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
     * Center the dialog on the parent frame
     *
     * @param parentFrame the parent frame
     * @param dialog the dialog
     */
    public static void centerDialogOnFrame(JFrame parentFrame, JDialog dialog) {
        Point topLeft = parentFrame.getLocationOnScreen();
        Dimension parentSize = parentFrame.getSize();

        Dimension dialogSize = dialog.getSize();

        int x = 0;
        int y = 0;

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
     * Get the name of the component currently visible in the card layout
     *
     * @param parentContainer the parent container
     * @return the component name
     */
    public static String getCurrentCardName(Container parentContainer) {
        CardLayout cardLayout = (CardLayout) parentContainer.getLayout();

        if (cardLayout == null) {
            throw new IllegalArgumentException("The layout of the parent container is no card layout.");
        }

        for (Component component : parentContainer.getComponents())
            if (component.isVisible())
                return ((JPanel) component).getName();

        throw new IllegalStateException("None of the cards in parentContainer was visible; Could not getCurrentCardName");
    }

    /**
     * Validate the entity validation annotations
     *
     * @param <T> the enity class
     * @param t the entity
     * @return the list of validation messages
     */
    public static <T> List<String> validateEntity(T t) {
        List<String> validationMessages = new ArrayList<String>();

        ValidatorFactory entityValidator = Validation.buildDefaultValidatorFactory();
        Validator validator = entityValidator.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                if(constraintViolation.getInvalidValue() != null && !constraintViolation.getInvalidValue().toString().isEmpty()){
                    validationMessages.add(constraintViolation.getMessage() + ": " + constraintViolation.getInvalidValue());                
                }
                else{
                    validationMessages.add(constraintViolation.getMessage());                
                }                                
            }
        }

        return validationMessages;
    }
}
