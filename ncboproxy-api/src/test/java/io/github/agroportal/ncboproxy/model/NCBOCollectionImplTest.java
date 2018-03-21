package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.Json;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class NCBOCollectionImplTest {

    private NCBOCollection collection;
    private NCBOCollection emptyCollection;

    private JSONLDObject jsonldObject;

    @Before
    public void setUp() throws Exception {
        final String fileContent;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NCBOPaginatedCollectionImplTest.class.getResourceAsStream("/collection.json")))) {
            fileContent = bufferedReader
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        collection = new NCBOCollectionImpl(Json.parse(fileContent).asArray());

        emptyCollection = new NCBOCollectionImpl(Json.parse("[]").asArray());
        final String objectFileContent;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NCBOPaginatedCollectionImplTest.class.getResourceAsStream("/collectionObject.json")))) {
            objectFileContent = bufferedReader
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        jsonldObject = JSONLDObject.create(Json.parse(objectFileContent).asObject());
    }

    @Test(expected = com.eclipsesource.json.ParseException.class)
    public void errorHandling() throws IOException {
        final String fileContent;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NCBOPaginatedCollectionImplTest.class.getResourceAsStream("/invalidcollection.json")))) {
            fileContent = bufferedReader
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        collection = new NCBOCollectionImpl(Json.parse(fileContent).asArray());
    }

    @Test
    public void isCollection() {
        assert collection.isCollection();
    }

    @Test
    public void size() {
        assert !collection.isEmpty();
        assert emptyCollection.isEmpty();
    }

    @Test
    public void isEmpty() {
        assert !collection.isEmpty();
        assert emptyCollection.isEmpty();
    }

    @Test
    public void contains() {
        assert collection.contains(jsonldObject);
        assert !emptyCollection.contains(jsonldObject);
    }

    @Test
    public void iterator() {
        for(final NCBOOutputModel object: collection){
            assert object.isObject();
        }

        for(final NCBOOutputModel ignored: emptyCollection){
            assert false;
        }
    }

    @Test
    public void toArray() {
    }

    @Test
    public void toArray1() {
    }

    @Test
    public void add() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void containsAll() {
    }

    @Test
    public void addAll() {
    }

    @Test
    public void removeAll() {
    }

    @Test
    public void retainAll() {
    }

    @Test
    public void clear() {
    }
}