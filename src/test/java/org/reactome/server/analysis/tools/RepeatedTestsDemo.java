package org.reactome.server.analysis.tools;

import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepeatedTestsDemo {
    private static Logger logger = Logger.getLogger(RepeatedTestsDemo.class.getName());

    @BeforeEach
    void beforeEach(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        int totalRepetitions = repetitionInfo.getTotalRepetitions();
        String methodName = testInfo.getTestMethod().get().getName();
        logger.info(String.format("About to execute repetition %d of %d for %s",
                currentRepetition,
                totalRepetitions,
                methodName));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Hello", "World" })
    void testWithStringParameter(String argument) {
        System.out.println("Testing with parameter: " + argument);
        assertNotNull(argument);
    }

    @RepeatedTest(5)
    @Disabled
    void repeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        assertEquals(5, repetitionInfo.getTotalRepetitions());
    }

    @RepeatedTest(value = 1, name = "{displayName} {currentRepetition} / {totalRepetitions}")
    @DisplayName("Repeat!")
    @Ignore
    void customDisplayName(TestInfo testInfo) {
        assertEquals("Repeat! 1 / 1", testInfo.getDisplayName() );
    }

    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
    @DisplayName("Details...")
    void customDisplayNameWithLongPattern(TestInfo testInfo) {
        //assertEquals("Details... :: repetition 1 of 1", testInfo.getDisplayName());
        System.out.println(testInfo.getDisplayName());
        assertTrue(testInfo.getDisplayName().startsWith("Details... :: repetition "));
    }

    @RepeatedTest(value = 5, name = "Wiederholung {currentRepetition} von {totalRepetitions}")
    void repeatedTestInGerman() {

    }

    @RepeatedTest(value = 5, name = "繰り返し {currentRepetition} / {totalRepetitions}")
    void repeatedTestInJapanese() {

    }
}
