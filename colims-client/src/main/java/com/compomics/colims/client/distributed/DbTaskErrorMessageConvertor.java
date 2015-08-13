package com.compomics.colims.client.distributed;

import com.compomics.colims.core.distributed.model.DbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * This class extracts the DbTask instance from the DbTaskError.
 *
 * @author Niels Hulstaert
 */
public class DbTaskErrorMessageConvertor implements MessageConverter {

    @Override
    public Message toMessage(final Object object, final Session session) throws JMSException {
        DbTask dbTask = ((DbTaskError) object).getDbTask();

        return session.createObjectMessage(dbTask);
    }

    @Override
    public Object fromMessage(final Message message) throws JMSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
