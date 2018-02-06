package org.reactome.server.analysis.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class FileUtil {

    private static Logger logger = LoggerFactory.getLogger("importLogger");

    public static void checkFileName(String fileName){
        File file = new File(fileName);

        if(file.isDirectory()){
            String msg = fileName + " is a folder. Please specify a valid file name.";
            System.err.println(msg);
            logger.error(msg);
            System.exit( 1 );
        }

        if(file.getParent()==null){
            file = new File("./" + fileName);
        }
        Path parent = Paths.get(file.getParent());
        if(!Files.exists(parent)){
            String msg = parent + " does not exist.";
            System.err.println(msg);
            logger.error(msg);
            System.exit( 1 );
        }

        if(!file.getParentFile().canWrite()){
            String msg = "No write access in " + file.getParentFile();
            System.err.println(msg);
            logger.error(msg);
            System.exit( 1 );
        }

        logger.trace(fileName + " is a valid file name");
    }
}
