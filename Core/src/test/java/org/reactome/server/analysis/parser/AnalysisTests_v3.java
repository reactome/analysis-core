package org.reactome.server.analysis.parser;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.InputFormat_v3;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.springframework.context.annotation.Description;

import java.io.*;
import java.net.URL;

import static org.reactome.server.analysis.parser.util.ConstantHolder.PATH;
import static org.reactome.server.analysis.parser.util.FileUtils.getString;

/**
 * Tester class for the Analysis parser
 *
 * @author Guilherme Viteri <gviteri@ebi.ac.uk>
 */

public class AnalysisTests_v3 {

    private static final String PATH = "target/test-classes/analysis/input/";

    /**
     * MULTIPLE LINES INPUTS
     **/
    private static final String CARRIAGE_RETURN = PATH.concat("carriage_return.txt");
    private static final String CARRIAGE_RETURN_ERROR = PATH.concat("carriage_return_error.txt");
    private static final String CARRIAGE_RETURN_WARNING = PATH.concat("carriage_return_warning.txt");
    private static final String EMPTY_FILE = PATH.concat("empty_file.txt");
    private static final String EMPTY_FILE_SPACES = PATH.concat("empty_file_with_spaces.txt");
    private static final String EMPTY_LINES = PATH.concat("empty_lines.txt");
    private static final String EMPTY_LINES_WITH_SPACES = PATH.concat("empty_lines_with_spaces.txt");
    private static final String INLINE_PROBLEMS = PATH.concat("inline_problems.txt");
    private static final String MISSING_HEADER = PATH.concat("missing_header.txt");
    private static final String POTENTIAL_HEADER = PATH.concat("potential_header.txt");
    private static final String SPACES_ON_HEADER = PATH.concat("spaces_on_header.txt");
    private static final String CORRECT_FILE = PATH.concat("correct_file.txt");
    private static final String COLUMN_MISMATCH_HEADER = PATH.concat("column_mismatch_header.txt");
    private static final String COLUMN_MISMATCH_CONTENT = PATH.concat("column_mismatch_content.txt");
    private static final String MULTIPLE_WARNINGS = PATH.concat("multiple_warnings.txt");
    private static final String BROKEN_FILE = PATH.concat("broken_file.txt");
    private static final String PRIDE_SAMPLE = PATH.concat("pride_sample.txt");
    private static final String ONLY_IDENTIFIERS = PATH.concat("only_identifiers.txt");
    private static final String SAMPLE_WITH_SPACES_WITHOUT_HEADER = PATH.concat("sample_with_spaces_without_header.txt");
    private static final String SAMPLE_WITH_TAB_WITHOUT_HEADER = PATH.concat("sample_with_tab_without_header.txt");
    private static final String SAMPLE_WITH_COLON_WITHOUT_HEADER = PATH.concat("sample_with_colon_without_header.txt");

    /**
     * SINGLE LINE INPUTS
     **/
    private static final String ONELINE_START_WITH_NUMBER = PATH.concat("oneline_start_with_number.txt");
    private static final String ONELINE_START_WITH_HASH = PATH.concat("oneline_start_with_hash.txt");
    private static final String ONELINE_IDENTIFIERS = PATH.concat("oneline_identifiers.txt");
    private static final String ONELINE_ID_EXPRESSIONS = PATH.concat("oneline_id_expressions.txt");
    private static final String ONELINE_ID_EXPRESSIONS_MIXED = PATH.concat("oneline_id_expressions_mixed.txt");
    private static final String ONELINE_CSV = PATH.concat("oneline_csv.txt");
    private static final String ONELINE_OTHERSSEPARATOR = PATH.concat("oneline_others_separators.txt");
    private static final String ONELINE_START_WITH_SPACES = PATH.concat("oneline_start_with_spaces.txt");
    private static final String ONELINE_BUT_NOT_IN_FIRST_LINE = PATH.concat("oneline_but_not_in_first_line.txt");
    private static final String ONELINE_RANDOM_CHARACTERS = PATH.concat("oneline_random_characters.txt");
    private static final String ONELINE_ONE_IDENTIFIER = PATH.concat("oneline_one_identifier.txt");

    /**
     * ONE LINE FILE WITH EXPRESSION VALUES. HEADER MUST BE PRESENT - SO, IT IS A 2 LINE FILES
     */
    private static final String ONELINE_WITH_HEADER = PATH.concat("oneline_with_header.txt");

