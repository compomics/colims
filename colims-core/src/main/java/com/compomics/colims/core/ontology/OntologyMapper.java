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
import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of mapping between resource (MaxQuant, PeptideShaker, Colims client) terms and ontology terms.
 * <p>
 * Created by Niels Hulstaert on 11/09/16.
 */
@Lazy
@Component("ontologyMapper")
public class OntologyMapper {

    /**
     * Enum class for the available mapping resources.
     */
    public enum ResourceType {
        MAXQUANT(null, "MaxQuant"),
            MODIFICATIONS(MAXQUANT, "modifications"),
            QUANTIFICATION_REAGENTS(MAXQUANT, "quantification_reagents"),
        COLIMS(null, "Colims"),
            QUANTIFICATION_METHODS(COLIMS, "quantification_methods");

        /**
         * The parent resource type.
         */
        private ResourceType parent = null;
        /**
         * The child resource types.
         */
        private final List<ResourceType> children = new ArrayList<>();
        /**
         * The JSON value of the resource.
         */
        private String jsonValue;

        /**
         * Private constructor.
         *
         * @param parent    the parent resource type
         * @param jsonValue the JSON value of the resource type
         */
        ResourceType(final ResourceType parent, final String jsonValue) {
            this.parent = parent;
            if (this.parent != null) {
                this.parent.addChild(this);
            }
            this.jsonValue = jsonValue;
        }

        /**
         * Get all children of this instance.
         *
         * @return the list of child resource types
         */
        public List<ResourceType> getChildren() {
            return children;
        }

        /**
         * Get the parent of this instance.
         *
         * @return the parent resource type
         */
        public ResourceType getParent() {
            return parent;
        }

        /**
         * Return the JSON value for this resource type.
         *
         * @return true or false
         */
        public String jsonValue() {
            return jsonValue;
        }

        /**
         * Return all child resource types of this resource type as an array.
         *
         * @return the resource type children
         */
        public ResourceType[] allChildren() {
            List<ResourceType> list = new ArrayList<>();
            addChildren(this, list);
            return list.toArray(new ResourceType[list.size()]);
        }

        /**
         * Return the child resource types of this resource type (not the sub types)
         * as an array.
         *
         * @return the array of resource type children
         */
        public ResourceType[] getChildrenAsArray() {
            return children.toArray(new ResourceType[children.size()]);
        }

        @Override
        public String toString() {
            return jsonValue;
        }

        /**
         * Add a child resource type to this (parent) resource type.
         *
         * @param child the child resource type
         */
        private void addChild(final ResourceType child) {
            this.children.add(child);
        }

        /**
         * Add child resource types to this (parent) resource type in a recursive way.
         *
         * @param parent the parent resource type
         * @param list   the list of child resource types
         */
        private static void addChildren(final ResourceType parent, final List<ResourceType> list) {
            list.addAll(parent.children);
            parent.children.stream().forEach((child) -> {
                addChildren(child, list);
            });
        }
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The {@link JsonNode} instance with all the mapped ontoloy terms from the different resources.
     */
    private JsonNode resourcesNode;

    @PostConstruct
    private void parse() throws IOException {
        Resource ontologyMapping = ResourceUtils.getResourceByRelativePath("config/ontology_mapping.json");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.reader();
        resourcesNode = objectReader.readTree(ontologyMapping.getInputStream());
    }

    public OntologyTerm getMappedTerm(ResourceType parent, ResourceType child, String name) throws JsonProcessingException {
        OntologyTerm mappedTerm = null;

        if (!child.getParent().equals(parent)) {
            throw new IllegalArgumentException("Resource type " + parent.jsonValue() + " doesn't have a " + child.jsonValue() + " as child resource type.");
        }

        if (resourcesNode.get(parent.jsonValue()).get(child.jsonValue()).has(name)) {
            ObjectReader objectReader = objectMapper.reader();
            mappedTerm = objectReader.treeToValue(resourcesNode.get(parent.jsonValue()).get(child.jsonValue()).get(name), OntologyTerm.class);
        }

        return mappedTerm;
    }

}
