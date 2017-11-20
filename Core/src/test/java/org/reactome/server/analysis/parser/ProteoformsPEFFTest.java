package org.reactome.server.analysis.parser;

import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.springframework.context.annotation.Description;

import static org.junit.jupiter.api.Assertions.*;
import static org.reactome.server.analysis.parser.tools.ParserFactory.createParser;
import static org.reactome.server.analysis.parser.util.FileUtils.getString;

class ProteoformsPEFFTest {

    private static final String PATH_VALID = "target/test-classes/analysis/input/ProteoformsPEFF/Valid/";
    private static final String PATH_INVALID = "target/test-classes/analysis/input/ProteoformsPEFF/Invalid/";

    @Test
    @Tag("Valid")
    void exampleMinimalValidTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "PEFF_Minimal_Valid.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        assertEquals(1, p.getHeaderColumnNames().size());
        assertEquals(1, p.getAnalysisIdentifierSet().size());
        assertEquals(0, p.getWarningResponses().size());
    }

    @Test
    @Tag("Valid")
    void exampleTinyValidTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "PEFF_Tiny_Valid.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
        } catch (ParserException e) {
            fail(testInfo.getDisplayName() + " has failed");
        }

        assertEquals(1, p.getHeaderColumnNames().size());
        assertEquals(3, p.getAnalysisIdentifierSet().size());
        assertEquals(2, p.getWarningResponses().size());

        assertAll("p.getWarningResponses()",
                () -> assertTrue(p.getWarningResponses().contains("ModRes term '|Custom Mod 1' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 16 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")),
                () -> assertTrue(p.getWarningResponses().contains("ModRes term 'ModCV:22|Floxilation' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 18 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")));
    }


    @Test
    @Tag("Valid")
    void exampleSmallValid09Test(TestInfo testInfo) {
        String data = getString(PATH_VALID + "SmallTestDB-PEFF0.9.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        assertEquals(1, p.getHeaderColumnNames().size());
        assertEquals(29, p.getAnalysisIdentifierSet().size());
        assertEquals(4, p.getWarningResponses().size());

        assertAll("p.getWarningResponses()",
                () -> assertTrue(p.getWarningResponses().contains("No PEFF version provided. Assuming and allowing pre-release version 0.9 for now in file header")),
                () -> assertTrue(p.getWarningResponses().contains("ModRes term '|1' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 1 in entry 'sp:Q62622_CHAIN0'")),
                () -> assertTrue(p.getWarningResponses().contains("ModRes term '|63' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 63 in entry 'sp:Q62622_CHAIN0'")),
                () -> assertTrue(p.getWarningResponses().contains("Attribute \\Length is not present in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")));
    }

    @Test
    @Tag("Valid")
    void exampleSmallValid10Test(TestInfo testInfo) {
        String data = getString(PATH_VALID + "SmallTestDB-PEFF1.0.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(testInfo.getDisplayName() + " has failed");
        }

        assertEquals(1, p.getHeaderColumnNames().size());
        assertEquals(29, p.getAnalysisIdentifierSet().size());
        assertEquals(2, p.getWarningResponses().size());

        assertAll("p.getWarningResponses()",
                () -> assertTrue(p.getWarningResponses().contains("ModRes term '1|ACET_nterm' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 1 in entry 'sp:Q62622_CHAIN0'")),
                () -> assertTrue(p.getWarningResponses().contains("ModRes term '63|PHOS' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 63 in entry 'sp:Q62622_CHAIN0'")));
    }

    @Test
    @Tag("Invalid")
    void exampleMinimalInvalidTest(TestInfo testInfo) {
        String data = getString(PATH_VALID + "PEFF_Minimal_INValid1.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed");
        } catch (ParserException e) {
            // Some things should go wrong
            assertEquals(11, p.getWarningResponses().size());
            assertAll("p.getWarningResponses()",
                    () -> assertTrue(p.getWarningResponses().contains("Only PEFF 1.0 is currently supported, but this file claims to be PEFF version '0.0'. We will press on and hope for the best, but it probably won't end well in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("Mandatory header attribute 'Prefix' is missing in header")),
                    () -> assertTrue(p.getWarningResponses().contains("Mandatory header attribute 'DbSource' is missing in header")),
                    () -> assertTrue(p.getWarningResponses().contains("Mandatory header attribute 'DbVersion' is missing in header")),
                    () -> assertTrue(p.getWarningResponses().contains("Mandatory header attribute 'SequenceType' is missing in header")),
                    () -> assertTrue(p.getWarningResponses().contains("Mandatory header attribute 'NumberOfEntries' is missing in header")),
                    () -> assertTrue(p.getWarningResponses().contains("The database key in the accession is not defined from 'sp:Q9Y2X3'")),
                    () -> assertTrue(p.getWarningResponses().contains("Term 'Color' not found in CV (and the database 'sp' is not defined so no custom keys) in entry 'sp:Q9Y2X3'")),
                    () -> assertTrue(p.getWarningResponses().contains("The database key in the accession is not defined from 'sp:Q9Y2X4'")),
                    () -> assertTrue(p.getWarningResponses().contains("The \\Length= attribute does not match calculated sequence length \\Length=5 should be 11 in entry 'sp:Q9Y2X4'")),
                    () -> assertTrue(p.getWarningResponses().contains("Attribute \\Length is not present in entry 'sp:Q9Y2X3'"))
            );
        }
    }

    @Test
    @Tag("Invalid")
    void exampleTinyInvalidTest(TestInfo testInfo) {
        String data = getString(PATH_INVALID + "PEFF_Tiny_INValid1.peff");
        Parser p = createParser(data);
        try {
            assertEquals(p.getClass(), ParserProteoformPEFF.class);
            p.parseData(data);
            Assert.fail(testInfo.getDisplayName() + " has failed");
        } catch (ParserException e) {
            // Some things should go wrong
            assertEquals(11, p.getWarningResponses().size());
            assertAll("p.getWarningResponses()",
                    () -> assertTrue(p.getWarningResponses().contains("Only PEFF 1.0 is currently supported, but this file claims to be PEFF version '1.99'. We will press on and hope for the best, but it probably won't end well in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("Line '# Forecast=Rainy and sunbreaks' is not permitted in overall header. Only GeneralComment is permitted in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("Term 'BogusTag' not found in CV or in custom keys in entry 'sp:Q9Y2X3'")),
                    () -> assertTrue(p.getWarningResponses().contains("The \\Length= attribute does not match calculated sequence length \\Length=520 should be 529 in entry 'sp:Q9Y2X3'")),
                    () -> assertTrue(p.getWarningResponses().contains("ModRes 'MOD:00046' has a name 'O-phospho-L-threonine' instead of O-phospho-L-serine at position 7 in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("Residue 'L' is not a permitted site for MOD:00046 = O-phospho-L-serine at position 7 in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("Residue 'P' is not a permitted site for MOD:00047 = O-phospho-L-threonine at position 61 in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("VariantSimple substitution contains too many characters 'RS' in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("Variant contains illegal character '-' in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("Variant startPosition must be <= endPosition in '55|52||litRep' in entry 'nxp:NX_P11171-2'")),
                    () -> assertTrue(p.getWarningResponses().contains("The database key in the accession is not defined from 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")),
                    () -> assertTrue(p.getWarningResponses().contains("ModRes 'UNIMOD:4' has a name 'Carboximethyl' instead of Carbamidomethyl at position 16 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")),
                    () -> assertTrue(p.getWarningResponses().contains("Residue 'A' is not a permitted site for UNIMOD:4 = Carbamidomethyl at position 16 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")),
                    () -> assertTrue(p.getWarningResponses().contains("Stated NumberOfEntries for database 'sp' is 2, but 1 were counted in the file in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("Stated NumberOfEntries for database 'nxp' is 3, but 1 were counted in the file in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("Line '# GeneralComment=' is missing a value. This is not good, but is legal in file header")),
                    () -> assertTrue(p.getWarningResponses().contains("ModRes term '|Custom Mod 1' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 16 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'")),
                    () -> assertTrue(p.getWarningResponses().contains("ModRes term 'ModCV:22|Floxilation' cannot be resolved in PSI-MOD or UniMod and may not be interpretable by reading software at position 18 in entry 'nr:gi|136429|sp|P00761.1|TRYP_PIG'"))
            );
        }
    }

    @Test
    @Tag("Valid")
    void isValid_lowerCase_true() {

    }

    @Test
    @Tag("Valid")
    @Description("One database header block")
    void singleDatabaseHeaderBlockTest() {
        Assert.fail("Not implemented");
    }

    @Test
    @Tag("Valid")
    @Description("Two database header blocks")
    void twoDatabaseHeaderBlocksTest() {
        Assert.fail("Not implemented");
    }


    @Test
    @Tag("Valid")
    @Description("Multiple database header blocks")
    void multipleDatabaseHeaderBlocksTest() {

    }

    @Test
    @Tag("Valid")
    @Description("Send warning that DbName was not specified.")
    void singleDatabaseHeaderBlockMissingDbNameTest() {

    }

    @Test
    @Tag("Valid")
    @Description("Send warning when at least one of the key elements are not specified.")
    void multipleDatabaseHeaderBlocksMissingKeysTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Test file with a single sequence")
    void singleSequenceTest() {
    }

    @Test
    @Tag("Feature")
    @Description("Test possibility of multiple sequences in one file")
    void multipleSequenceTest() {
    }

    @Test
    @Tag("Feature")
    @Description("The file must contain a Header and an Individual sequence entries section")
    void generalFileStructureTest() {

    }

    @Test
    @Tag("Feature")
    @Description("The file header section MUST start with a file description block that MUST be followed by at least one sequence database description block.")
    void headerStructureTest() {

    }

    @Test
    @Tag("Feature")
    @Description("All lines in the file header section start with the character #, followed by a space (ASCII 32) character. ")
    void headerLinesStartAndEndCharacterTest() {

    }

    @Test
    @Tag("Feature")
    @Description("The first line of the header section is also the first line of the file: # PEFF N.N, e.g. 1.0")
    void firstLineTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Parsers SHOULD check PEFF version in the first line and react if supported or not.")
    void supportedPEFFVersionTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Optional general comments can appear after the first header line. Nowhere else. They must be nonempty. Structure of the comments is '# GeneralComment=value'")
    void generalCommentsStructureTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Sequence database description lines appear after first header line or after the general comments.")
    void sequenceDatabaseLinesLocationTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Sequence database description lines structure is: '# key=value'. 'PEFFKey' attached to a CV term in the PSI-MS CV under the “PEFF keyword” branch.")
    void sequenceDatabaseLinesContentTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Each sequence database block MUST start with a sequence database line description and follow the following format: # DbName=value, (where value is the database name)")
    void sequenceDatabaseFirstLineTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Each sequence database block must contain meta-information about the database itself: Prefix; DbVersion, DbSource, NumberOfEntries, SequenceType.")
    void sequenceDatabaseMandatoryInformationTest() {
    }

    @Test
    @Tag("Feature")
    @Description("Each sequence database block may contain optional meta-information defined after the mandatory database keys using the SpecificKey key")
    void sequenceDatabaseOptionalTest() {
    }

    @Test
    @Tag("Feature")
    @Description("Each sequence database block MUST end with the following separation line: '# //'")
    void sequenceDatabaseLastLineTest() {
    }


    @Test
    @Tag("Feature")
    @Description("Test compatibility with MIAPE guidelines")
    void compliantWithMIAPETest() {

    }

    @Test
    @Tag("Feature")
    @Description("Test possibility to encode exact proteoforms")
    void encodeExactProteoformsTest() {

    }

    @Test
    @Tag("Feature")
    @Description("All lines in the file MUST end with LF (ASCII 10). A CR (ASCII 13) MAY precede the LF and SHOULD be ignored by parsers.")
    void lineEndingTest() {

    }

    @Test
    @Tag("Feature")
    @Description("The characters allowed are the set of ASCII characters")
    void charactersAllowedTest() {

    }

    @Test
    @Tag("Feature")
    @Description("Possibility for multiple database sources; indicated in the Header section and with individual sequence entries.")
    void multipleDatabasesTest() {

    }


}