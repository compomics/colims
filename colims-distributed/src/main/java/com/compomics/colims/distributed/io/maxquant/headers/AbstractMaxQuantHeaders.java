package com.compomics.colims.distributed.io.maxquant.headers;

import com.compomics.colims.core.util.ResourceUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class holds all headers for the different MaxQuant identification files.
 * <p>
 * Created by Niels Hulstaert on 13/09/16.
 */
public abstract class AbstractMaxQuantHeaders<T extends Enum<T>> {

    protected Class<T> enumType;
    protected EnumMap<T, MaxQuantHeader> headersMap;
    protected String jsonRelativePath;

    public AbstractMaxQuantHeaders() {
    }

    public AbstractMaxQuantHeaders(Class<T> enumType, EnumMap<T, MaxQuantHeader> headersMap, String jsonRelativePath) {
        this.enumType = enumType;
        this.headersMap = headersMap;
        this.jsonRelativePath = jsonRelativePath;
    }

    /**
     * Get all the mandatory headers for a given MaxQuant file.
     *
     * @return the list of mandatory headers
     */
    public List<MaxQuantHeader> getMandatoryHeaders() {
        return headersMap.values().stream().filter(maxQuantHeader -> maxQuantHeader.isMandatory()).collect(Collectors.toList());
    }

    public MaxQuantHeader getHeader(T headerEnum) {
        return headersMap.get(headerEnum);
    }

    protected void parse() throws IOException {
        Resource jsonHeadersResource = ResourceUtils.getResourceByRelativePath(jsonRelativePath);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.reader();

        JsonNode headersNode = objectReader.readTree(jsonHeadersResource.getInputStream());
        Iterator<JsonNode> headersIterator = headersNode.elements();

        while (headersIterator.hasNext()) {
            JsonNode evidenceHeaderNode = headersIterator.next();

            String headerName = "";

            MaxQuantHeader maxQuantHeader = new MaxQuantHeader();
            headersMap.put(getByStringValue(headerName), maxQuantHeader);
        }
    }

    private T getByStringValue(String headersEnumStringValue) {
        for (T headersEnum : enumType.getEnumConstants()) {
            if (headersEnum.name().equals(headersEnumStringValue)) {
                return headersEnum;
            }
        }

        throw new IllegalArgumentException("Value " + " does not correspond to an enum value of class " + enumType.getCanonicalName());
    }

}
