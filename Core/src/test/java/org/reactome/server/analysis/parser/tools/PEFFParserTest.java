package org.reactome.server.analysis.parser.tools;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactome.server.analysis.parser.util.TimingExtension;

import java.io.File;
import java.net.URL;

import static org.reactome.server.analysis.parser.util.ConstantHolder.REPETITIONS;

@ExtendWith(TimingExtension.class)
class PEFFParserTest {

    private static final String PATH = "analysis/parser/PEFF/";

    @RepeatedTest(REPETITIONS)
    @DisplayName("Tiny valid example")
    void isValid_Tiny_Example_true() {
        File file = getFileFromResources(PATH.concat("Valid/PEFF_Tiny_Valid.peff"));
        Assert.assertTrue("The file is valid", PEFFParser.isValid(file));
    }

    @RepeatedTest(REPETITIONS)
    @DisplayName("Minimal valid example")
    void isValid_Minimal_Example_true() {
        File file = getFileFromResources(PATH.concat("Valid/PEFF_Minimal_Valid.peff"));
        Assert.assertTrue("The file is valid", PEFFParser.isValid(file));
    }

    @RepeatedTest(REPETITIONS)
    @DisplayName("Tiny invalid example")
    void isValid_Tiny_Example_false() {
        File file = getFileFromResources(PATH.concat("Invalid/PEFF_Tiny_INValid1.peff"));
        Assert.assertFalse("The file is invalid", PEFFParser.isValid(file));
    }

    @RepeatedTest(REPETITIONS)
    @DisplayName("Minimal invalid example")
    void isValid_Minimal_Example_false() {
        File file = getFileFromResources(PATH.concat("Invalid/PEFF_Minimal_INValid1.peff"));
        Assert.assertFalse("The file is invalid", PEFFParser.isValid(file));
    }

    @RepeatedTest(REPETITIONS)
    @DisplayName("Uses lower case valid")
    void isValid_lowerCase_true(){

    }



    private File getFileFromResources(String fileName) {
        String msg = "Can't get an instance of ".concat(fileName);

        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            Assert.fail("[1] - ".concat(msg));
        }

        URL url = classLoader.getResource(fileName);
        if (url == null) {
            Assert.fail("[2] - ".concat(msg));
        }

        File file = new File(url.getFile());
        if (!file.exists()) {
            Assert.fail("[3] - ".concat(msg));
        }

        return file;
    }

}