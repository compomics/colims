
package com.compomics.colims.core.bean;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("consoleMonitor")
public class ProgressConsoleMonitor {

    private static final double STEP_VALUE = 5.0;

    private double previousProgressValue;

    private final EventBus eventBus;

    @Autowired
    public ProgressConsoleMonitor(EventBus eventBus){
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init(){
       //register to event bus
       eventBus.register(this);
    }

    @Subscribe
    public void onProgressEvent(final ProgressEvent progressEvent){
        if(progressEvent.getProgress() - previousProgressValue > STEP_VALUE){
            //print the progress to the console
            //System.out.println("------------------ " + progressEvent.getDescription() + " : " + progressEvent.getProgress());
            previousProgressValue = progressEvent.getProgress();
        }
    }

}
