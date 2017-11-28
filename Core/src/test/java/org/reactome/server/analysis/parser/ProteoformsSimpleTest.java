package org.reactome.server.analysis.parser;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.parser.exception.ParserException;

import java.util.ArrayList;
import java.util.List;

import static org.reactome.server.analysis.parser.tools.ParserFactory.createParser;
import static org.reactome.server.analysis.parser.util.FileUtils.getString;

class ProteoformsSimpleTest {

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

    private static final String PATH_VALID = "src/test/resources/analysis/input/ProteoformsSimple/Valid/";
    private static final String PATH_INVALID = "src/test/resources/analysis/input/ProteoformsSimple/Invalid/";

    static List<AnalysisIdentifier> aiSet;
    static List<AnalysisIdentifier> aiSet2;
    static List<AnalysisIdentifier> aiSetWithNull;
    static List<AnalysisIdentifier> aiSetWithNull2;
    static List<AnalysisIdentifier> aiSetWithMultiplePTMs;
    static List<AnalysisIdentifier> aiSetWithIsoforms;

    private static AnalysisIdentifier ai_P12345_2 = new AnalysisIdentifier("P12345-2");

    @Test
    @Tag("Valid")
    void oneLineWithManySpacesTest(TestInfo testInfo) {

        String data = getString(PATH_VALID + "oneLineWithManySpaces.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " should fail");
        } catch (ParserException e) {
            Assert.assertEquals(1, e.getErrorMessages().size());
            Assert.assertTrue("There should be an inline error.", e.getErrorMessages().contains("A single line input is invalid. Found proteins and values together."));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineWithTabsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithTabs.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
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
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Expecting start with comment", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesWithExpressionValuesTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesWithExpressionValues.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSet2) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesOnlyUniprotAccessionsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesOnlyUniprotAccessions.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(18, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

    }

    @Test
    @Tag("Valid")
    void multipleLinesWithMultiplePTMsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesWithMultiplePTMs.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(11, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithMultiplePTMs) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesNullCoordinateTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesNullCoordinate.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(11, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithNull2) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Invalid")
    // Proteoforms will not follow the format specified in the regex, therefore it is taken
    // as a regular id, but then the number of columns will not match
    void multipleLinesWithInvalidPTMTypeTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "multipleLinesWithInvalidPTMType.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Line should be rejected as invalid for the proteoform format.", e.getErrorMessages().contains("Line 2 does not follow the proteoform format SIMPLE."));
            Assert.assertTrue("Line should be rejected as invalid for the proteoform format.", e.getErrorMessages().contains("Line 3 does not follow the proteoform format SIMPLE."));
            Assert.assertTrue("Line should be rejected as invalid for the proteoform format.", e.getErrorMessages().contains("Line 5 does not follow the proteoform format SIMPLE."));
            Assert.assertTrue("Line should be rejected as invalid for the proteoform format.", e.getErrorMessages().contains("Line 7 does not follow the proteoform format SIMPLE."));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesWithIsoformsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesWithIsoforms.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(13, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithIsoforms) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Invalid")
    void multipleLinesBrokenFileTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "multipleLinesBrokenFile.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformSimple.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Should have less columns than first line.", e.getErrorMessages().contains("Line 2 does not have 5 column(s). 1 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 3 does not have 5 column(s). 8 Column(s) found."));
            Assert.assertTrue("Line should be rejected as invalid for the proteoform format.", e.getErrorMessages().contains("Line 7 does not follow the proteoform format SIMPLE."));
        }
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

        /****************/

        aiSet2 = new ArrayList<>();

        ai = new AnalysisIdentifier("O14641");
        ai.addPtm("01148", null);
        aiSet2.add(ai);

        ai = new AnalysisIdentifier("O14678");
        ai.addPtm("01649", 319L);
        ai.addPtm("00014", 319L);
        aiSet2.add(ai);

        ai = new AnalysisIdentifier("O14775-1");
        aiSet2.add(ai);

        ai = new AnalysisIdentifier("P08581");
        ai.addPtm("00048", 1349L);
        ai.addPtm("00048", 1356L);
        aiSet2.add(ai);

        ai = new AnalysisIdentifier("O14841");
        ai.addPtm("00011", 323L);
        ai.addPtm("01646", 323L);
        aiSet2.add(ai);

        ai = new AnalysisIdentifier("O14841");
        ai.addPtm("00019", 1089L);
        ai.addPtm("01650", 1089L);
        aiSet2.add(ai);

        aiSetWithNull2 = new ArrayList<>();

        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00046", null);
        ai.addPtm("01148", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00696", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00046", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("01148", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00162", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00162", null);
        ai.addPtm("00039", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        ai.addPtm("00037", null);
        ai.addPtm("00038", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("01914", null);
        ai.addPtm("00130", null);
        ai.addPtm("00039", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("01914", null);
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        aiSetWithNull2.add(ai);

        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        ai.addPtm("00037", null);
        aiSetWithNull2.add(ai);

        aiSetWithMultiplePTMs = new ArrayList<>();
        ai = new AnalysisIdentifier("O14678");
        ai.addPtm("01649", 319L);
        ai.addPtm("00014", 319L);
        aiSetWithMultiplePTMs.add(ai);

        ai = new AnalysisIdentifier("P08581");
        ai.addPtm("00048", 1003L);
        ai.addPtm("00048", 1234L);
        ai.addPtm("00048", 1235L);
        ai.addPtm("00048", 1349L);
        ai.addPtm("00048", 1356L);
        ai.addPtm("00048", null);
        aiSetWithMultiplePTMs.add(ai);

        aiSetWithMultiplePTMs = new ArrayList<>();
        ai = new AnalysisIdentifier("P08581");
        ai.addPtm("00048", 1234L);
        ai.addPtm("00048", 1235L);
        ai.addPtm("00048", 1349L);
        ai.addPtm("00048", 1356L);
        aiSetWithMultiplePTMs.add(ai);

        aiSetWithIsoforms = new ArrayList<>();
        ai = new AnalysisIdentifier("P08235-2");
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P08235-4");
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 392L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 395L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-2");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 395L);
        aiSetWithIsoforms.add(ai);
    }
}