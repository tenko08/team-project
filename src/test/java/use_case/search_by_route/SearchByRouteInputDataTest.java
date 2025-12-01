package use_case.search_by_route;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearchByRouteInputDataTest {

    @Test
    public void testConstructorAndGetter() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Assert
        assertNotNull(inputData);
        assertEquals("36", inputData.getRouteNumber());
    }

    @Test
    public void testConstructorWithNull() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData(null);

        // Assert
        assertNotNull(inputData);
        assertNull(inputData.getRouteNumber());
    }

    @Test
    public void testConstructorWithEmptyString() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData("");

        // Assert
        assertNotNull(inputData);
        assertEquals("", inputData.getRouteNumber());
    }

    @Test
    public void testConstructorWithWhitespace() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData("  501  ");

        // Assert
        assertNotNull(inputData);
        assertEquals("  501  ", inputData.getRouteNumber());
    }

    @Test
    public void testConstructorWithNumericString() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData("501");

        // Assert
        assertEquals("501", inputData.getRouteNumber());
    }

    @Test
    public void testConstructorWithNonNumericString() {
        // Arrange & Act
        SearchByRouteInputData inputData = new SearchByRouteInputData("abc");

        // Assert
        assertEquals("abc", inputData.getRouteNumber());
    }

    @Test
    public void testMultipleInstances() {
        // Arrange & Act
        SearchByRouteInputData input1 = new SearchByRouteInputData("36");
        SearchByRouteInputData input2 = new SearchByRouteInputData("501");
        SearchByRouteInputData input3 = new SearchByRouteInputData("95");

        // Assert
        assertEquals("36", input1.getRouteNumber());
        assertEquals("501", input2.getRouteNumber());
        assertEquals("95", input3.getRouteNumber());
    }

    @Test
    public void testImmutability() {
        // Arrange
        SearchByRouteInputData inputData = new SearchByRouteInputData("36");

        // Act
        String routeNumber = inputData.getRouteNumber();

        // Assert - verify that the value doesn't change
        assertEquals("36", routeNumber);
        assertEquals("36", inputData.getRouteNumber());
    }
}

