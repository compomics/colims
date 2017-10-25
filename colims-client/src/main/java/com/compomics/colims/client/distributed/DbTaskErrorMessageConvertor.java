package com.compomics.colims.client.distributed;

import com.compomics.colims.core.distributed.model.DbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * This class extracts the DbTask instance from the DbTaskError.
 *
 * @author Niels Hulstaert
 */
public class DbTaskErrorMessageConvertor implements MessageConverter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConverter.class);

    /**
     * Mapper for converting a DbTask object to the matching JSON construct.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message toMessage(final Object object, final Session session) throws JMSException {
        DbTask dbTask = ((DbTaskError) object).getDbTask();

        String jsonDbTask;
        //map to JSON construct
        try {
            jsonDbTask = objectMapper.writeValueAsString(dbTask);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new JMSException(e.getMessage());
        }

        return session.createTextMessage(jsonDbTask);
    }

    @Override
    public Object fromMessage(final Message message) throws JMSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
