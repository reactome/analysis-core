package org.reactome.server.analysis.core.data;

import org.reactome.server.analysis.core.methods.EnrichmentAnalysis;
import org.reactome.server.analysis.core.model.DataContainer;
import org.reactome.server.analysis.core.model.HierarchiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("WeakerAccess")
public class HierarchiesDataProducer {

    private static Logger logger = LoggerFactory.getLogger("analysisDataLogger");

    private static HierarchiesDataProducer producer;
    private static Thread backgroundProducer;

    private DataContainer data;

    private HierarchiesDataProducer(DataContainer data) {
        this.data = data;
        if (HierarchiesDataContainer.POOL_SIZE > 1) {
            logger.trace("Initialising the background producer...");
            backgroundProducer = new Thread(new BackgroundProducer());
            backgroundProducer.setName(BackgroundProducer.class.getSimpleName());
            backgroundProducer.start();
            logger.info("Hierarchy content background producer initialised");
        } else {
            logger.error("No background producer initialised");
        }
    }

    static void initializeProducer(DataContainer data) {
        if (producer == null) {
            producer = new HierarchiesDataProducer(data);
        } else {
	    logger.warn("Already initialized. Ensure you do not use two data containers or you do not initialise this object with the same twice.");
        }
    }

    public static void interruptProducer() {
        if (backgroundProducer != null) {
            backgroundProducer.interrupt();
        } else {
            logger.warn("The producer has not previously been initialized.");
        }
    }

    static HierarchiesData getHierarchiesData() {
        if (producer != null) {
            return producer.data.getHierarchiesData();
        }
        synchronized (AnalysisData.LOADER_SEMAPHORE) {
            if (producer != null) {
                return producer.data.getHierarchiesData();
            } else {
                logger.error("This class needs to be initialised with the data structure to perform the analysis with");
                return null;
            }
        }
    }

    /**
     * When running just checks when the server is lazy ( EnrichmentAnalysis.ANALYSIS_COUNT equals zero ) and produces
     * the data needed to the analysis until the container pool is full (NOTE: It stops as soon as there are analysis
     * running).
     * <p>
     * IMPORTANT: When there are more analysis running than the size of the POOL of objects, new analysis while produce
     * the data object by demand
     */
    class BackgroundProducer implements Runnable {
        private boolean active = true;

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + " thread started");
            while (active) {
                try {
                    synchronized (EnrichmentAnalysis.ANALYSIS_SEMAPHORE) {
                        if (HierarchiesDataContainer.isFull() || EnrichmentAnalysis.getAnalysisCount() > 0) {
                            EnrichmentAnalysis.ANALYSIS_SEMAPHORE.wait();
                        }
                    }
                    HierarchiesDataContainer.put(data.getHierarchiesData());
                } catch (InterruptedException e) {
                    active = false;
                    data = null; System.gc();
                    logger.info(Thread.currentThread().getName() + ": data has been cleaned up and thread interrupted");
                }
            }
        }
    }
}
