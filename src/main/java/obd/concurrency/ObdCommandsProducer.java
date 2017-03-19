package obd.concurrency;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import lombok.extern.slf4j.Slf4j;
import obd.ObdCommandJob;

import java.util.concurrent.BlockingQueue;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */
@Slf4j
class ObdCommandsProducer extends Thread {

    private BlockingQueue<ObdCommandJob> jobsQueue;
    private long queueCounter = 0;


    public ObdCommandsProducer(BlockingQueue<ObdCommandJob> jobsQueue) {
        this.jobsQueue = jobsQueue;
    }

    private void queueCommands() {
        queueJob(new SpeedCommand());
        queueJob(new RPMCommand());
        queueJob(new EngineCoolantTemperatureCommand());
        queueJob(new MassAirFlowCommand());
        queueJob(new ThrottlePositionCommand());
    }

    @Override
    public void run() {
        queueCommands();
    }

    public void queueJob(ObdCommand job) {

        ObdCommandJob obdCommandJob = new ObdCommandJob(job);

        queueCounter++;
        log.debug("Adding job[" + queueCounter + "] to queue..");

        obdCommandJob.setId(queueCounter);
        try {
            jobsQueue.put(obdCommandJob);
            log.debug("Job queued successfully.");
        } catch (InterruptedException e) {
            obdCommandJob.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.QUEUE_ERROR);
            log.debug("Failed to queue job.");
        }
    }
}
