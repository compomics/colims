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
public abstract class MaxQuantHeaders<T extends Enum<T>> {

    protected Class<T> enumType;
    protected EnumMap<T, MaxQuantHeader> headersMap;
    protected String jsonRelativePath;

    /**
     * No-arg constructor.
     */
    public MaxQuantHeaders() {
    }

    /**
     * Constructor.
     *
     * @param enumType the enum class for generics purposes
     * @param headersMap the headers map
     * @param jsonRelativePath the path of the JSON file with the
     */
    public MaxQuantHeaders(Class<T> enumType, EnumMap<T, MaxQuantHeader> headersMap, String jsonRelativePath) {
        this.enumType = enumType;
        this.headersMap = headersMap;
        this.jsonRelativePath = jsonRelativePath;
    }

    public Class<T> getEnumType() {
        return enumType;
    }

    /**
     * Get all the headers for a given MaxQuant file.
     *
     * @return the list of mandatory headers
     */
    public List<MaxQuantHeader> getHeaders() {
        return headersMap.values().stream().collect(Collectors.toList());
    }

    /**
     * Get all the mandatory headers for a given MaxQuant file.
     *
     * @return the list of mandatory headers
     */
    public List<MaxQuantHeader> getMandatoryHeaders() {
        return headersMap.values().stream().filter(maxQuantHeader -> maxQuantHeader.isMandatory()).collect(Collectors.toList());
    }

    /**
     * Get the parsed header value for the given header.
     *
     * @param headerEnum the header enum
     * @return the header value
     */
    public String get(T headerEnum) {
        return headersMap.get(headerEnum).getValue();
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
