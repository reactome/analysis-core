package org.reactome.server.analysis.core.data;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.model.DataContainer;
import org.reactome.server.analysis.core.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisDataUtils {
    private static Logger logger = LoggerFactory.getLogger(AnalysisDataUtils.class.getName());

    public static DataContainer getDataContainer(InputStream file) {
        logger.info(String.format("Loading %s file...", DataContainer.class.getSimpleName()));
        long start = System.currentTimeMillis();
        DataContainer container = (DataContainer) AnalysisDataUtils.read(file);
        container.initialize();
        long end = System.currentTimeMillis();
        logger.info(String.format("Time to load %s file >> %s", DataContainer.class.getSimpleName(), FormatUtils.getTimeFormatted(end - start)));
        return container;
    }

    public static <T> T kryoCopy(T object) {
        long start = System.currentTimeMillis();
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        T rtn = kryo.copy(object);
        long end = System.currentTimeMillis();
        logger.trace(String.format("%s cloned in %d ms", object.getClass().getSimpleName(), end - start));
        return rtn;
    }

    public static void kryoSerialisation(DataContainer container, String fileName) {
        String msgPrefix = String.format("\rStoring %s data into file %s", container.getClass().getSimpleName(), fileName);
        if (Main.VERBOSE) System.out.print(msgPrefix + " >> Please wait...");
        try {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            OutputStream file = new FileOutputStream(fileName);
            Output output = new Output(file);
            kryo.writeClassAndObject(output, container);
            output.close();
            container.initialize(); //At the end the data structure remains the same
            if (Main.VERBOSE) System.out.println(msgPrefix + " >> Done.");
        } catch (FileNotFoundException e) {
            if (Main.VERBOSE) System.out.println(msgPrefix + " >> An error has occurred: " + e.getMessage());
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private static Object read(InputStream file) {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Input input = new Input(file);
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }
}
