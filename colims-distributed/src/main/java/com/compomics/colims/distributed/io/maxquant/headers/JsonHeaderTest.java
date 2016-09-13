package com.compomics.colims.distributed.io.maxquant.headers;

import com.compomics.colims.core.util.ResourceUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Niels Hulstaert on 13/09/16.
 */
public class JsonHeaderTest {

    public static void main(String[] args) throws IOException {
        Headers headers = new Headers();
        System.out.println("dddddddddddd");
    }

    public void parse() throws IOException {
        Resource ontologyMapping = ResourceUtils.getResourceByRelativePath("maxquant/evidence_headers.json");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.reader();
        JsonNode evidenceHeadersNode = objectReader.readTree(ontologyMapping.getInputStream());

        Iterator<JsonNode> evidenceHeadersIterator = evidenceHeadersNode.elements();
        while (evidenceHeadersIterator.hasNext()) {
            JsonNode evidenceHeaderNode = evidenceHeadersIterator.next();
            Header header = objectReader.treeToValue(evidenceHeaderNode, Header.class);
            System.out.println("test");
        }


        System.out.println("test");
    }

}
