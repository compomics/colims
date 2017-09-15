package com.compomics.colims.model.eav;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;

public class JsonNodeBinaryType extends AbstractSingleColumnStandardBasicType<JsonNode> {

    public JsonNodeBinaryType() {
        super(
                JsonBinarySqlTypeDescriptor.INSTANCE,
                JsonNodeTypeDescriptor.INSTANCE
        );
    }

    public String getName() {
        return "jsonb-node";
    }
}
