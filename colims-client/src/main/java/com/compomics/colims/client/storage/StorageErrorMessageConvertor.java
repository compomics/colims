package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.StorageError;
import com.compomics.colims.distributed.model.StorageTask;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageErrorMessageConvertor implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        StorageTask storageTask = ((StorageError) object).getStorageTask();

        return session.createObjectMessage(storageTask);
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
