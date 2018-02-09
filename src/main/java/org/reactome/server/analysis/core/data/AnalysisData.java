package org.reactome.server.analysis.core.data;

import org.reactome.server.analysis.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Used to load the DataContainer from the file only ONCE when the application is loaded
 * (either in a local execution or deployed in a server). The default constructor uses
 * the Kryo deserializer in AnalysisDataUtils to create the DataContainer and from this
 * moment on, the AnalysisData object is a PROXY to the DataContainer.
 * <p/>
 * *** IMPORTANT ***
 * Please note that every time that getHierarchiesData method is called, it retrieves a
 * clone of the HierarchiesData contained in DataContainer so the changes in it doesn't
 * happen to other instances of HierarchiesData
 * <p/>
 * *** NOTE ****
 * AnalysisData is the object to be used in the code, PLEASE avoid using the DataContainer
 * directly in the analysis (unless there is a good reason).
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Component
@Scope("singleton")
public class AnalysisData {
    private static Logger logger = LoggerFactory.getLogger("analysisDataLogger");

    static final Object LOADER_SEMAPHORE = new Object();

    private static DataContainer container = null;

    private IntermediateDataLoader intermediateDataLoader = null;

    private DataContainer getContainer() {
        if (container == null) {
            synchronized (LOADER_SEMAPHORE){
                if (container == null) {
                    logger.error(getClass().getName() + " [ERROR] : DataContainer has not been initialized.");
                }
            }
        }
        return container;
    }

    /**
     * Returns the physical entity container with all the complexes, sets and proteins in Reactome
     *
     * @return the physical entity container with all the complexes, sets and proteins in Reactome
     */
    public EntitiesContainer getPhysicalEntityContainer() {
        return this.getContainer().getEntitiesContainer();
    }

    /**
     * Returns a map from identifier to [resource, [EntityNode]]
     *
     * @return a map from identifier to [resource, [EntityNode]]
     */
    public IdentifiersMap<EntityNode> getEntitiesMap() {
        return getContainer().getEntitiesMap();
    }

    /**
     * Returns a map from identifier to [resource, [InteractorNode]]
     *
     * @return a map from identifier to [resource, [InteractorNode]]
     */
    public IdentifiersMap<InteractorNode> getInteractorsMap() {
        return getContainer().getInteractorsMap();
    }

    public Map<SpeciesNode, PathwayHierarchy> getPathwayHierarchies() {
        return this.getContainer().getHierarchiesData().getPathwayHierarchies();
    }

    /**
     * Initialise the AnalysisData object loading the content of the file into the container
     *
     * @param fileName the binary file containing the data structures for the analysis
     */
    @SuppressWarnings("unused")
    public void setFileName(String fileName) {
        if (container == null) {
            intermediateDataLoader = new IntermediateDataLoader(fileName);
            intermediateDataLoader.start();
        } else {
            logger.warn("Attempt to load the content file when previously loaded");
        }
    }

    public void interrupt(){
        if(intermediateDataLoader!=null && intermediateDataLoader.isAlive()){
            intermediateDataLoader.interrupt();
        }
        HierarchiesDataProducer.interruptProducer();
        container = null;
        System.gc();
    }

    class IntermediateDataLoader extends Thread {

        private String fileName;

        IntermediateDataLoader(String fileName) {
            super("IntermediateDataLoader");
            this.fileName = fileName;
        }

        @Override
        public void run() {
            synchronized (LOADER_SEMAPHORE) {
                try {
                    container = AnalysisDataUtils.getDataContainer(fileName);
                    //Note: HierarchiesDataProducer.getHierarchiesData is also sync with LOADER_SEMAPHORE
                    HierarchiesDataProducer.initializeProducer(container);
                } catch (InterruptedException e) {
                    logger.warn("The thread has been interrupted");
                } catch (Exception e){
                    logger.error(e.getMessage());
                } finally {
                    LOADER_SEMAPHORE.notifyAll();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


}
