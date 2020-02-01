package com.araguacaima.open_archi.web;

import com.araguacaima.commons.utils.ReflectionUtils;
import com.araguacaima.open_archi.persistence.commons.Config;
import com.araguacaima.open_archi.persistence.commons.ConfigValues;
import com.araguacaima.open_archi.persistence.commons.exceptions.*;
import com.araguacaima.open_archi.persistence.diagrams.architectural.System;
import com.araguacaima.open_archi.persistence.diagrams.architectural.*;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentElement;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel;
import com.araguacaima.open_archi.persistence.diagrams.component.Group;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.persistence.diagrams.meta.Storable;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Transient;
import javax.transaction.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings({"WeakerAccess", "MismatchedQueryAndUpdateOfCollection"})
public class DBUtil {

    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);
    private static ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();

    static {
        try {
            OrpheusDbJPAEntityManagerUtils.begin();
        } catch (SystemException | NotSupportedException e) {
            e.printStackTrace();
        }
    }

    public DBUtil() {
    }

    public static class Initialize {
        public static void process() throws DBError {
            try {

                /***** CONFIG *****/
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "drools.engine.verbose") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("drools.engine.verbose", "true"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "drools.kie.session.type") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("drools.kie.session.type", "STATELESS"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "drools.kie.session") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("drools.kie.session", "defaultStatelessKieSession"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "scannerPeriod") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("scannerPeriod", "0L"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "rulesPath") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("rulesPath", "11WAEzHEn81enc9kO-gUh_H0IsQ5W4_70riboDIJ9Hcw"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "rules.repository.strategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("rules.repository.strategy", "DECISION_TABLE_SPREADSHEET"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "urlResourceStrategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("urlResourceStrategy", "GOOGLE_DRIVE_DECISION_TABLE_PATH"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "credentialStrategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("credentialStrategy", "SERVER"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "mail.debug") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("mail.debug", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "mail.server.protocol") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("mail.server.protocol", "smtp"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "mail.smtp.auth") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("mail.smtp.auth", "true"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "mail.smtp.starttls.enable") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("mail.smtp.starttls.enable", "true"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(Config.class, "mail.smtp.quitwait") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new Config("mail.smtp.quitwait", "false"));
                }

                // ConfigValues
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "drools.engine.verbose") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("drools.engine.verbose", "true", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "drools.kie.session.type") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("drools.kie.session.type", "STATELESS", "STATEFUL"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "drools.kie.session") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("drools.kie.session", "defaultStatelessKieSession", "defaultKieSession"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "rules.repository.strategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("rules.repository.strategy", "DECISION_TABLE_SPREADSHEET", "DRL"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "urlResourceStrategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("urlResourceStrategy", "GOOGLE_DRIVE_DECISION_TABLE_PATH", "ABSOLUTE_DECISION_TABLE_PATH", "ABSOLUTE_DRL_PATH", "MAVEN", "WORKBENCH"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "credentialStrategy") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("credentialStrategy", "SERVER", "LOCAL", "DB"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "mail.debug") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("mail.debug", "true", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "mail.smtp.auth") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("mail.smtp.auth", "true", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "mail.smtp.starttls.enable") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("mail.smtp.starttls.enable", "true", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "mail.smtp.quitwait") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("mail.smtp.quitwait", "true", "false"));
                }
                if (OrpheusDbJPAEntityManagerUtils.find(ConfigValues.class, "mail.server.protocol") == null) {
                    OrpheusDbJPAEntityManagerUtils.persist(new ConfigValues("mail.server.protocol", "smtp", "imap"));
                }

                /***** ELEMENT SHAPES *****/

                /* ARCHITECTURE_MODEL */

                ColorScheme colorSchemeES_AM_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "DarkSlateGray", "#EAEDED", "#EAEDED"));
                ColorScheme colorSchemeES_AM_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#EAEDED", "DarkSlateGray", "DarkSlateGray"));
                ElementShape elementShape_AM = fixElementShape(colorSchemeES_AM_Primary, colorSchemeES_AM_Secondary, new ElementShape(ElementKind.ARCHITECTURE_MODEL, true, true, null, true));

                /* LAYER */

                ColorScheme colorSchemeES_LA_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "LimeGreen", "#EAFAEA", "#EAFAEA"));
                ColorScheme colorSchemeES_LA_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#EAFAEA", "LimeGreen", "LimeGreen"));
                ElementShape elementShape_LA = fixElementShape(colorSchemeES_LA_Primary, colorSchemeES_LA_Secondary, new ElementShape(ElementKind.LAYER, true, true, null, true));

                /* SYSTEM */

                ColorScheme colorSchemeES_SY_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#02172C", "#E5E7E9", "#E5E7E9"));
                ColorScheme colorSchemeES_SY_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#E5E7E9", "#02172C", "#02172C"));
                ElementShape elementShape_SY = fixElementShape(colorSchemeES_SY_Primary, colorSchemeES_SY_Secondary, new ElementShape(ElementKind.SYSTEM, true, true, null, true));

                /* CONTAINER */

                ColorScheme colorSchemeES_CON_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#08427B", "#E6ECF1", "#E6ECF1"));
                ColorScheme colorSchemeES_CON_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#E6ECF1", "#08427B", "#08427B"));
                ElementShape elementShape_CON = fixElementShape(colorSchemeES_CON_Primary, colorSchemeES_CON_Secondary, new ElementShape(ElementKind.CONTAINER, true, true, null, true));

                /* COMPONENT */
                ColorScheme colorSchemeES_COM_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#1368BD", "#E7EFF8", "#E7EFF8"));
                ColorScheme colorSchemeES_COM_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#E7EFF8", "#1368BD", "#1368BD"));
                ElementShape elementShape_COM = fixElementShape(colorSchemeES_COM_Primary, colorSchemeES_COM_Secondary, new ElementShape(ElementKind.COMPONENT, true, true, null, false));

                /* COMPONENT_MODEL */

                ColorScheme colorSchemeES_CM_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#380B4F", "#ECEAED", "#ECEAED"));
                ColorScheme colorSchemeES_CM_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#ECEAED", "#380B4F", "#380B4F"));
                ElementShape elementShape_CM = fixElementShape(colorSchemeES_CM_Primary, colorSchemeES_CM_Secondary, new ElementShape(ElementKind.COMPONENT_MODEL, true, true, null, true));

                /* GROUP */
                ColorScheme colorSchemeES_GR_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#08427B", "#E6ECF1", "#E6ECF1"));
                ColorScheme colorSchemeES_GR_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#E6ECF1", "#08427B", "#08427B"));
                ElementShape elementShape_GR = fixElementShape(colorSchemeES_GR_Primary, colorSchemeES_GR_Secondary, new ElementShape(ElementKind.GROUP, true, true, null, true));

                /* ELEMENT */
                ColorScheme colorSchemeES_EL_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#1368BD", "#E7EFF8", "#E7EFF8"));
                ColorScheme colorSchemeES_EL_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#E7EFF8", "#1368BD", "#1368BD"));
                ElementShape elementShape_EL = fixElementShape(colorSchemeES_EL_Primary, colorSchemeES_EL_Secondary, new ElementShape(ElementKind.ELEMENT, true, true, null, false));

                /***** DIAGRAM TYPES *****/

                /* ARCHITECTURE_MODEL */

                ColorScheme colorSchemeDT_AM_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#407d7d", "#FFFFFF", "#FFFFFF"));
                ColorScheme colorSchemeDT_AM_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#FFFFFF", "#407d7d", "#407d7d"));
                DiagramType diagramTypeDT_AM = new DiagramType(ElementKind.ARCHITECTURE_MODEL, null, true);
                diagramTypeDT_AM.setId(null);
                Map<String, Object> mapElementShapeDT_AM = buildParametersMap(diagramTypeDT_AM, true);
                String findByAttributesElementShapeDT_AM = fixNativeQuery(DiagramType.FIND_BY_ATTRIBUTES, mapElementShapeDT_AM);
                DiagramType foundElementShapeDT_AM;
                try {
                    foundElementShapeDT_AM = OrpheusDbJPAEntityManagerUtils.findByNativeQuery(DiagramType.class, findByAttributesElementShapeDT_AM);
                } catch (Throwable ignored) {
                    mapElementShapeDT_AM = new HashMap<>();
                    mapElementShapeDT_AM.put("type", diagramTypeDT_AM.getType());
                    foundElementShapeDT_AM = OrpheusDbJPAEntityManagerUtils.findByQuery(DiagramType.class, DiagramType.GET_DIAGRAM_TYPE_BY_TYPE, mapElementShapeDT_AM);
                }
                if (foundElementShapeDT_AM == null) {
                    diagramTypeDT_AM.setId(new ObjectId().toString());
                    Set<ColorScheme> colorSchemesDiagramTypes = new TreeSet<>();
                    colorSchemesDiagramTypes.add(colorSchemeDT_AM_Primary);
                    colorSchemesDiagramTypes.add(colorSchemeDT_AM_Secondary);
                    diagramTypeDT_AM.setColorSchemes(colorSchemesDiagramTypes);
                    Set<ElementShape> elementShapesDiagramTypes = new TreeSet<>();
                    elementShapesDiagramTypes.add(elementShape_AM);
                    elementShapesDiagramTypes.add(elementShape_LA);
                    elementShapesDiagramTypes.add(elementShape_SY);
                    elementShapesDiagramTypes.add(elementShape_CON);
                    elementShapesDiagramTypes.add(elementShape_COM);
                    diagramTypeDT_AM.setElementShapes(elementShapesDiagramTypes);
                    OrpheusDbJPAEntityManagerUtils.merge(diagramTypeDT_AM, true);
                } else {
                    Set<ColorScheme> colorSchemesDiagramTypes = foundElementShapeDT_AM.getColorSchemes();
                    if (CollectionUtils.isEmpty(colorSchemesDiagramTypes)) {
                        colorSchemesDiagramTypes = new TreeSet<>();
                        colorSchemesDiagramTypes.add(colorSchemeDT_AM_Primary);
                        colorSchemesDiagramTypes.add(colorSchemeDT_AM_Secondary);
                        OrpheusDbJPAEntityManagerUtils.merge(foundElementShapeDT_AM, true);
                    }
                    Set<ElementShape> elementShapesDiagramTypes = foundElementShapeDT_AM.getElementShapes();
                    if (CollectionUtils.isEmpty(elementShapesDiagramTypes)) {
                        elementShapesDiagramTypes.add(elementShape_AM);
                        elementShapesDiagramTypes.add(elementShape_LA);
                        elementShapesDiagramTypes.add(elementShape_SY);
                        elementShapesDiagramTypes.add(elementShape_CON);
                        elementShapesDiagramTypes.add(elementShape_COM);
                        OrpheusDbJPAEntityManagerUtils.merge(foundElementShapeDT_AM, true);
                    }
                }

                /* COMPONENT_MODEL */

                ColorScheme colorSchemeDT_CM_Primary = fixColorScheme(new ColorScheme(ColorSchemeOption.PRIMARY, "#FF8E00", "#0A67A3", "#FFFFFF"));
                ColorScheme colorSchemeDT_CM_Secondary = fixColorScheme(new ColorScheme(ColorSchemeOption.SECONDARY, "#0A67A3", "#FF8E00", "#FFFFFF"));
                DiagramType diagramTypeDT_CM = new DiagramType(ElementKind.COMPONENT_MODEL, null, true);
                diagramTypeDT_CM.setId(null);
                Map<String, Object> mapElementShapeDT_CM = buildParametersMap(diagramTypeDT_CM, true);
                String findByAttributesElementShapeDT_CM = fixNativeQuery(DiagramType.FIND_BY_ATTRIBUTES, mapElementShapeDT_CM);
                DiagramType foundElementShapeDT_CM;
                try {
                    foundElementShapeDT_CM = OrpheusDbJPAEntityManagerUtils.findByNativeQuery(DiagramType.class, findByAttributesElementShapeDT_CM);
                } catch (Throwable ignored) {
                    mapElementShapeDT_CM = new HashMap<>();
                    mapElementShapeDT_CM.put("type", diagramTypeDT_CM.getType());
                    foundElementShapeDT_CM = OrpheusDbJPAEntityManagerUtils.findByQuery(DiagramType.class, DiagramType.GET_DIAGRAM_TYPE_BY_TYPE, mapElementShapeDT_CM);
                }
                if (foundElementShapeDT_CM == null) {
                    diagramTypeDT_CM.setId(new ObjectId().toString());
                    Set<ColorScheme> colorSchemesDiagramTypes = new TreeSet<>();
                    colorSchemesDiagramTypes.add(colorSchemeDT_CM_Primary);
                    colorSchemesDiagramTypes.add(colorSchemeDT_CM_Secondary);
                    diagramTypeDT_CM.setColorSchemes(colorSchemesDiagramTypes);
                    Set<ElementShape> elementShapesDiagramTypes = new TreeSet<>();
                    elementShapesDiagramTypes.add(elementShape_CM);
                    elementShapesDiagramTypes.add(elementShape_GR);
                    elementShapesDiagramTypes.add(elementShape_EL);
                    diagramTypeDT_CM.setElementShapes(elementShapesDiagramTypes);
                    OrpheusDbJPAEntityManagerUtils.merge(diagramTypeDT_CM, true);
                } else {
                    Set<ColorScheme> colorSchemesDiagramTypes = foundElementShapeDT_CM.getColorSchemes();
                    if (CollectionUtils.isEmpty(colorSchemesDiagramTypes)) {
                        colorSchemesDiagramTypes = new TreeSet<>();
                        colorSchemesDiagramTypes.add(colorSchemeDT_CM_Primary);
                        colorSchemesDiagramTypes.add(colorSchemeDT_CM_Secondary);
                        OrpheusDbJPAEntityManagerUtils.merge(foundElementShapeDT_CM, true);
                    }
                    Set<ElementShape> elementShapesDiagramTypes = foundElementShapeDT_CM.getElementShapes();
                    if (CollectionUtils.isEmpty(elementShapesDiagramTypes)) {
                        elementShapesDiagramTypes.add(elementShape_CM);
                        elementShapesDiagramTypes.add(elementShape_GR);
                        elementShapesDiagramTypes.add(elementShape_EL);
                        OrpheusDbJPAEntityManagerUtils.merge(foundElementShapeDT_CM, true);
                    }
                }
                //TODO

                /* FLOWCHART_MODEL */
                /* SEQUENCE_MODEL */
                /* GANTT_MODEL */
                /* ENTITY_RELATIONSHIP_MODEL */
                /* UML_CLASS_MODEL */
                /* BPM_MODEL */

            } catch (SystemException | NotSupportedException | HeuristicMixedException | HeuristicRollbackException | RollbackException e) {
                e.printStackTrace();
                throw new DBError(e.getMessage(), e);
            }
        }

        private static ElementShape fixElementShape(ColorScheme colorSchemePrimary, ColorScheme colorSchemeSecondary, ElementShape elementShape) throws NotSupportedException, SystemException {
            elementShape.setId(null);
            Map<String, Object> mapElementShape = buildParametersMap(elementShape, true);
            String findByAttributesElementShape = fixNativeQuery(ElementShape.FIND_BY_ATTRIBUTES, mapElementShape);
            ElementShape foundElementShape;
            try {
                foundElementShape = OrpheusDbJPAEntityManagerUtils.findByNativeQuery(ElementShape.class, findByAttributesElementShape);
            } catch (Throwable ignored) {
                mapElementShape = new HashMap<>();
                mapElementShape.put("type", elementShape.getType());
                foundElementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, mapElementShape);
            }
            if (foundElementShape == null) {
                elementShape.setId(new ObjectId().toString());
                Set<ColorScheme> colorSchemesElementShape = new TreeSet<>();
                colorSchemesElementShape.add(colorSchemePrimary);
                colorSchemesElementShape.add(colorSchemeSecondary);
                elementShape.setColorSchemes(colorSchemesElementShape);
                foundElementShape = OrpheusDbJPAEntityManagerUtils.merge(elementShape, true);
            }
            return foundElementShape;
        }

        private static ColorScheme fixColorScheme(ColorScheme colorScheme) throws NotSupportedException, SystemException {
            colorScheme.setId(null);
            Map<String, Object> mapColorScheme = buildParametersMap(colorScheme, true);
            String findByAttributesColorScheme = fixNativeQuery(ColorScheme.FIND_BY_ATTRIBUTES, mapColorScheme);
            ColorScheme foundColorScheme = null;
            try {
                foundColorScheme = OrpheusDbJPAEntityManagerUtils.findByNativeQuery(ColorScheme.class, findByAttributesColorScheme);
            } catch (Throwable ignored) {
                mapColorScheme = buildParametersMap(colorScheme, false);
                try {
                    foundColorScheme = OrpheusDbJPAEntityManagerUtils.findByQuery(ColorScheme.class, ColorScheme.FIND_BY_ATTRIBUTES_, mapColorScheme);
                } catch (NonUniqueResultException ignored_) {
                }
            }
            if (foundColorScheme == null) {
                colorScheme.setId(new ObjectId().toString());
                foundColorScheme = OrpheusDbJPAEntityManagerUtils.merge(colorScheme, true);
            }
            return foundColorScheme;
        }

        private static String fixNativeQuery(String query, Map<String, Object> map) {
            String regex = "(_\\w+_)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(query);
            while (matcher.find()) {
                String field = matcher.group();
                String key = field.replaceAll("_", StringUtils.EMPTY);
                Object value = map.get(key);
                if (value != null) {
                    query = query.replaceAll(field, value.toString());
                } else {
                    query = query.replaceAll("\\{ *" + key + " : '_" + key + "_' *\\} *,", StringUtils.EMPTY)
                            .replaceAll(", *\\{ *" + key + " : '_" + key + "_' *\\}", StringUtils.EMPTY);
                }
            }
            return query;
        }

        private static Map<String, Object> buildParametersMap(Object o, boolean skipNull) {
            Map<String, Object> map = new HashMap<>();
            ReflectionUtils.doWithFields(o.getClass(), field -> {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(o);
                if (value != null) {
                    if (reflectionUtils.isCollectionImplementation(value.getClass())) {
                        if (CollectionUtils.isNotEmpty((Collection) value)) {
                            map.put(name, value);
                        }
                    } else {
                        map.put(name, value);
                    }
                } else {
                    if (!skipNull) {
                        map.put(name, null);
                    }
                }
            }, Initialize::filterMethod);
            return map;
        }

        public static boolean filterMethod(Field field) {
            if (field.getAnnotation(JsonIgnore.class) != null || field.getAnnotation(Transient.class) != null) {
                return false;
            }
            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                return false;
            }
            Class aClass = null;
            try {
                aClass = reflectionUtils.extractGenerics(field);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return aClass != null;
        }
    }

    public static void persist(BaseEntity entity) throws EntityInsertionError {
        boolean autocommit = OrpheusDbJPAEntityManagerUtils.getAutocommit();
        OrpheusDbJPAEntityManagerUtils.setAutocommit(false);
        try {
            OrpheusDbJPAEntityManagerUtils.begin();
            try {
                boolean alreadyExists = false;
                BaseEntity entity1 = OrpheusDbJPAEntityManagerUtils.find(entity);
                if (entity1 == null) {
                    if (Item.class.isAssignableFrom(entity.getClass())) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("kind", ((Item) entity).getKind());
                        params.put("name", ((Item) entity).getName());
                        entity1 = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                    }
                    if (entity1 == null) {
                        entity1 = entity;
                    } else {
                        entity1.override(entity);
                        alreadyExists = true;
                    }
                } else {
                    entity1.override(entity);
//                    alreadyExists = true;
                }
                if (!alreadyExists) {
                    Class<? extends BaseEntity> clazz = entity1.getClass();
                    if (ArchitecturalModel.class.isAssignableFrom(clazz)) {
                        DBPersist.ArchitecturalModels.process((ArchitecturalModel) entity1);
                    } else if (Layer.class.isAssignableFrom(clazz)) {
                        DBPersist.Layers.process((Layer) entity1);
                    } else if (System.class.isAssignableFrom(clazz)) {
                        DBPersist.Systems.process((System) entity1);
                    } else if (Container.class.isAssignableFrom(clazz)) {
                        DBPersist.Containers.process((Container) entity1);
                    } else if (Component.class.isAssignableFrom(clazz)) {
                        DBPersist.Components.process((Component) entity1);
                    } else if (ComponentModel.class.isAssignableFrom(clazz)) {
                        DBPersist.ComponentModels.process((ComponentModel) entity1);
                    } else if (Group.class.isAssignableFrom(clazz)) {
                        DBPersist.Groups.process((Group) entity1);
                    } else if (ComponentElement.class.isAssignableFrom(clazz)) {
                        DBPersist.ComponentElements.process((ComponentElement) entity1);
                    }
//                } else {
//                    OrpheusDbJPAEntityManagerUtils.merge(entity1);
                }
                OrpheusDbJPAEntityManagerUtils.commit();
            } catch (Throwable t) {
                OrpheusDbJPAEntityManagerUtils.rollback();
                throw new EntityInsertionError(t.getMessage(), t);
            } finally {
                OrpheusDbJPAEntityManagerUtils.setAutocommit(autocommit);
            }
        } catch (Throwable e) {
            throw new EntityInsertionError(e.getMessage(), e);
        }
    }

    public static void update(BaseEntity entity) throws EntityModificationError {
        boolean autocommit = OrpheusDbJPAEntityManagerUtils.getAutocommit();
        OrpheusDbJPAEntityManagerUtils.setAutocommit(false);
        try {
            OrpheusDbJPAEntityManagerUtils.begin();
            Class<?> clazz = entity.getClass();
            Object persistedEntity = OrpheusDbJPAEntityManagerUtils.find(clazz, entity.getId());
            try {
                if (persistedEntity == null) {
                    throw new EntityNotFoundException("Can not replace due object with id '" + entity.getId() + "' does not exists");
                }
                reflectionUtils.invokeMethod(persistedEntity, "copyNonEmpty", new Object[]{entity, true});
                clazz = persistedEntity.getClass();
                if (ArchitecturalModel.class.isAssignableFrom(clazz)) {
                    DBUpdate.ArchitecturalModels.process((ArchitecturalModel) persistedEntity);
                } else if (Layer.class.isAssignableFrom(clazz)) {
                    DBUpdate.Layers.process((Layer) persistedEntity);
                } else if (System.class.isAssignableFrom(clazz)) {
                    DBUpdate.Systems.process((System) persistedEntity);
                } else if (Container.class.isAssignableFrom(clazz)) {
                    DBUpdate.Containers.process((Container) persistedEntity);
                } else if (Component.class.isAssignableFrom(clazz)) {
                    DBUpdate.Components.process((Component) persistedEntity);
                } else if (ComponentModel.class.isAssignableFrom(clazz)) {
                    DBUpdate.ComponentModels.process((ComponentModel) persistedEntity);
                } else if (Group.class.isAssignableFrom(clazz)) {
                    DBUpdate.Groups.process((Group) persistedEntity);
                } else if (ComponentElement.class.isAssignableFrom(clazz)) {
                    DBUpdate.ComponentElements.process((ComponentElement) persistedEntity);
                }
                OrpheusDbJPAEntityManagerUtils.commit();
            } catch (Throwable t) {
                OrpheusDbJPAEntityManagerUtils.rollback();
                throw t;
            } finally {
                OrpheusDbJPAEntityManagerUtils.setAutocommit(autocommit);
            }
        } catch (Throwable e) {
            throw new EntityModificationError(e.getMessage(), e);
        }
    }

    public static void persistComposite(CompositeElement entity) throws EntityCreationError {
        boolean autocommit = OrpheusDbJPAEntityManagerUtils.getAutocommit();
        OrpheusDbJPAEntityManagerUtils.setAutocommit(false);
        try {
            OrpheusDbJPAEntityManagerUtils.begin();
            try {
                OrpheusDbJPAEntityManagerUtils.persist(entity);
            } catch (Throwable t) {
                OrpheusDbJPAEntityManagerUtils.rollback();
                throw t;
            } finally {
                OrpheusDbJPAEntityManagerUtils.commit();
                OrpheusDbJPAEntityManagerUtils.setAutocommit(autocommit);
            }
        } catch (SystemException | NotSupportedException | HeuristicRollbackException | RollbackException | HeuristicMixedException e) {
            e.printStackTrace();
            throw new EntityCreationError(e.getMessage(), e);
        }
    }

    public static void delete(Class<?> clazz, String key) throws EntityDeletionError {
        Object entity = OrpheusDbJPAEntityManagerUtils.find(clazz, (String) key);
        if (entity != null) {
            delete((Storable) entity);
        } else {
            throw new EntityNotFoundException("Can not delete object due it doesn't exists");
        }
    }

    public static void delete(Storable entity) throws EntityDeletionError {
        try {
            if (!OrpheusDbJPAEntityManagerUtils.getEntityManager().contains(entity)) {
                OrpheusDbJPAEntityManagerUtils.merge(entity);
            }
            boolean autocommit = OrpheusDbJPAEntityManagerUtils.getAutocommit();
            OrpheusDbJPAEntityManagerUtils.setAutocommit(false);
            try {
                OrpheusDbJPAEntityManagerUtils.begin();
                Class<?> clazz = entity.getClass();
                Object persistedEntity = OrpheusDbJPAEntityManagerUtils.find(clazz, entity.getId());
                try {
                    if (persistedEntity == null) {
                        throw new EntityNotFoundException("Can not delete object due it doesn't exists");
                    }
                    clazz = persistedEntity.getClass();
                    if (ArchitecturalModel.class.isAssignableFrom(clazz)) {
                        DBDelete.ArchitecturalModels.process((ArchitecturalModel) persistedEntity);
                    } else if (Layer.class.isAssignableFrom(clazz)) {
                        DBDelete.Layers.process((Layer) persistedEntity);
                    } else if (System.class.isAssignableFrom(clazz)) {
                        DBDelete.Systems.process((System) persistedEntity);
                    } else if (Container.class.isAssignableFrom(clazz)) {
                        DBDelete.Containers.process((Container) persistedEntity);
                    } else if (Component.class.isAssignableFrom(clazz)) {
                        DBDelete.Components.process((Component) persistedEntity);
                    } else if (ComponentModel.class.isAssignableFrom(clazz)) {
                        DBDelete.ComponentModels.process((ComponentModel) persistedEntity);
                    } else if (com.araguacaima.open_archi.persistence.diagrams.component.Layer.class.isAssignableFrom(clazz)) {
                        DBDelete.ComponentModelLayers.process((com.araguacaima.open_archi.persistence.diagrams.component.Layer) persistedEntity);
                    } else if (Group.class.isAssignableFrom(clazz)) {
                        DBDelete.Groups.process((Group) persistedEntity);
                    } else if (ComponentElement.class.isAssignableFrom(clazz)) {
                        DBDelete.ComponentElements.process((ComponentElement) persistedEntity);
                    }
                    OrpheusDbJPAEntityManagerUtils.commit();
                } catch (Throwable t) {
                    OrpheusDbJPAEntityManagerUtils.rollback();
                    throw t;
                } finally {
                    OrpheusDbJPAEntityManagerUtils.setAutocommit(autocommit);
                }
            } catch (Throwable e) {
                throw new EntityDeletionError(e.getMessage(), e);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new EntityDeletionError(e.getMessage(), e);
        }
    }
}
