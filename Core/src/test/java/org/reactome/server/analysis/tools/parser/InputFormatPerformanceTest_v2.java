package org.reactome.server.analysis.tools.parser;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.parser.InputFormat;
import org.reactome.server.analysis.parser.InputFormat_v2;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.reactome.server.analysis.parser.util.TimingExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.reactome.server.analysis.parser.util.ConstantHolder.*;

@ExtendWith(TimingExtension.class)
class InputFormatPerformanceTest_v2 {

    @RepeatedTest(REPETITIONS)
    void parseCorrectFileExtendedTest() {
        File file = getFileFromResources(CORRECT_FILE);

        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(CORRECT_FILE + " has failed.");
        }

        Assert.assertEquals(6, format.getHeaderColumnNames().size());
        Assert.assertEquals(9, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, format.getWarningResponses().size());
    }

    @RepeatedTest(REPETITIONS)
    void parseCorrectFileTest() {
        File file = getFileFromResources(CORRECT_FILE);

        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(CORRECT_FILE + " has failed.");
        }

        Assert.assertEquals(6, format.getHeaderColumnNames().size());
        Assert.assertEquals(9, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, format.getWarningResponses().size());
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000010() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000010);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000010 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(10, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B2VGW0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B2VGW0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000050() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000050);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000050 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(50, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q9QZH4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9QZH4")));
        Assert.assertTrue("Looking for Q9QZH4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9QZH4")));
        Assert.assertTrue("Looking for B1IU08", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1IU08")));
        Assert.assertTrue("Looking for B5F4V4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5F4V4")));
        Assert.assertTrue("Looking for B2VGW0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B2VGW0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000100() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000100);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000100 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(100, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q9NRW3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9NRW3")));
        Assert.assertTrue("Looking for Q2U9L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2U9L6")));
        Assert.assertTrue("Looking for Q8R0P4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8R0P4")));
        Assert.assertTrue("Looking for Q8XEK3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8XEK3")));
        Assert.assertTrue("Looking for C7J6G6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C7J6G6")));
        Assert.assertTrue("Looking for Q8FD49", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8FD49")));
        Assert.assertTrue("Looking for Q38967", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q38967")));
        Assert.assertTrue("Looking for Q9FT51", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9FT51")));
        Assert.assertTrue("Looking for A1IGV8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A1IGV8")));
        Assert.assertTrue("Looking for Q8GU85", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8GU85")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000150() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000150);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000150 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(150, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q8T6H8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T6H8")));
        Assert.assertTrue("Looking for B5QWU2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5QWU2")));
        Assert.assertTrue("Looking for Q9ESR9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ESR9")));
        Assert.assertTrue("Looking for Q9ZUU9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ZUU9")));
        Assert.assertTrue("Looking for Q52814", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q52814")));
        Assert.assertTrue("Looking for Q2U9L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2U9L6")));
        Assert.assertTrue("Looking for Q8K442", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8K442")));
        Assert.assertTrue("Looking for B4T776", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B4T776")));
        Assert.assertTrue("Looking for Q7TNJ2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7TNJ2")));
        Assert.assertTrue("Looking for Q8T673", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T673")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000200() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000200);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000200 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(200, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q8T6J2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T6J2")));
        Assert.assertTrue("Looking for Q9HC16", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9HC16")));
        Assert.assertTrue("Looking for Q9SW08", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9SW08")));
        Assert.assertTrue("Looking for Q86AG8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q86AG8")));
        Assert.assertTrue("Looking for O95870", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("O95870")));
        Assert.assertTrue("Looking for Q8T673", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T673")));
        Assert.assertTrue("Looking for P41421", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P41421")));
        Assert.assertTrue("Looking for B1IS15", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1IS15")));
        Assert.assertTrue("Looking for Q1JPD2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1JPD2")));
        Assert.assertTrue("Looking for Q96375", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q96375")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000250() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000250);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000250 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(250, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for A3M2B3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A3M2B3")));
        Assert.assertTrue("Looking for A6VUT8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A6VUT8")));
        Assert.assertTrue("Looking for Q4WL66", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4WL66")));
        Assert.assertTrue("Looking for Q42093", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q42093")));
        Assert.assertTrue("Looking for Q2KHT9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2KHT9")));
        Assert.assertTrue("Looking for A6VUT8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A6VUT8")));
        Assert.assertTrue("Looking for Q8N139", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8N139")));
        Assert.assertTrue("Looking for Q4R2Y9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4R2Y9")));
        Assert.assertTrue("Looking for Q9ZR72", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ZR72")));
        Assert.assertTrue("Looking for O34847", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("O34847")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList000500() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000500);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000500 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(500, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B9J0A0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B9J0A0")));
        Assert.assertTrue("Looking for A3PTW3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A3PTW3")));
        Assert.assertTrue("Looking for P32316", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P32316")));
        Assert.assertTrue("Looking for Q9JRV8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9JRV8")));
        Assert.assertTrue("Looking for C1CCJ2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C1CCJ2")));
        Assert.assertTrue("Looking for P48255", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P48255")));
        Assert.assertTrue("Looking for P12217", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P12217")));
        Assert.assertTrue("Looking for B9E7A6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B9E7A6")));
        Assert.assertTrue("Looking for A5FE24", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A5FE24")));
        Assert.assertTrue("Looking for A1TDB8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A1TDB8")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList001000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_001000);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_001000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(1000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q949P1", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q949P1")));
        Assert.assertTrue("Looking for Q8WN94", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8WN94")));
        Assert.assertTrue("Looking for Q9L0L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9L0L6")));
        Assert.assertTrue("Looking for U6A629", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("U6A629")));
        Assert.assertTrue("Looking for Q556W2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q556W2")));
        Assert.assertTrue("Looking for B1MH39", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1MH39")));
        Assert.assertTrue("Looking for P05201", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P05201")));
        Assert.assertTrue("Looking for Q3AC47", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q3AC47")));
        Assert.assertTrue("Looking for P83662", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P83662")));
        Assert.assertTrue("Looking for A9KWZ3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A9KWZ3")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList005000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_005000);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_005000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(5000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q77TI4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q77TI4")));
        Assert.assertTrue("Looking for Q7Z5R6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7Z5R6")));
        Assert.assertTrue("Looking for A8MF35", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A8MF35")));
        Assert.assertTrue("Looking for B5R5I8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5R5I8")));
        Assert.assertTrue("Looking for P10447", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P10447")));
        Assert.assertTrue("Looking for P37877", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P37877")));
        Assert.assertTrue("Looking for Q9AHF0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9AHF0")));
        Assert.assertTrue("Looking for Q91F88", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q91F88")));
        Assert.assertTrue("Looking for P0C6K8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P0C6K8")));
        Assert.assertTrue("Looking for Q9CQJ0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9CQJ0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList010000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_010000);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_010000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(10000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for C0JAR2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C0JAR2")));
        Assert.assertTrue("Looking for Q74CR8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q74CR8")));
        Assert.assertTrue("Looking for A4SGK0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A4SGK0")));
        Assert.assertTrue("Looking for P56413", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P56413")));
        Assert.assertTrue("Looking for Q8S932", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8S932")));
        Assert.assertTrue("Looking for C0ZK19", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C0ZK19")));
        Assert.assertTrue("Looking for Q61694", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q61694")));
        Assert.assertTrue("Looking for P58248", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P58248")));
        Assert.assertTrue("Looking for Q1KXV0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1KXV0")));
        Assert.assertTrue("Looking for B0XQB2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B0XQB2")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList050000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_050000);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_050000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(50000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q5JQC9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q5JQC9")));
        Assert.assertTrue("Looking for A9AAA7", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A9AAA7")));
        Assert.assertTrue("Looking for Q4UVI5", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4UVI5")));
        Assert.assertTrue("Looking for B3LLK7", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B3LLK7")));
        Assert.assertTrue("Looking for K4D9Y4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("K4D9Y4")));
        Assert.assertTrue("Looking for Q9URV6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9URV6")));
        Assert.assertTrue("Looking for Q1JUP6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1JUP6")));
        Assert.assertTrue("Looking for Q4R5M3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4R5M3")));
        Assert.assertTrue("Looking for Q81TW1", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q81TW1")));
        Assert.assertTrue("Looking for P26731", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P26731")));
    }

    @RepeatedTest(REPETITIONS)
    public void testUniprotAccessionList100000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_100000);
        InputFormat format = null;
        try {
            format = parser(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_100000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(100000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B4N1G8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B4N1G8")));
        Assert.assertTrue("Looking for Q9H3H3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9H3H3")));
        Assert.assertTrue("Looking for Q7V9B0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7V9B0")));
        Assert.assertTrue("Looking for B8ZT93", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B8ZT93")));
        Assert.assertTrue("Looking for Q9WVT6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9WVT6")));
        Assert.assertTrue("Looking for Q9WVF9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9WVF9")));
        Assert.assertTrue("Looking for A7THN6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A7THN6")));
        Assert.assertTrue("Looking for Q6NQK9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q6NQK9")));
        Assert.assertTrue("Looking for C1AW01", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C1AW01")));
        Assert.assertTrue("Looking for A7Z1S6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A7Z1S6")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000010() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000010);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000010 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(10, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B2VGW0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B2VGW0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000050() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000050);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000050 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(50, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q9QZH4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9QZH4")));
        Assert.assertTrue("Looking for Q9QZH4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9QZH4")));
        Assert.assertTrue("Looking for B1IU08", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1IU08")));
        Assert.assertTrue("Looking for B5F4V4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5F4V4")));
        Assert.assertTrue("Looking for B2VGW0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B2VGW0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000100() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000100);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000100 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(100, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q9NRW3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9NRW3")));
        Assert.assertTrue("Looking for Q2U9L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2U9L6")));
        Assert.assertTrue("Looking for Q8R0P4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8R0P4")));
        Assert.assertTrue("Looking for Q8XEK3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8XEK3")));
        Assert.assertTrue("Looking for C7J6G6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C7J6G6")));
        Assert.assertTrue("Looking for Q8FD49", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8FD49")));
        Assert.assertTrue("Looking for Q38967", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q38967")));
        Assert.assertTrue("Looking for Q9FT51", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9FT51")));
        Assert.assertTrue("Looking for A1IGV8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A1IGV8")));
        Assert.assertTrue("Looking for Q8GU85", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8GU85")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000150() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000150);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000150 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(150, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q8T6H8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T6H8")));
        Assert.assertTrue("Looking for B5QWU2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5QWU2")));
        Assert.assertTrue("Looking for Q9ESR9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ESR9")));
        Assert.assertTrue("Looking for Q9ZUU9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ZUU9")));
        Assert.assertTrue("Looking for Q52814", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q52814")));
        Assert.assertTrue("Looking for Q2U9L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2U9L6")));
        Assert.assertTrue("Looking for Q8K442", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8K442")));
        Assert.assertTrue("Looking for B4T776", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B4T776")));
        Assert.assertTrue("Looking for Q7TNJ2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7TNJ2")));
        Assert.assertTrue("Looking for Q8T673", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T673")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000200() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000200);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000200 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(200, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q8T6J2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T6J2")));
        Assert.assertTrue("Looking for Q9HC16", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9HC16")));
        Assert.assertTrue("Looking for Q9SW08", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9SW08")));
        Assert.assertTrue("Looking for Q86AG8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q86AG8")));
        Assert.assertTrue("Looking for O95870", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("O95870")));
        Assert.assertTrue("Looking for Q8T673", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8T673")));
        Assert.assertTrue("Looking for P41421", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P41421")));
        Assert.assertTrue("Looking for B1IS15", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1IS15")));
        Assert.assertTrue("Looking for Q1JPD2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1JPD2")));
        Assert.assertTrue("Looking for Q96375", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q96375")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000250() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000250);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000250 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(250, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for A3M2B3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A3M2B3")));
        Assert.assertTrue("Looking for A6VUT8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A6VUT8")));
        Assert.assertTrue("Looking for Q4WL66", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4WL66")));
        Assert.assertTrue("Looking for Q42093", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q42093")));
        Assert.assertTrue("Looking for Q2KHT9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q2KHT9")));
        Assert.assertTrue("Looking for A6VUT8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A6VUT8")));
        Assert.assertTrue("Looking for Q8N139", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8N139")));
        Assert.assertTrue("Looking for Q4R2Y9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4R2Y9")));
        Assert.assertTrue("Looking for Q9ZR72", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9ZR72")));
        Assert.assertTrue("Looking for O34847", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("O34847")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList000500() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_000500);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_000500 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(500, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B9J0A0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B9J0A0")));
        Assert.assertTrue("Looking for A3PTW3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A3PTW3")));
        Assert.assertTrue("Looking for P32316", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P32316")));
        Assert.assertTrue("Looking for Q9JRV8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9JRV8")));
        Assert.assertTrue("Looking for C1CCJ2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C1CCJ2")));
        Assert.assertTrue("Looking for P48255", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P48255")));
        Assert.assertTrue("Looking for P12217", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P12217")));
        Assert.assertTrue("Looking for B9E7A6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B9E7A6")));
        Assert.assertTrue("Looking for A5FE24", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A5FE24")));
        Assert.assertTrue("Looking for A1TDB8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A1TDB8")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList001000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_001000);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_001000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(1000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q949P1", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q949P1")));
        Assert.assertTrue("Looking for Q8WN94", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8WN94")));
        Assert.assertTrue("Looking for Q9L0L6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9L0L6")));
        Assert.assertTrue("Looking for U6A629", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("U6A629")));
        Assert.assertTrue("Looking for Q556W2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q556W2")));
        Assert.assertTrue("Looking for B1MH39", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B1MH39")));
        Assert.assertTrue("Looking for P05201", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P05201")));
        Assert.assertTrue("Looking for Q3AC47", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q3AC47")));
        Assert.assertTrue("Looking for P83662", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P83662")));
        Assert.assertTrue("Looking for A9KWZ3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A9KWZ3")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList005000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_005000);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_005000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(5000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q77TI4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q77TI4")));
        Assert.assertTrue("Looking for Q7Z5R6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7Z5R6")));
        Assert.assertTrue("Looking for A8MF35", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A8MF35")));
        Assert.assertTrue("Looking for B5R5I8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B5R5I8")));
        Assert.assertTrue("Looking for P10447", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P10447")));
        Assert.assertTrue("Looking for P37877", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P37877")));
        Assert.assertTrue("Looking for Q9AHF0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9AHF0")));
        Assert.assertTrue("Looking for Q91F88", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q91F88")));
        Assert.assertTrue("Looking for P0C6K8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P0C6K8")));
        Assert.assertTrue("Looking for Q9CQJ0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9CQJ0")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList010000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_010000);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_010000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(10000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for C0JAR2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C0JAR2")));
        Assert.assertTrue("Looking for Q74CR8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q74CR8")));
        Assert.assertTrue("Looking for A4SGK0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A4SGK0")));
        Assert.assertTrue("Looking for P56413", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P56413")));
        Assert.assertTrue("Looking for Q8S932", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q8S932")));
        Assert.assertTrue("Looking for C0ZK19", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C0ZK19")));
        Assert.assertTrue("Looking for Q61694", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q61694")));
        Assert.assertTrue("Looking for P58248", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P58248")));
        Assert.assertTrue("Looking for Q1KXV0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1KXV0")));
        Assert.assertTrue("Looking for B0XQB2", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B0XQB2")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList050000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_050000);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_050000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(50000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for Q5JQC9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q5JQC9")));
        Assert.assertTrue("Looking for A9AAA7", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A9AAA7")));
        Assert.assertTrue("Looking for Q4UVI5", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4UVI5")));
        Assert.assertTrue("Looking for B3LLK7", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B3LLK7")));
        Assert.assertTrue("Looking for K4D9Y4", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("K4D9Y4")));
        Assert.assertTrue("Looking for Q9URV6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9URV6")));
        Assert.assertTrue("Looking for Q1JUP6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q1JUP6")));
        Assert.assertTrue("Looking for Q4R5M3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q4R5M3")));
        Assert.assertTrue("Looking for Q81TW1", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q81TW1")));
        Assert.assertTrue("Looking for P26731", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P26731")));
    }

    @RepeatedTest(REPETITIONS)
    public void testExtendedUniprotAccessionList100000() throws ParserException {

        File file = getFileFromResources(UNIPROT_ACCESSION_LIST_100000);
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST_100000 + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(100000, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
        Assert.assertTrue("Looking for B4N1G8", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B4N1G8")));
        Assert.assertTrue("Looking for Q9H3H3", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9H3H3")));
        Assert.assertTrue("Looking for Q7V9B0", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q7V9B0")));
        Assert.assertTrue("Looking for B8ZT93", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("B8ZT93")));
        Assert.assertTrue("Looking for Q9WVT6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9WVT6")));
        Assert.assertTrue("Looking for Q9WVF9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9WVF9")));
        Assert.assertTrue("Looking for A7THN6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A7THN6")));
        Assert.assertTrue("Looking for Q6NQK9", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q6NQK9")));
        Assert.assertTrue("Looking for C1AW01", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C1AW01")));
        Assert.assertTrue("Looking for A7Z1S6", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A7Z1S6")));
    }

    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000001() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00001.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00001.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(1, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000010() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00010.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00010.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(10, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000050() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00050.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00050.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(50, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000100() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00100.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00100.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(100, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000150() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00150.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00150.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(150, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000200() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00200.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00200.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(200, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000250() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00250.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00250.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(250, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_000500() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "00500.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "00500.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(500, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_001000() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "01000.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "01000.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(998, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }


    @RepeatedTest(REPETITIONS)
    public void testPROTEOFORMS_005000() throws ParserException {
        File file = getFileFromResources(PATH_PROTEOFORMS_SIMPLE + "05000.txt");
        InputFormat_v2 format = null;
        try {
            format = parserExtended(file);
        } catch (ParserException e) {
            Assert.fail(PATH_PROTEOFORMS_SIMPLE + "05000.txt" + " has failed.");
        }

        Assert.assertEquals(1, format.getHeaderColumnNames().size());
        Assert.assertEquals(4965, format.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, format.getWarningResponses().size());
    }

    private InputFormat parser(File file) throws ParserException {
        InputFormat format = new InputFormat();

        try {
            InputStream fis = new FileInputStream(file);
            format.parseData(IOUtils.toString(fis));
        } catch (IOException e) {
            Assert.fail("Couldn't get the file to be analysed properly. File [".concat(file.getName()).concat("]"));
        }

        return format;
    }

    private InputFormat_v2 parserExtended(File file) throws ParserException {
        InputFormat_v2 format = new InputFormat_v2();

        try {
            InputStream fis = new FileInputStream(file);
            format.parseData(IOUtils.toString(fis));
        } catch (IOException e) {
            Assert.fail("Couldn't get the file to be analysed properly. File [".concat(file.getName()).concat("]"));
        }

        return format;
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