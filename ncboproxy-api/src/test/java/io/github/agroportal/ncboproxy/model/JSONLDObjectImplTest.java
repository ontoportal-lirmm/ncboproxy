package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.Json;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSONLDObjectImplTest {
    private JSONLDObject jsonldObject;
    private JSONLDObject jsonldObject2;


    @Before
    public void setUp() throws Exception {
        final String objectFileContent;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NCBOPaginatedCollectionImplTest.class.getResourceAsStream("/collectionObject.json")))) {
            objectFileContent = bufferedReader
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        jsonldObject = JSONLDObject.create(Json
                .parse(objectFileContent)
                .asObject());
        jsonldObject2 = JSONLDObject.create(Json
                .parse(objectFileContent)
                .asObject());
    }

    @Test
    public void getLinks() {
        final List<JSONLDLink> links = jsonldObject.getLinks();
        assert !links.isEmpty();
        for (final JSONLDLink link : links) {
            assert (link.getLinkName() != null) && !link
                    .getLinkName()
                    .isEmpty();
            assert link.getLinkValue() != null;
        }
    }

    @Test
    public void getStringValue() {
        final Optional<String> value = jsonldObject.getStringValue("prefLabel");
        assert value.isPresent();
        assert "Question de grossesse".equals(value.get());
        assert !jsonldObject
                .getStringValue("cuis")
                .isPresent();
    }

    @Test
    public void getBooleanValue() {
        final Optional<Boolean> obsoleteValue = jsonldObject.getBooleanValue("obsolete");
        assert obsoleteValue.isPresent() && obsoleteValue.get();
        assert !jsonldObject
                .getBooleanValue("cuis")
                .isPresent();
    }

    @Test
    public void getObject() {
        assert !jsonldObject.getObject("cuis").isPresent();
    }

    @Test
    public void getCollection() {
        final Optional<NCBOCollection> ncboCollection = jsonldObject.getCollection("cui");
        assert ncboCollection.isPresent();
        //.stream().map(JsonValue::asString).collect(Collectors.toList()).contains("C0425965");
        assert ncboCollection
                .get()
                .asStringStream()
                .anyMatch(str -> str.equals("C0425965"));
    }

    @Test
    public void expandFieldFromContext() throws URISyntaxException {
        assert jsonldObject
                .expandFieldFromContext("cui")
                .toASCIIString()
                .equals("http://bioportal.bioontology.org/ontologies/umls/cui");
    }

    @Test
    public void getModelRoot() {
    }

    @Test
    public void isPaginatedCollection() {
        assert !jsonldObject.isPaginatedCollection();
    }

    @Test
    public void isCollection() {
        assert !jsonldObject.isCollection();
    }

    @Test
    public void isObject() {
        assert jsonldObject.isObject();
    }

    @Test
    public void equals() {
        assert jsonldObject.equals(jsonldObject2);
    }
}