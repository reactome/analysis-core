package org.reactome.server.analysis.tools;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParameterizedTestsDemo {

    @ParameterizedTest
    @ValueSource(strings = { "Hello", "World" })
    void testWithStringParameter(String argument) {
        System.out.println("Testing with parameter: " + argument);
        assertNotNull(argument);
    }

}
