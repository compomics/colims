package com.compomics.colims.distributed.io.maxquant.headers;

import com.compomics.colims.core.util.ResourceUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class holds all headers for the different MaxQuant identification files.
 * <p>
 * Created by Niels Hulstaert on 13/09/16.
 */
@Component("maxQuantHeaders")
public class MaxQuantHeaders {

    /**
     * Enum representing the MaxQuant tab seperated identification files.
     */
    public enum MaxQuantFile {
        EVIDENCE("maxquant/evidence_headers.json"),
        MSMS("maxquant/msms_headers.json"),
        PARAMETER("maxquant/parameter_headers.json"),
        PROTEIN_GROUPS("maxquant/protein_groups_headers.json"),
        SPECTRUM_PARAMETER("maxquant/spectrum_parameter_headers.json"),
        SUMMARY("maxquant/summary_headers.json");

        /**
         * The relative path of the JSON headers file.
         */
        private String jsonRelativePath;

        /**
         * Package-private enum constructor.
         *
         * @param jsonRelativePath the JSON headers file relative path
         */
        MaxQuantFile(String jsonRelativePath) {
            this.jsonRelativePath = jsonRelativePath;
        }

        /**
         * Getter for the JSON file path.
         *
         * @return the JSON relative file path
         */
        public String jsonRelativePath() {
            return jsonRelativePath;
        }
    }

    /**
     * Map containing headers for each file (key: {@link MaxQuantFile} enum; value: {@link MaxQuantHeader} list).
     */
    private EnumMap<MaxQuantFile, List<MaxQuantHeader>> headersMap = new EnumMap<MaxQuantFile, List<MaxQuantHeader>>(MaxQuantFile.class);

    /**
     * No-arg constructor.
     */
    public MaxQuantHeaders() throws IOException {
    }

    public EnumMap<MaxQuantFile, List<MaxQuantHeader>> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(EnumMap<MaxQuantFile, List<MaxQuantHeader>> headersMap) {
        this.headersMap = headersMap;
    }

    /**
     * Get all the mandatory headers for a given MaxQuant file.
     *
     * @param maxQuantFile the {@link MaxQuantFile} enum value
     * @return the list of mandatory headers
     */
    public List<MaxQuantHeader> getMandatoryHeaders(MaxQuantFile maxQuantFile) {
        return headersMap.get(maxQuantFile).stream().filter(maxQuantHeader -> maxQuantHeader.isMandatory()).collect(Collectors.toList());
    }

    /**
     * Parse the JSON header files for the different MaxQuant identification files.
     *
     * @throws IOException in case of an Input/Output related problem
     */
    @PostConstruct
    private void parseJsonHeaderFiles() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (MaxQuantFile maxQuantFile : MaxQuantFile.values()) {
            Resource jsonHeadersResource = ResourceUtils.getResourceByRelativePath(maxQuantFile.jsonRelativePath());

            ObjectReader objectReader = objectMapper.reader();
            JsonNode headersNode = objectReader.readTree(jsonHeadersResource.getInputStream());

            List<MaxQuantHeader> maxQuantHeaders = new ArrayList<>();
            Iterator<JsonNode> evidenceHeadersIterator = headersNode.elements();
            while (evidenceHeadersIterator.hasNext()) {
                JsonNode evidenceHeaderNode = evidenceHeadersIterator.next();
                MaxQuantHeader maxQuantHeader = objectReader.treeToValue(evidenceHeaderNode, MaxQuantHeader.class);
                maxQuantHeaders.add(maxQuantHeader);
            }

            headersMap.put(maxQuantFile, maxQuantHeaders);
        }
    }

}
