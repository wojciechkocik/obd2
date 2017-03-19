package obd.concurrency;

import com.github.pires.obd.exceptions.UnsupportedCommandException;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import obd.ObdCommandJob;
import obd.ReaderObserver;

import java.util.concurrent.BlockingQueue;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */
@Slf4j
class ObdCommandsConsumer extends Thread {

    private BlockingQueue<ObdCommandJob> jobsQueue;
    private ReaderObserver reader;

    private SerialPort serialPort;

    public ObdCommandsConsumer(BlockingQueue<ObdCommandJob> jobsQueue, ReaderObserver reader) {
        this.jobsQueue = jobsQueue;
        this.reader = reader;
    }

    public ObdCommandsConsumer(BlockingQueue<ObdCommandJob> jobsQueue, SerialPort serialPort) {
        this.jobsQueue = jobsQueue;
        this.serialPort = serialPort;
    }

    @Override
    public void run() {
        try {
            executeQueue();
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

    private void executeQueue() throws InterruptedException {
        log.debug("Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();

                // log job
                log.debug("Taking job[" + job.getId() + "] from queue..");

                if (job.getObdCommandJobState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    log.debug("Job state is NEW. Run it..");
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    final ObdCommandJob job2 = job;
                    reader.read(job2);



                } else
                {
                    // log not new job
                    log.debug(
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
                }

            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                log.debug("Command not supported. -> " + u.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                log.debug("Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                final ObdCommandJob job2 = job;
//                Thread.sleep(300);
//                log.info(job2.getObdCommand().getFormattedResult());

//                ObdJobEvent obdJobEvent = new ObdJobEvent();
//                obdJobEvent.setObdCommandJob(job2);
//                EventBus.getDefault().post(obdJobEvent);
            }


        }
    }
}
