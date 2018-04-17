package io.github.agroportal.ncboproxy.model;

import com.eclipsesource.json.Json;
import io.github.agroportal.ncboproxy.APIContext;
import io.github.agroportal.ncboproxy.APIContextImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@SuppressWarnings("all")
public class NCBOPaginatedCollectionImplTest {

    static final int TOTAL_SIZE = 745;
    static final int NUMBER_OF_PAGES = 15;
    NCBOPaginatedCollection paginatedCollection;

    APIContext apiContext;

    @Before
    public void setUp() throws Exception {
         apiContext = new APIContextImpl("1de0a270-29c5-4dda-b043-7c3580628cd5","utf-8", "","GET","/");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NCBOPaginatedCollectionImplTest.class.getResourceAsStream("/collectionPage.json")));
        final String fileContent = bufferedReader.lines().collect(Collectors.joining("\n"));
        paginatedCollection = new NCBOPaginatedCollectionImpl(Json.parse(fileContent).asObject(),apiContext);
    }

    @Test(expected = IOException.class)
    public void testGetPreviousPage() throws IOException {
        paginatedCollection.getPreviousPage();
    }


    @Test
    public void testGetNextPage() throws IOException {
        assert paginatedCollection
                .getNextPage()
                .getCurrentPageNumber() == (paginatedCollection.getCurrentPageNumber() + 1);
    }

    @Test
    public void testGetNumberOfPages() {
        Assert.assertEquals(NUMBER_OF_PAGES, paginatedCollection.getNumberOfPages());
    }

    @Test
    public void testGetCurrentPageNumber() {
        Assert.assertEquals(1, paginatedCollection.getCurrentPageNumber());
    }

    @Test
    public void testGetTotalSize() {
        Assert.assertEquals(TOTAL_SIZE, paginatedCollection.getTotalSize());
    }

    @Test
    public void testIsPaginatedCollection() {
        assert paginatedCollection.isPaginatedCollection();
    }
}