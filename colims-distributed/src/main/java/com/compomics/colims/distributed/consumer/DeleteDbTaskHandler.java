package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class handles a DeleteDbTask.
 *
 * @author Niels Hulstaert
 */
@Component("deleteDbTaskHandler")
public class DeleteDbTaskHandler {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DeleteDbTaskHandler.class);

    @Autowired
    private CompletedTaskProducer completedTaskProducer;
    @Autowired
    private DbTaskErrorProducer dbTaskErrorProducer;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AnalyticalRunService analyticalRunService;

    public void handleDeleteDbTask(DeleteDbTask deleteDbTask) {
        Long started = System.currentTimeMillis();

        Class entityClass = deleteDbTask.getDbEntityClass();
        Long entityId = deleteDbTask.getEnitityId();
        try {
            if (entityClass.equals(Project.class)) {
                projectService.deleteById(entityId);
            } else if (entityClass.equals(Experiment.class)) {
                experimentService.deleteById(entityId);
            } else if (entityClass.equals(Sample.class)) {
                sampleService.deleteById(entityId);
            } else if (entityClass.equals(AnalyticalRun.class)) {
                analyticalRunService.deleteById(entityId);
            } else {
                throw new IllegalArgumentException("Unsupported DB entity class to delete.");
            }

            //wrap the DeleteDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), deleteDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the DeleteDbTask in a DbTaskError and send it to the error queue
            dbTaskErrorProducer.sendDbTaskError(new DbTaskError(started, System.currentTimeMillis(), deleteDbTask, e));
        }
    }

}
