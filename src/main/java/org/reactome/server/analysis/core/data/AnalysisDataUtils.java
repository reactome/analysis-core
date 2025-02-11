package org.reactome.server.analysis.core.data;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.googlecode.concurrenttrees.radix.node.util.AtomicReferenceArrayListAdapter;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisDataUtils {
    private static Logger logger = LoggerFactory.getLogger("analysisDataLogger");

    static DataContainer getDataContainer(String fileName) throws Exception {
        String clazz = DataContainer.class.getSimpleName();
        logger.info(String.format("%s: Loading %s file...", clazz, fileName));
        long start = System.currentTimeMillis();
        DataContainer container = (DataContainer) AnalysisDataUtils.read(fileName);
        if(container == null){
            throw new Exception(String.format("%s: It was not possible to load %s", clazz, fileName));
        }
        container.initialize();
        long end = System.currentTimeMillis();
        logger.info(String.format("Loading %s file >> Done (%s)", DataContainer.class.getSimpleName(), FormatUtils.getTimeFormatted(end - start)));
        return container;
    }

    public static <T> T kryoCopy(T object) {
        long start = System.currentTimeMillis();
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(AtomicReferenceArrayListAdapter.class, new FieldSerializer<>(kryo, AtomicReferenceArrayListAdapter.class));
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
            kryo.setRegistrationRequired(false);
            kryo.setReferences(true);
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            // Kryo's default registration for AtomicReferenceArrayListAdapter is CollectionSerializer(it implements List).
            // CollectionSerializer doesn't know how to create a valid new instance of AtomicReferenceArrayListAdapter.
            // Overrides Kryo's default registration and tells it to use FieldSerializer instead of CollectionSerializer.
            // FieldSerializer uses reflection to read/write all fields of the class.
            kryo.register(AtomicReferenceArrayListAdapter.class, new FieldSerializer<>(kryo, AtomicReferenceArrayListAdapter.class));
            OutputStream file = new FileOutputStream(fileName);
            Output output = new Output(file);
            kryo.writeClassAndObject(output, container);
            output.close();
            container.initialize(); //At the end the data structure remains the same
            if (Main.VERBOSE) System.out.println(msgPrefix + " >> Done.");
        } catch (FileNotFoundException e) {
            if (Main.VERBOSE) System.err.println(msgPrefix + " >> An error has occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Object read(String fileName) {
        Object rtn = null;
        Input input = null;
        try {
            System.gc();
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(true);
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            kryo.register(AtomicReferenceArrayListAdapter.class, new FieldSerializer<>(kryo, AtomicReferenceArrayListAdapter.class));
            input = new Input(new FileInputStream(fileName));
            rtn = kryo.readClassAndObject(input);
        } catch (RuntimeException ex){
            logger.error(String.format("There was a problem loading the intermediate data file. %s", ex.getMessage()));
        } catch (FileNotFoundException e) {
            logger.error(String.format("%s has not been found. Please check the settings", fileName));
        } finally {
            if(input!=null) input.close();;
        }
        return rtn;
    }
}
