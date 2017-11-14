package org.reactome.server.analysis.parser;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.exception.ParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.reactome.server.analysis.parser.util.ConstantHolder.PATH;
import static org.reactome.server.analysis.parser.util.FileUtils.getString;

class ProteoformsCustomTest {

    /**
     * Format rules:
     * - One proteoform per line
     * - There is semi-colon (';') after each protein accession.
     * - PTMs are expressed using two elements: psi mod id (five digits) and an integer coordinate.
     * - The coordinate can have a value between 0 and 99,999,999,999 or "null".
     * - PTMs match this regex: "\d{5}:(\d{1,11}|[Nn][Uu][Ll][Ll])"
     * - PTMS are separated using (',').
     * - A proteoform matches this pattern: ID;PTM,PTM...PTM
     * where ID stands for the Uniprot Accession including the optional isoform specification.
     * <p>
     * For single line files the rules are:
     * - No headers
     * - No expressions values
     * - Cannot start with # or comments
     * - Must match this "regular expression" (\delim)*ID((\delimID)* | (\delimNUMBER)* ),
     * where \delim can be a whitespace character (\s), and ID stands for an Identifier of any type.
     * - Does not accept comma or semi-colon as separators like the original input for proteins.
     * <p>
     * For multiple line files the rules are:
     * - Can contain headers in the first line.
     * - Headers must start with # or //.
     * - Headers follow this regex "(#|//).*([\t,;]+(#|//).*)*", where .* stands for the header text itself.
     * - Can contain expression values
     */

    private static final String PROTEOFORM_CUSTOM = PATH.concat("proteoforms_custom.txt");
    private static final String PROTEOFORM_CUSTOM_ONE_PROTEIN = PATH.concat("proteoforms_custom_one_protein.txt");

    private static final String PATH_VALID = "target/test-classes/analysis/input/ProteoformsCustom/Valid/";
    private static final String PATH_INVALID = "target/test-classes/analysis/input/ProteoformsCustom/Invalid/";

    static List<AnalysisIdentifier> aiSet;
    static List<AnalysisIdentifier> aiSetWithNull;

    private static AnalysisIdentifier ai_P12345_2 = new AnalysisIdentifier("P12345-2");