    /**
     * Analysis Portal Samples
     */
    private static final String SAMPLE = PATH.concat("samples/");
    private static final String GENE_NAME_LIST = SAMPLE.concat("gene_name_list.txt");
    private static final String GENE_NCBI = SAMPLE.concat("gene_ncbi.txt");
    private static final String METABOLOMICS_DATA = SAMPLE.concat("metabolomics_data.txt");
    private static final String MICROARRAY_DATA = SAMPLE.concat("microarray_data.txt");
    private static final String SMALL_MOLECULES_CHEBI = SAMPLE.concat("small_molecules_chebi.txt");
    private static final String SMALL_MOLECULES_KEGG = SAMPLE.concat("small_molecules_kegg.txt");
    private static final String UNIPROT_ACCESSION_LIST = SAMPLE.concat("uniprot_accession_list.txt");

    @Test
    @Tag("Valid")
    public void testEmptyLines() {
        String data = getString(EMPTY_LINES);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(EMPTY_FILE + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(14, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(8, p.getWarningResponses().size());
    }

    @Test
    @Tag("Valid")
    public void testEmptyLinesWithSpaces() {
        String data = getString(EMPTY_LINES_WITH_SPACES);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(EMPTY_LINES_WITH_SPACES + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(14, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(7, p.getWarningResponses().size());
    }

    @Test
    @Tag("Invalid")
    public void testInlineProblems() {
        String data = getString(INLINE_PROBLEMS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(INLINE_PROBLEMS + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(10, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(9, p.getWarningResponses().size());
    }

    @Test
    public void testEmptyFile() {
        String data = getString(EMPTY_FILE);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);

            Assert.fail(EMPTY_FILE + " should fail");
        } catch (ParserException e) {
            Assert.assertTrue(e.getErrorMessages().contains("There is no file to be analysed."));
        }
    }


    @Test
    public void testEmptyFileWithSpaces() {
        String data = getString(EMPTY_FILE_SPACES);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(EMPTY_FILE_SPACES + " should fail.");
        } catch (ParserException e) {
            Assert.assertTrue(e.getErrorMessages().contains("There is no file to be analysed."));
        }
    }

    @Test
    public void testMissingHeader() {
        String data = getString(MISSING_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(MISSING_HEADER + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Missing header was expected", p.getWarningResponses().contains("Missing header. Using a default one."));
        Assert.assertEquals(1, p.getWarningResponses().size());

    }

    @Test
    public void testPotentialHeader() {
        String data = getString(POTENTIAL_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(POTENTIAL_HEADER + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("First line does not match the expected result.", p.getWarningResponses().contains("The first line seems to be a header. Make sure it is being initialised by # or //."));
        Assert.assertEquals(1, p.getWarningResponses().size());

    }

    @Test
    public void testSpacesOnHeader() {
        String data = getString(SPACES_ON_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SPACES_ON_HEADER + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertTrue("Header does not match", p.getHeaderColumnNames().contains("10h After"));
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testCorrectFile() {
        String data = getString(CORRECT_FILE);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(CORRECT_FILE + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(9, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testColumnMismatchHeader() {
        String data = getString(COLUMN_MISMATCH_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);

            Assert.fail(COLUMN_MISMATCH_HEADER + " should fail");
        } catch (ParserException e) {
            Assert.assertEquals(9, e.getErrorMessages().size());
        }

    }

    @Test
    public void testColumnMismatchContent() {
        String data = getString(COLUMN_MISMATCH_CONTENT);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(COLUMN_MISMATCH_HEADER + " should fail");

        } catch (ParserException e) {
            Assert.assertEquals(6, e.getErrorMessages().size());
        }
    }

    @Test
    public void testCarriageReturn() {
        String data = getString(CARRIAGE_RETURN);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(CARRIAGE_RETURN + " has failed.");
        }

        Assert.assertEquals(5, p.getHeaderColumnNames().size());
        Assert.assertEquals(77, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testMultipleWarnings() {
        String data = getString(MULTIPLE_WARNINGS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(MULTIPLE_WARNINGS + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(36, p.getWarningResponses().size());

        int emptyLines = 0;
        for (String warn : p.getWarningResponses()) {
            if (warn.contains("is empty and has been ignored")) {
                emptyLines++;
            }
        }

        Assert.assertEquals(30, emptyLines);

    }

    @Test
    public void testCarriageReturnErrors() {
        String data = getString(CARRIAGE_RETURN_ERROR);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
            Assert.fail(CARRIAGE_RETURN_ERROR + " should fail.");
        } catch (ParserException e) {
            Assert.assertEquals(2, e.getErrorMessages().size());
        }

    }

    @Test
    public void testCarriageReturnWarnings() {
        String data = getString(CARRIAGE_RETURN_WARNING);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(CARRIAGE_RETURN_WARNING + " has failed.");
        }

        Assert.assertEquals(5, p.getHeaderColumnNames().size());
        Assert.assertEquals(77, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(3, p.getWarningResponses().size());

    }

    @Test
    public void testBrokenFile() {
        String data = getString(BROKEN_FILE);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);

            Assert.fail(BROKEN_FILE + " should fail.");
        } catch (ParserException e) {
            Assert.assertEquals(5, e.getErrorMessages().size());
        }

    }

    @Test
    public void testMaxFileSize() {
        File file = writeHugeFile();

        String data = getString(PATH + "max_file_size.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail("10MB file has failed.");
        } finally {
            file.deleteOnExit();
        }

        Assert.assertEquals(4, p.getHeaderColumnNames().size());
        Assert.assertEquals(310000, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOnlyIdentifiers() {
        String data = getString(ONLY_IDENTIFIERS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONLY_IDENTIFIERS + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Gene names from liver", p.getHeaderColumnNames().contains("Gene names from liver"));
        Assert.assertEquals(5, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for A2M Identifier", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("A2M")));
        Assert.assertEquals(0, p.getWarningResponses().size());
    }

    /**
     * ONE LINE TESTS
     **/

    @Test
    public void testOneLineStartingWithNumber() {
        String data = getString(ONELINE_START_WITH_NUMBER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_START_WITH_NUMBER + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(7, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
    }

    @Test
    public void testOneLineStartingWithHash() {
        String data = getString(ONELINE_START_WITH_HASH);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);

            Assert.fail(ONELINE_START_WITH_HASH + " has failed.");
        } catch (ParserException e) {
            Assert.assertTrue("Expecting start with comment", e.getErrorMessages().contains("A single line input cannot start with hash or comment."));
        }
    }

    @Test
    public void testOneLineIdentifiers() { //success
        String data = getString(ONELINE_IDENTIFIERS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_IDENTIFIERS + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    /**
     * For ONE LINE file --> NO EXPRESSION VALUES
     * Every token in this file will be consider as an Identifier
     */
    public void testOneLineIdAndExpressions() { //success
        String data = getString(ONELINE_ID_EXPRESSIONS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_ID_EXPRESSIONS + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineIdAndExpressionsMixed() {
        String data = getString(ONELINE_ID_EXPRESSIONS_MIXED);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);

        } catch (ParserException e) {
            Assert.fail(ONELINE_ID_EXPRESSIONS_MIXED + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(7, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineCsv() {
        String data = getString(ONELINE_CSV);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_CSV + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineOthersSeparator() {
        String data = getString(ONELINE_OTHERSSEPARATOR);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_OTHERSSEPARATOR + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineStartWithSpaces() {
        String data = getString(ONELINE_START_WITH_SPACES);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_START_WITH_SPACES + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(6, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineButNotInFirstLine() {
        String data = getString(ONELINE_BUT_NOT_IN_FIRST_LINE);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_BUT_NOT_IN_FIRST_LINE + " has failed.");
        }

        Assert.assertTrue(p.getHeaderColumnNames().size() == 1);
        Assert.assertTrue(p.getAnalysisIdentifierSet().size() == 6);
        Assert.assertTrue(p.getWarningResponses().size() == 0);

    }

    @Test
    public void testOneLineRandomCharacters() {
        String data = getString(ONELINE_RANDOM_CHARACTERS);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_RANDOM_CHARACTERS + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(4, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOnelineHuge() {
        File file = writeOneLineHugeFile();

        String data = getString(PATH + "one_line_max_file_size.txt");
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail("10MB one line file has failed.");
        } finally {
            file.deleteOnExit();
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(750000, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());
    }

    @Test
    public void testOneLineWithHeader() {
        /**
         * Remember this is not an ONE LINE file.
         * A ONE LINE file cannot have EXPRESSION VALUES
         */
        String data = getString(ONELINE_WITH_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_WITH_HEADER + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testOneLineOneIdentifier() {
        String data = getString(ONELINE_ONE_IDENTIFIER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(ONELINE_ONE_IDENTIFIER + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(1, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for PTEN", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("PTEN")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    /**
     * SAMPLES - These is testing the same samples we provide in the Portal
     */
    @Test
    public void testMetabolomicsData() {
        String data = getString(METABOLOMICS_DATA);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(METABOLOMICS_DATA + " has failed.");
        }

        Assert.assertEquals(5, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Molecules", p.getHeaderColumnNames().contains("Molecules"));
        Assert.assertEquals(899, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for C00137", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C00137")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testGeneNameList() {
        String data = getString(GENE_NAME_LIST);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(GENE_NAME_LIST + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Gene names from liver", p.getHeaderColumnNames().contains("Gene names from liver"));
        Assert.assertEquals(230, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for ALDH4A1", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("ALDH4A1")));
        Assert.assertEquals(0, p.getWarningResponses().size());
    }

    @Test
    public void testGeneNcbi() {
        String data = getString(GENE_NCBI);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(GENE_NCBI + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header 12 Tumours NCBI gene", p.getHeaderColumnNames().contains("12 Tumours NCBI gene"));
        Assert.assertEquals(128, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for 89795", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("89795")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testMicroarrayData() {
        String data = getString(MICROARRAY_DATA);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(MICROARRAY_DATA + " has failed.");
        }

        Assert.assertEquals(6, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Probeset", p.getHeaderColumnNames().contains("Probeset"));
        Assert.assertEquals(1203, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for 200000_s_at", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("200000_s_at")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testSmallMoleculesChebi() {
        String data = getString(SMALL_MOLECULES_CHEBI);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SMALL_MOLECULES_CHEBI + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Small_molecules_chEBI", p.getHeaderColumnNames().contains("Small_molecules_chEBI"));
        Assert.assertEquals(336, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for 1604", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("1604")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testSmallMoleculesKegg() {
        String data = getString(SMALL_MOLECULES_KEGG);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SMALL_MOLECULES_KEGG + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header Small_molecules_KEGG", p.getHeaderColumnNames().contains("Small_molecules_KEGG"));
        Assert.assertEquals(899, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for C00010", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("C00010")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    @Test
    public void testUniprotAccessionList() {
        String data = getString(UNIPROT_ACCESSION_LIST);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(UNIPROT_ACCESSION_LIST + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Looking for header GBM Uniprot", p.getHeaderColumnNames().contains("GBM Uniprot"));
        Assert.assertEquals(184, p.getAnalysisIdentifierSet().size());
        Assert.assertTrue("Looking for Q96GD4", p.getAnalysisIdentifierSet().contains(new AnalysisIdentifier("Q96GD4")));
        Assert.assertEquals(0, p.getWarningResponses().size());

    }

    /**
     * This is a 10MB files, we do not want to store a huge file in GitHub
     * If you want to test a 10mb file, please remove @Ignore in the method
     */
    private File writeHugeFile() {

        System.setProperty("line.separator", "\n");
        File file = new File(PATH + "max_file_size.txt");

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write("#Probeset\t");
            bw.write("Column 1\t");
            bw.write("Column 2\t");
            bw.write("Column 3");
//            bw.newLine();
            bw.write("\n");

            for (int i = 0; i < 310000; i++) {
                bw.write(i + "_abcd\t");

                bw.write("5.1234\t");
                bw.write("4.1234\t");
                bw.write("3.1234");
//                bw.newLine();
                bw.write("\n");

                if (i % 10000 == 0) {
                    bw.flush();
                }
            }

            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return file;
    }

    /**
     * This is a 10MB files, we do not want to store a huge file in GitHub
     * If you want to test a 10mb file, please remove @Ignore in the method
     */
    private File writeOneLineHugeFile() {

        File file = new File(PATH + "one_line_max_file_size.txt");

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);

            for (int i = 0; i < 750000; i++) {
                bw.write(i + "_abcdef\t");

                if (i % 10000 == 0) {
                    bw.flush();
                }
            }

            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return file;
    }

    @Test
    public void testPrideSample() {
        String data = getString(PRIDE_SAMPLE);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(PRIDE_SAMPLE + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertTrue("Header format in Pride file", p.getHeaderColumnNames().contains("PRIDE assay:27929"));
        Assert.assertEquals(3, p.getAnalysisIdentifierSet().size());
        //Assert.assertEquals(0, p.getWarningResponses().size());


    }

    @Test
    public void testSampleWithSpacesWithoutHeader() {
        String data = getString(SAMPLE_WITH_SPACES_WITHOUT_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SAMPLE_WITH_SPACES_WITHOUT_HEADER + " has failed.");
        }

        Assert.assertEquals(2, p.getHeaderColumnNames().size());
        Assert.assertEquals(2, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

    }

    @Test
    public void testSampleWithTabWithoutHeader() {
        String data = getString(SAMPLE_WITH_TAB_WITHOUT_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SAMPLE_WITH_TAB_WITHOUT_HEADER + " has failed.");
        }

        Assert.assertEquals(2, p.getHeaderColumnNames().size());
        Assert.assertEquals(2, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

    }

    @Test
    public void testSampleWithColonWithoutHeader() {
        String data = getString(SAMPLE_WITH_COLON_WITHOUT_HEADER);
        InputFormat_v3 p = new InputFormat_v3();
        try {
            p.parseData(data);
        } catch (ParserException e) {
            Assert.fail(SAMPLE_WITH_COLON_WITHOUT_HEADER + " has failed.");
        }

        Assert.assertEquals(1, p.getHeaderColumnNames().size());
        Assert.assertEquals(2, p.getAnalysisIdentifierSet().size());
        Assert.assertEquals(1, p.getWarningResponses().size());

    }
}

