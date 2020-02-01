package com.araguacaima.open_archi.persistence.diagrams;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import com.araguacaima.braas.core.drools.DroolsConfig;
import com.araguacaima.braas.core.drools.DroolsUtils;
import com.araguacaima.commons.utils.JsonUtils;
import com.araguacaima.open_archi.persistence.commons.Config;
import com.araguacaima.open_archi.persistence.commons.ConfigWrapper;
import com.araguacaima.open_archi.persistence.diagrams.architectural.Container;
import com.araguacaima.open_archi.persistence.diagrams.core.Image;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

@SuppressWarnings("unchecked")
//@Ignore
public class ContainerValidationTest {

    private static final String CONTAINER_IMAGE_CANNOT_BE_NULL = "Container's image cannot be null";
    private static final String CONTAINER_NAME_CANNOT_BE_NULL = "Item's name cannot be null";
    private static final String CONTAINER_NAME_CANNOT_BE_EMPTY = "Item's name cannot be empty";
    private static final String CONTAINER_NAME_IS_NOT_IN_CAPITAL_CASE = "Item's name should start with capital case";
    private static Logger log = LoggerFactory.getLogger(ContainerValidationTest.class);
    private JsonUtils jsonUtils;
    private Locale locale = Locale.ENGLISH;
    private Predicate predicateMessage = object -> RuleMessage.class.isAssignableFrom(object.getClass());
    private Predicate transformerLocalizedComments = input -> {
        IMessage message = (IMessage) input;
        String language = message.getLanguage();
        String localeLanguage = locale.getLanguage();
        return localeLanguage.equals(language);
    };
    private DroolsUtils droolsUtils = null;
    private Container container;

    @SuppressWarnings("ConstantConditions")
    @Before
    public void init() throws FileNotFoundException, MalformedURLException, URISyntaxException, IllegalAccessException {
        Collection<Config> configs = OrpheusDbJPAEntityManagerUtils.executeQuery(Config.class, Config.FIND_ALL);
        Properties properties = ConfigWrapper.toProperties(configs);
        DroolsConfig droolsConfig = new DroolsConfig(properties);
        droolsUtils = new DroolsUtils(droolsConfig);
        jsonUtils = new JsonUtils();
        container = new Container();
    }

    @Test
    public void testContainerNameNullity() throws Exception {

        container.setName(null);
        Collection comments = droolsUtils.executeRules(container);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due name is null, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due name is null");
        CollectionUtils.filter(comments, comment -> CONTAINER_NAME_CANNOT_BE_NULL.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testContainerNameEmpty() throws Exception {

        container.setName("");
        Collection comments = droolsUtils.executeRules(container);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due name is empty, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due name is empty");
        CollectionUtils.filter(comments, comment -> CONTAINER_NAME_CANNOT_BE_EMPTY.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testContainerNameUppercase() throws Exception {
        container.setName("test");
        Collection comments = droolsUtils.executeRules(container);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due name is not in capital case");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due name is not in capital case");
        CollectionUtils.filter(comments, comment -> CONTAINER_NAME_IS_NOT_IN_CAPITAL_CASE.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }


    @Test
    public void testNullContainer() throws Exception {
        Collection<Object> result = droolsUtils.executeRules(null);
        Collection comments = new ArrayList();
        if (result != null) {
            if (result.size() > 0) {
                Assert.fail("It should not be returned any error message due there is no facts provided, but some errors was retrieved");
                log.info(jsonUtils.toJSON(comments));
            }
        }
        log.debug("It should not be returned any error message due there is no facts provided");
        Assert.assertEquals(0, comments.size());
        log.info("Amount of message returned: " + comments.size());
    }

    @Test
    public void testContainerImageNullity() throws Exception {

        container.setImage(null);
        Collection comments = droolsUtils.executeRules(container);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due image is null, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due image is null");
        CollectionUtils.filter(comments, comment -> CONTAINER_IMAGE_CANNOT_BE_NULL.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testValidateContainer()
            throws Exception {
        container.setName("Test");
        container.setImage(new Image());
        Collection<Object> result = droolsUtils.executeRules(container);
        Collection comments = new ArrayList();
        if (result.size() == 1) {
            comments = getMessages(result);
        } else {
            Assert.fail("It should not be returned an error message due container provided is valid, but some error was retrieved");
            log.info(jsonUtils.toJSON(comments));
        }
        log.debug("It should not be returned an error message due container provided is valid");
        Assert.assertEquals(0, comments.size());
        log.info("Amount of message returned: " + comments.size());
    }

    private Collection getMessages(Collection result) {
        Collection collection = CollectionUtils.select(result, predicateMessage);
        CollectionUtils.filter(collection, transformerLocalizedComments);
        return collection;
    }
}