    @Test
    @Tag("Valid")
    void oneLineWithManySpacesTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithManySpaces.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(5, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSet) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Invalid")
    /*
    This input is invalid for the proteoform format, but it is accepted as a type of input
    as a one line file separated by coma and semi colon.
     */
    void oneLineWithComasTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "oneLineWithComas.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(10, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        Assert.assertTrue("Looking for 00084:382", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("00084:382")));

    }

    @Test
    @Tag("Valid")
    void oneLineWithTabsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithTabs.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(5, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSet) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
        // Whether it has the semi-colon or not, the single uniprot accession should be accepted.
    void oneLineOnlyUniprotAccessionsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithOneUniprot.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        Assert.assertTrue("Looking for P10412", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("P10412")));
    }

    @Test
    @Tag("Valid")
    void oneLineProteinWithOnePTMTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineProteinWithOnePTM.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        AnalysisIdentifier ai = new AnalysisIdentifier("P56524");
        ai.addPtm("00916", 559L);
        Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
    }

    @Test
    @Tag("Valid")
    void oneLineProteinWithMultiplePTMsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineProteinWithMultiplePTMs.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(11, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSet) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineNullCoordinateTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineNullCoordinate.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(4, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithNull) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineHasIsoformTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineHasIsoform.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        AnalysisIdentifier ai = new AnalysisIdentifier("P56524-2");
        ai.addPtm("00916", 559L);
        Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
    }

    @Test
    @Tag("Valid")
    void oneLineHasIsoformAndPTMsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineHasIsoformAndPTMs.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(2, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        AnalysisIdentifier ai = new AnalysisIdentifier("P12345-2");
        ai.addPtm("00916", 246L);
        ai.addPtm("00916", 467L);
        ai.addPtm("00916", 632L);
        Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
    }

    @Test
    @Tag("Valid")
        // Is valid as it becomes a multiline file: 1 header and 1 for content
    void oneLineWithHeadersTest(TestInfo testInfo) {

        String data = getString(PATH_VALID + "oneLineWithHeaders.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        Assert.assertTrue("Looking for " + ai_P12345_2.toString(), p.getAnalysisIdentifierSet().contains(ai_P12345_2));

    }

    @Test
    @Tag("Invalid")
    void oneLineStartsWithHashTest(TestInfo testInfo) {

        String data = getString(PATH_INVALID + "oneLineStartsWithHash.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Expecting start with comment", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    @Tag("Invalid")
    void oneLineStartsWithComment(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "oneLineStartsWithComment.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Expecting start with comment", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLineTest() {


    }

    @Test
    @Tag("Valid")
    void multipleLinesWithExpressionValuesTest() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesOnlyUniprotAccessionsTest() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesProteinWithMultiplePTMsTest() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesNullCoordinateTest() {

    }

    @Test
    @Tag("Valid")
    void unkownPTMTypeTest() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesWithHeaders() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesHasIsoformTest() {

    }

    @Test
    @Tag("Valid")
    void multipleLinesHasIsoformAndPTMsTest() {

    }

    @Test
    @Tag("Invalid")
    void multipleLinesBrokenFileTest(TestInfo testInfo){
        String data = getString(PATH_INVALID + "multipleLinesBrokenFile.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Should have less columns than first line.", e.getErrorMessages().contains("Line 2 does not have 5 column(s). 1 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 3 does not have 5 column(s). 8 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 7 does not have 5 column(s). 9 Column(s) found."));
        }
    }

    @org.junit.Test
    public void testProteoformsCustom() {
        String data = getString(PROTEOFORM_CUSTOM);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(PROTEOFORM_CUSTOM + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(11, p.getAnalysisIdentifierSet().size());

        AnalysisIdentifier ai = new AnalysisIdentifier("P10412");
        Assert.assertTrue("Looking for P10412", p.getAnalysisIdentifierSet().contains(ai));    //When asking for contains, it checks that the id and the ptms are equal.

        ai = new AnalysisIdentifier("P10412-1");
        Assert.assertTrue("Looking for P10412-1", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("P56524");
        MapList<String, Long> ptms = new MapList<>();
        ptms.add("00916", (long) 559);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for P56524;559:00916", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("P04637");
        ptms.clear();
        ptms.add("00084", (long) 370);
        ptms.add("00084", (long) 382);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for P04637;370:00084,382:00084", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("P56524");
        ptms.clear();
        ptms.add("00916", (long) 246);
        ptms.add("00916", (long) 467);
        ptms.add("00916", (long) 632);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for P56524;246:00916,467:00916,632:00916", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("P12345-2");
        ptms.clear();
        ptms.add("00916", (long) 246);
        ptms.add("00916", (long) 467);
        ptms.add("00916", (long) 632);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for P12345-2;246:00916,467:00916,632:00916", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("Q1AAA9");
        Assert.assertTrue("Looking for Q1AAA9", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("O456A1");
        Assert.assertTrue("Looking for O456A1", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("P4A123");
        Assert.assertTrue("Looking for P4A123", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("A0A022YWF9");
        Assert.assertTrue("Looking for A0A022YWF9", p.getAnalysisIdentifierSet().contains(ai));

        ai = new AnalysisIdentifier("A0A022YWF9");
        ptms.clear();
        ptms.add("00916", (long) 632);
        ptms.add("00916", (long) 246);
        ptms.add("00916", (long) 467);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for A0A022YWF9;246:00916,467:00916,632:00916", p.getAnalysisIdentifierSet().contains(ai));

        Assert.assertEquals(1, p.getWarningResponses().size());    // Expected: Missing header. Using a default one.
        // If the file ending is CFLF then 12 are expected, because of empty extra lines.
    }

    @org.junit.Test
    public void testProteoformsCustomOneProtein() {
        String data = getString(PROTEOFORM_CUSTOM_ONE_PROTEIN);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(PROTEOFORM_CUSTOM_ONE_PROTEIN + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        AnalysisIdentifier ai = new AnalysisIdentifier("P56524");
        MapList<String, Long> ptms = new MapList<>();
        ptms.add("00916", (long) 246);
        ptms.add("00916", (long) 467);
        ptms.add("00916", (long) 632);
        ai.setPtms(ptms);
        Assert.assertTrue("Looking for P56524;246:00916,467:00916,632:00916", p.getAnalysisIdentifierSet().contains(ai));
        Assert.assertEquals(0, p.getWarningResponses().size());
    }

    @BeforeAll
    public static void setUp() {
        aiSet = new ArrayList<>();

        AnalysisIdentifier ai = new AnalysisIdentifier("P10412");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P10412-1");
        aiSet.add(ai);

        //P56524;00916:559
        ai = new AnalysisIdentifier("P56524");
        ai.addPtm("00916", (long) 559);
        aiSet.add(ai);

        //P04637;00084:370,00084:382
        ai = new AnalysisIdentifier("P04637");
        ai.addPtm("00084", (long) 370);
        ai.addPtm("00084", (long) 382);
        aiSet.add(ai);

        //P56524;00916:246,00916:467,00916:632
        ai = new AnalysisIdentifier("P56524");
        ai.addPtm("00916", (long) 246);
        ai.addPtm("00916", (long) 467);
        ai.addPtm("00916", (long) 632);
        aiSet.add(ai);

        aiSetWithNull = new ArrayList<>();

        //P56524;00916:null
        ai = new AnalysisIdentifier("P56524");
        ai.addPtm("00916", null);
        aiSetWithNull.add(ai);

        //P04637;00084:NULL,00084:382
        ai = new AnalysisIdentifier("P04637");
        ai.addPtm("00084", null);
        ai.addPtm("00084", (long) 382);
        aiSetWithNull.add(ai);

        //P12345-2;00916:null,00916:467,00916:632
        ai = new AnalysisIdentifier("P12345-2");
        ai.addPtm("00916", null);
        ai.addPtm("00916", (long) 467);
        ai.addPtm("00916", (long) 632);
        aiSetWithNull.add(ai);

        ai_P12345_2 = ai;
    }
}