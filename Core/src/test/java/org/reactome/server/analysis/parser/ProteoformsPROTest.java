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

class ProteoformsPROTest {

    /**
     * Format rules:
     * - One proteoform per line
     * - Consists of a sequence block and optional modification blocks
     * - The only mandatory part is the accession number.
     * - There are one or more optional modification blocks
     * - Sequence blocks consist of a UniProtKB accession with an optional isoform indicated by a dash, followed
     * by a comma. And an optional subsequence range separated with a comma.
     * - Each modification block is presented in order from the N-terminal-most amino acid specified.
     * - Within a modification block there are one or more amino acids listed by type and position.
     * - Multiple amino-acids within a block are separated by forward slashes.
     * - Positions of modification are relative to the full length of the isoform.
     * - Missing a subsequence section indicates that the class encompasses either multiple species or isoforms.
     * - Missing modification blocks with a subsequence indicates that the class is defined by subsequence only.
     * - NOTE: In our casse we will only use the accession numbers and set of post translational modifications
     * to identify a particular proteoform, to make our analysis consistent with the rest of the formats.
     * - We allow the position to be null, so that it is also consistent with the rest.
     * <p>
     * The draft of the format is at: doi: 10.1093/nar/gkw1075
     */

    private static final String PATH_VALID = "target/test-classes/analysis/input/ProteoformsPRO/Valid/";
    private static final String PATH_INVALID = "target/test-classes/analysis/input/ProteoformsPRO/Invalid/";

    static List<AnalysisIdentifier> aiSet;
    static List<AnalysisIdentifier> aiSetWithNull;
    static List<AnalysisIdentifier> aiSetWithMultiplePTMs;
    static List<AnalysisIdentifier> aiSetWithIsoforms;
    static List<AnalysisIdentifier> aiSetMultipleLinesWithIsoforms;
    static List<AnalysisIdentifier> aiSetWithExpressionValues;
    static List<AnalysisIdentifier> aiSetOneLineWithMultiplePTMs;
    static List<AnalysisIdentifier> aiSetOneLineNullCoordinate;
    static List<AnalysisIdentifier> aiSetOneLineHasIsoform;


    private static AnalysisIdentifier ai_P12345_2 = new AnalysisIdentifier("P12345-2");

    @Test
    @Tag("Valid")
    void oneLineWithManySpacesTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithManySpaces.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
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
    @Tag("Invalid")
    /*
    This input is invalid for the proteoform format, but it is accepted as a type of input
    as a one line file separated by comma and semi colon.
     */
    void oneLineWithCommasTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "oneLineWithComas.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed");
        } catch (ParserException e) {
            Assert.assertTrue("One line proteoform files are not separated by commas.", e.getErrorMessages().contains("A single line input is invalid. Found proteins and values together."));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineWithTabsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithTabs.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
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
        // Whether it has the semi-colon or not, the single uniprot accession should be accepted.
    void oneLineOnlyUniprotAccessionsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineWithOneUniprot.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
        Assert.assertTrue("Looking for Q9BUH8", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q9BUH8")));
    }

    @Test
    @Tag("Valid")
    void oneLineProteinWithOnePTMTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineProteinWithOnePTM.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        AnalysisIdentifier ai = new AnalysisIdentifier("Q9H8P0");
        ai.addPtm("01646", 10L);
        Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
    }

    @Test
    @Tag("Valid")
    void oneLineProteinWithMultiplePTMsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineProteinWithMultiplePTMs.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(3, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetOneLineWithMultiplePTMs) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineNullCoordinateTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineNullCoordinate.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetOneLineNullCoordinate) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void oneLineHasIsoformTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineHasIsoform.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(3, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        //Here
    }

    @Test
    @Tag("Valid")
    void oneLineHasIsoformAndPTMsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "oneLineHasIsoformAndPTMs.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(15, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithIsoforms) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
        // Is valid as it becomes a multiline file: 1 header and 1 for content
    void oneLineWithHeadersTest(TestInfo testInfo) {

        String data = getString(PATH_VALID + "oneLineWithHeaders.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        AnalysisIdentifier ai = new AnalysisIdentifier("Q9HAW4");
        ai.addPtm("00046", 945L);
        ai.addPtm("00047", 916L);
        Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));

    }

    @Test
    @Tag("Invalid")
    void oneLineStartsWithHashTest(TestInfo testInfo) {

        String data = getString(PATH_INVALID + "oneLineStartsWithHash.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("One line files do not start with hash.", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    @Tag("Invalid")
    void oneLineStartsWithComment(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "oneLineStartsWithComment.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("No comment should be in one line files.", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesWithExpressionValuesTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesWithExpressionValues.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithExpressionValues) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesOnlyUniprotAccessionsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesOnlyUniprotAccessions.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
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
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(8, p.getAnalysisIdentifierSet().size());    // The row 4 and 7 are repeated
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
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(11, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetWithNull) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Invalid")
    void multipleLinesNullCoordinateSpacedHeaderTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "multipleLinesNullCoordinateSpacedHeader.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " was expected to fail beause header has multiple columns and the rest of rows not.");
        } catch (ParserException e) {
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
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Line does not match the proteoform PRO format.", e.getErrorMessages().contains("Line 2 does not follow the proteoform format PRO."));
            Assert.assertTrue("Line does not match the proteoform PRO format.", e.getErrorMessages().contains("Line 3 does not follow the proteoform format PRO."));
            Assert.assertTrue("Line does not match the proteoform PRO format.", e.getErrorMessages().contains("Line 5 does not follow the proteoform format PRO."));
            Assert.assertTrue("Line does not match the proteoform PRO format.", e.getErrorMessages().contains("Line 7 does not follow the proteoform format PRO."));
            Assert.assertTrue("Line does not match the proteoform PRO format.", e.getErrorMessages().contains("Line 9 does not follow the proteoform format PRO."));
        }
    }

    @Test
    @Tag("Valid")
    void multipleLinesWithIsoformsTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "multipleLinesWithIsoforms.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(13, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

        for (AnalysisIdentifier ai : aiSetMultipleLinesWithIsoforms) {
            Assert.assertTrue("Looking for " + ai.toString(), p.getAnalysisIdentifierSet().contains(ai));
        }
    }

    @Test
    @Tag("Invalid")
    void multipleLinesBrokenFileTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "multipleLinesBrokenFile.txt");
        Parser p = createParser(data);
        try {
            Assert.assertEquals(p.getClass(), ParserProteoformPRO.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Should have less columns than first line.", e.getErrorMessages().contains("Line 2 does not have 5 column(s). 1 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 3 does not have 5 column(s). 8 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 4 does not have 5 column(s). 2 Column(s) found."));
            Assert.assertTrue("Should have more columns than the first line.", e.getErrorMessages().contains("Line 5 does not have 5 column(s). 3 Column(s) found."));
            Assert.assertTrue("Line 7 should not be valid.", e.getErrorMessages().contains("Line 7 does not follow the proteoform format PRO."));
        }
    }

    @BeforeAll
    public static void setUp() {
        aiSet = new ArrayList<>();

        AnalysisIdentifier ai = new AnalysisIdentifier("P56589");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P56696");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P56703");
        ai.addPtm("01381", 212L);
        ai.addPtm("00160", null);
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P56703");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P58549");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P58753");
        ai.addPtm("00048", 86L);
        ai.addPtm("00048", 106L);
        ai.addPtm("00048", 159L);
        ai.addPtm("00048", 187L);
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P58753");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P58876");
        ai.addPtm("00064", null);
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P58876");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P60468");
        aiSet.add(ai);

        ai = new AnalysisIdentifier("P60484");
        ai.addPtm("00017", 130L);
        ai.addPtm("01632", 130L);
        aiSet.add(ai);


        /****************/


        aiSetWithNull = new ArrayList<>();

