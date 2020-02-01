package com.araguacaima.open_archi.persistence.diagrams;

import com.araguacaima.commons.utils.JsonUtils;
import com.araguacaima.open_archi.persistence.diagrams.architectural.ArchitecturalModel;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BaseEntityTest {

    private static final JsonUtils jsonUtils = new JsonUtils();
    private ArchitecturalModel model;

    public BaseEntityTest() {
        Class<ArchitecturalModel> modelClass = ArchitecturalModel.class;
        String body = "{\n" +
                "   \"status\":\"INITIAL\",\n" +
                "   \"name\":\"Pattern Test 1\",\n" +
                "   \"kind\":\"ARCHITECTURE_MODEL\",\n" +
                "   \"prototype\":true,\n" +
                "   \"shape\":{\n" +
                "      \"type\":\"ARCHITECTURE_MODEL\"\n" +
                "   },\n" +
                "   \"components\":[\n" +
                "      {\n" +
                "         \"key\":-1,\n" +
                "         \"status\":0,\n" +
                "         \"name\":\"New Element 1\",\n" +
                "         \"kind\":\"COMPONENT\",\n" +
                "         \"prototype\":false,\n" +
                "         \"location\":{\n" +
                "            \"x\":\"71.65841674804688\",\n" +
                "            \"y\":\"17.88341293334961\"\n" +
                "         },\n" +
                "         \"shape\":{\n" +
                "            \"type\":\"COMPONENT\",\n" +
                "            \"fill\":\"#1368BD\"\n" +
                "         },\n" +
                "         \"relationships\":[\n" +
                "            {\n" +
                "               \"sourceId\":-1,\n" +
                "               \"destinationId\":-2\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"key\":-2,\n" +
                "         \"status\":0,\n" +
                "         \"name\":\"New Element 2\",\n" +
                "         \"kind\":\"COMPONENT\",\n" +
                "         \"prototype\":false,\n" +
                "         \"location\":{\n" +
                "            \"x\":\"71.65841674804688\",\n" +
                "            \"y\":\"72.8834129333496\"\n" +
                "         },\n" +
                "         \"shape\":{\n" +
                "            \"type\":\"COMPONENT\",\n" +
                "            \"fill\":\"#1368BD\"\n" +
                "         },\n" +
                "         \"relationships\":[\n" +
                "            {\n" +
                "               \"sourceId\":-1,\n" +
                "               \"destinationId\":-2\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ]\n" +
                "}";
        try {
            model = jsonUtils.fromJSON(body, modelClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateCreation() {
        model.getComponents().forEach(component -> {
            component.getRelationships().forEach(relationship -> {
                assertNull(relationship.getSource());
                assertNull(relationship.getDestination());
            });
        });

        model.validateCreation();

        model.getComponents().forEach(component -> {
            String id = component.getId();
            component.getRelationships().forEach(relationship -> {
                CompositeElement source = relationship.getSource();
                assertNotNull(source);
                CompositeElement destination = relationship.getDestination();
                assertNotNull(destination);
                String sourceId = source.getId();
                String destinationId = destination.getId();
                assertTrue(sourceId.equals(id) || destinationId.equals(id));
            });
        });
    }

    @Test
    public void testModelJsonPath() {
        try {
            File file = new File("./model-json-path.txt");
            FileUtils.writeLines(file, jsonUtils.buildJsonPath(ArchitecturalModel.class, null, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
