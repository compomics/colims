package com.compomics.colims.distributed.io.maxquant.headers;

import com.compomics.colims.core.util.ResourceUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Niels Hulstaert on 13/09/16.
 */
public class Headers {

    /**
     * Enum representing the MaxQuant tab seperated identification files.
     */
    public enum MaxQuantFile {
        EVIDENCE("maxquant/evidence_headers.json"),
        MSMS("maxquant/evidence_headers.json"),
        PARAMETER("maxquant/evidence_headers.json"),
        PROTEIN_GROUPS("maxquant/evidence_headers.json"),
        SPECTRUM_PARAMETER("maxquant/evidence_headers.json"),
        SUMMARY("maxquant/evidence_headers.json");

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

        public String jsonRelativePath() {
            return jsonRelativePath;
        }
    }

    /**
     * Map containing headers for each file (key: {@link MaxQuantFile} enum; value: {@link Header} list).
     */
    private EnumMap<MaxQuantFile, List<Header>> headersMap = new EnumMap<MaxQuantFile, List<Header>>(MaxQuantFile.class);

    /**
     * No-arg constructor.
     */
    public Headers() throws IOException {
        parseJsonHeaderFiles();
    }

    public EnumMap<MaxQuantFile, List<Header>> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(EnumMap<MaxQuantFile, List<Header>> headersMap) {
        this.headersMap = headersMap;
    }

    /**
     * Get all the mandatory headers for a given MaxQuant file.
     *
     * @param maxQuantFile the {@link MaxQuantFile} enum value
     * @return the list of mandatory headers
     */
    public List<Header> getMandatoryHeaders(MaxQuantFile maxQuantFile) {
        return headersMap.get(maxQuantFile).stream().filter(header -> header.isMandatory()).collect(Collectors.toList());
    }

    /**
     * Parse the JSON header files for the different MaxQuant identification files.
     *
     * @throws IOException in case of an Input/Output related problem
     */
    private void parseJsonHeaderFiles() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (MaxQuantFile maxQuantFile : MaxQuantFile.values()) {
            Resource jsonHeadersResource = ResourceUtils.getResourceByRelativePath(maxQuantFile.jsonRelativePath());

            ObjectReader objectReader = objectMapper.reader();
            JsonNode headersNode = objectReader.readTree(jsonHeadersResource.getInputStream());

            List<Header> headers = new ArrayList<>();
            Iterator<JsonNode> evidenceHeadersIterator = headersNode.elements();
            while (evidenceHeadersIterator.hasNext()) {
                JsonNode evidenceHeaderNode = evidenceHeadersIterator.next();
                Header header = objectReader.treeToValue(evidenceHeaderNode, Header.class);
                headers.add(header);
            }

            headersMap.put(maxQuantFile, headers);
        }
    }

}