//        UniProtKB:P08151,Ser-null,MOD:00046|Lys-null,MOD:01148
        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00046", null);
        ai.addPtm("01148", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08151,XXX-null,MOD:00696
        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00696", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08151,Ser-null,MOD:00046
        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("00046", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08151,Lys-null,MOD:01148
        ai = new AnalysisIdentifier("P08151");
        ai.addPtm("01148", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-84,MOD:00130|Lys-null,MOD:00162
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00162", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-84,MOD:00130|Lys-null,MOD:00162|Pro-null,MOD:00039
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00162", null);
        ai.addPtm("00039", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-84,MOD:00130|Pro-null,MOD:00039|Lys-null,MOD:00037|Pro-null,MOD:00038
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        ai.addPtm("00038", null);
        ai.addPtm("00037", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-null,MOD:01914|Lys-null,MOD:00130|Pro-null,MOD:00039
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("01914", null);
        ai.addPtm("00130", null);
        ai.addPtm("00039", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-null,MOD:01914|Lys-84,MOD:00130|Pro-null,MOD:00039
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("01914", null);
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-84,MOD:00130|Pro-null,MOD:00039
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        aiSetWithNull.add(ai);

//        UniProtKB:P08123,Lys-84,MOD:00130|Pro-null,MOD:00039|Lys-null,MOD:00037
        ai = new AnalysisIdentifier("P08123");
        ai.addPtm("00130", 84L);
        ai.addPtm("00039", null);
        aiSetWithNull.add(ai);

        /****************/
        ai_P12345_2 = ai;

        /****************/

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
        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("01148", null);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("01148", null);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 280L);
        ai.addPtm("00046", 284L);
        ai.addPtm("00046", 288L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 183L);
        ai.addPtm("00047", 185L);
        ai.addPtm("00047", 187L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 280L);
        ai.addPtm("00046", 284L);
        ai.addPtm("00046", 288L);
        ai.addPtm("01148", null);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 418L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 451L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-2");
        ai.addPtm("00046", 280L);
        ai.addPtm("00046", 298L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 312L);
        ai.addPtm("00046", 294L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 298L);
        ai.addPtm("00046", 302L);
        ai.addPtm("00046", 294L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 196L);
        ai.addPtm("00047", 198L);
        ai.addPtm("00047", 200L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("01148", null);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 294L);
        ai.addPtm("00046", 298L);
        ai.addPtm("00046", 302L);
        ai.addPtm("01148", null);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 465L);
        aiSetWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("Q13950-1");
        ai.addPtm("00046", 432L);
        aiSetWithIsoforms.add(ai);

        /**************************/

        aiSetWithExpressionValues = new ArrayList<>();

        ai = new AnalysisIdentifier("O14641");
        ai.addPtm("00696", null);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14678");
        ai.addPtm("01649", 319L);
        ai.addPtm("00014", 319L);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14775-1");
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("P08581");
        ai.addPtm("00048", 1349L);
        ai.addPtm("00048", 1356L);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14965");
        ai.addPtm("00047", 288L);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14904");
        ai.addPtm("01381", 221L);
        ai.addPtm("00160", null);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14841");
        ai.addPtm("00011", 323L);
        ai.addPtm("01646", 323L);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14841");
        ai.addPtm("00019", 1089L);
        ai.addPtm("01650", 1089L);
        aiSetWithExpressionValues.add(ai);

        ai = new AnalysisIdentifier("O14791");
        aiSetWithExpressionValues.add(ai);

        /******************************/

        aiSetOneLineWithMultiplePTMs = new ArrayList<>();

        ai = new AnalysisIdentifier("P10398");
        ai.addPtm("00046", 299L);
        ai.addPtm("00046", 576L);
        ai.addPtm("00047", 452L);
        ai.addPtm("00047", 455L);
        ai.addPtm("00048", 302L);
        aiSetOneLineWithMultiplePTMs.add(ai);

        aiSetOneLineWithMultiplePTMs = new ArrayList<>();
        ai = new AnalysisIdentifier("P10253");
        ai.addPtm("00026", 318L);
        ai.addPtm("01643", 318L);
        aiSetOneLineWithMultiplePTMs.add(ai);

        aiSetOneLineWithMultiplePTMs = new ArrayList<>();
        ai = new AnalysisIdentifier("P10070");
        ai.addPtm("00046", 808L);
        ai.addPtm("00046", 811L);
        ai.addPtm("00046", 820L);
        ai.addPtm("00046", 824L);
        ai.addPtm("00046", 827L);
        ai.addPtm("00046", 832L);
        ai.addPtm("00046", 836L);
        ai.addPtm("00046", 839L);
        ai.addPtm("00046", 863L);
        ai.addPtm("00046", 867L);
        ai.addPtm("00046", 870L);
        aiSetOneLineWithMultiplePTMs.add(ai);

        aiSetOneLineNullCoordinate = new ArrayList<>();

        ai = new AnalysisIdentifier("P10070");
        ai.addPtm("00696", null);
        aiSetOneLineNullCoordinate.add(ai);

        ai = new AnalysisIdentifier("P10070");
        ai.addPtm("00696", null);
        ai.addPtm("01148", null);
        aiSetOneLineNullCoordinate.add(ai);

        ai = new AnalysisIdentifier("P10071");
        ai.addPtm("00696", null);
        aiSetOneLineNullCoordinate.add(ai);

        ai = new AnalysisIdentifier("P10275");
        ai.addPtm("01148", null);
        aiSetOneLineNullCoordinate.add(ai);

        ai = new AnalysisIdentifier("P0C0S8");
        ai.addPtm("00064", null);
        aiSetOneLineNullCoordinate.add(ai);

        ai = new AnalysisIdentifier("P09544");
        ai.addPtm("01381", 212L);
        ai.addPtm("00160", null);
        aiSetOneLineNullCoordinate.add(ai);

        aiSetOneLineHasIsoform = new ArrayList<>();

        ai = new AnalysisIdentifier("P98177-1");
        ai.addPtm("00046", 197L);
        ai.addPtm("00046", 262L);
        ai.addPtm("00047", 32L);
        aiSetOneLineHasIsoform.add(ai);

        ai = new AnalysisIdentifier("P98177-2");
        aiSetOneLineHasIsoform.add(ai);

        ai = new AnalysisIdentifier(":P98177-2");
        ai.addPtm("00046", 142L);
        ai.addPtm("00046", 207L);
        ai.addPtm("00047", 32L);
        aiSetOneLineHasIsoform.add(ai);

        aiSetMultipleLinesWithIsoforms = new ArrayList<>();
        ai = new AnalysisIdentifier("P08235-2");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P08235-1");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P08235-4");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P08235-3");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P05549-1");
        ai.addPtm("01149", 10L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P04629-1");
        ai.addPtm("00048", 490L);
        ai.addPtm("00048", 670L);
        ai.addPtm("00048", 674L);
        ai.addPtm("00048", 675L);
        ai.addPtm("00048", 785L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 392L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 395L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-1");
        ai.addPtm("00046", 395L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-2");
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-2");
        ai.addPtm("00046", 22L);
        ai.addPtm("00046", 395L);
        aiSetMultipleLinesWithIsoforms.add(ai);

        ai = new AnalysisIdentifier("P02545-2");
        ai.addPtm("00046", 395L);
        aiSetMultipleLinesWithIsoforms.add(ai);
    }
}