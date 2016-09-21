package com.compomics.colims.core.ontology;

import com.compomics.colims.core.util.ResourceUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class keeps track of mapping between resource (MaxQuant, PeptideShaker, Colims client) terms and ontology terms.
 * <p>
 * Created by Niels Hulstaert on 11/09/16.
 */
@Lazy
@Component("ontologyMapper")
public class OntologyMapper {

    /**
     * The Colims terms mapping.
     */
    private ColimsMapping colimsMapping = new ColimsMapping();
    /**
     * The MaxQuant terms mapping.
     */
    private MaxQuantMapping maxQuantMapping = new MaxQuantMapping();

    /**
     * No-arg constructor.
     */
    public OntologyMapper() {
    }

    public ColimsMapping getColimsMapping() {
        return colimsMapping;
    }

    public MaxQuantMapping getMaxQuantMapping() {
        return maxQuantMapping;
    }

    /**
     * Parse the JSON mapping file and populate the mapping terms for the different resources.
     *
     * @throws IOException in case of an I/O related problem
     */
    @PostConstruct
    private void parse() throws IOException {
        Resource ontologyMapping = ResourceUtils.getResourceByRelativePath("config/ontology_mapping.json");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.reader();
        JsonNode mappingResourcesNode = objectReader.readTree(ontologyMapping.getInputStream());

        Iterator<Map.Entry<String, JsonNode>> mappingResourcesIterator = mappingResourcesNode.fields();
        while (mappingResourcesIterator.hasNext()) {
            Map.Entry<String, JsonNode> mappingResource = mappingResourcesIterator.next();
            Map<String, Map<String, OntologyTerm>> mappingResourceTypes = parseMappingResourceTypes(objectReader, mappingResource.getValue());
            switch (mappingResource.getKey()) {
                case "MaxQuant":
                    for (Map.Entry<String, Map<String, OntologyTerm>> entry : mappingResourceTypes.entrySet()) {
                        switch (entry.getKey()) {
                            case "modifications":
                                maxQuantMapping.setModifications(entry.getValue());
                                break;
                            case "quantification_reagents":
                                maxQuantMapping.setQuantificationReagents(entry.getValue());
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown MaxQuant mapping resource type " + entry.getKey());
                        }
                    }
                    break;
                case "Colims":
                    for (Map.Entry<String, Map<String, OntologyTerm>> entry : mappingResourceTypes.entrySet()) {
                        switch (entry.getKey()) {
                            case "quantification_methods":
                                colimsMapping.setQuantificationMethods(entry.getValue());
                                break;
                            case "quantification_reagents":
                                colimsMapping.setQuantificationReagents(entry.getValue());
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown Colims mapping resource type " + entry.getKey());
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown mapping resource " + mappingResource.getKey());
            }
        }
    }

    /**
     * Parse the mapping between resource and ontology terms for the given mapping resource node.
     *
     * @param objectReader    the {@link ObjectReader} instance
     * @param mappingResource the mapping resource parent node
     * @return the mapped terms (key: mapping resource type; value: the ontology terms map)
     * @throws JsonProcessingException
     */
    private Map<String, Map<String, OntologyTerm>> parseMappingResourceTypes(ObjectReader objectReader, JsonNode mappingResource) throws JsonProcessingException {
        Map<String, Map<String, OntologyTerm>> mappings = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> mappingResourceTypesIterator = mappingResource.fields();
        while (mappingResourceTypesIterator.hasNext()) {
            Map.Entry<String, JsonNode> mappingResourceType = mappingResourceTypesIterator.next();
            String mappingResourceTypeName = mappingResourceType.getKey();
            Iterator<Map.Entry<String, JsonNode>> mappingsIterator = mappingResourceType.getValue().fields();
            Map<String, OntologyTerm> terms = new HashMap<>();
            while (mappingsIterator.hasNext()) {
                Map.Entry<String, JsonNode> mapping = mappingsIterator.next();
                OntologyTerm ontologyTerm = objectReader.treeToValue(mapping.getValue(), OntologyTerm.class);
                terms.put(mapping.getKey(), ontologyTerm);
            }
            mappings.put(mappingResourceTypeName, terms);
        }

        return mappings;
    }

}
