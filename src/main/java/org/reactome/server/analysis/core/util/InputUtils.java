package org.reactome.server.analysis.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.model.UserData;
import org.reactome.server.analysis.core.parser.InputFormat;
import org.reactome.server.analysis.core.parser.exception.ParserException;
import org.reactome.server.analysis.core.result.external.ExternalAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Parses the input to create a UserData object with the data to be analysed
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public abstract class InputUtils {

    private static Logger logger = LoggerFactory.getLogger("importLogger");

    private static final ObjectMapper mapper = new ObjectMapper();

    public static UserData getUserData(String input) throws IOException, ParserException {
        String md5 = DigestUtils.md5DigestAsHex(input.getBytes());
        return processData(input, md5);
    }

    public static UserData getUserData(InputStream is) throws IOException, ParserException {
        return getUserData(IOUtils.toString(is));
    }

    public static ExternalAnalysisResult getExternalAnalysisResult(String input) throws IOException {
        return mapper.readValue(input, ExternalAnalysisResult.class);
    }

    public static ExternalAnalysisResult getExternalAnalysisResult(InputStream is) throws IOException {
        return getExternalAnalysisResult(IOUtils.toString(is));
    }

    private static boolean isComment(String line){
        return line.startsWith("#") || line.startsWith("//");
    }

    private static List<String> getHeaderColumnNames(String line){
        line = line.replaceAll("^(#|//)", "");
        List<String> columnNames = new LinkedList<String>();
        for (String columnName : line.split("\\t")) {
            columnNames.add(StringEscapeUtils.escapeJava(columnName.trim())); //Kryo serialization error instead :(
        }
        return columnNames; // Arrays.asList(line.split("\\s"));
    }

    private static AnalysisIdentifier getAnalysisIdentifier(String line){
        AnalysisIdentifier rtn = null;
        if(line==null || line.isEmpty()) return rtn;
        String[] data = line.split("\\t");
        if(data.length>0){
            rtn = new AnalysisIdentifier(data[0].trim());
            for(int i=1; i<data.length; ++i){
                try{
                    rtn.add(Double.valueOf(data[i].trim()));
                }catch (NumberFormatException nfe){
                    rtn.add(null); //null won't be taken into account for the AVG
                }
            }
        }
        return rtn;
    }

    private static UserData processData(String data, String md5) throws IOException, ParserException {
        InputFormat parser = new InputFormat();
        parser.parseData(data);

        return new UserData(parser.getHeaderColumnNames(), parser.getAnalysisIdentifierSet(), md5, parser.getWarningResponses());
    }
}
