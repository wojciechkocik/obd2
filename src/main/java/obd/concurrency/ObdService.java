package obd.concurrency;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.*;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import obd.ObdCommandJob;
import obd.ReaderObserver;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by Wojciech on 19.03.2017.
 */
@Slf4j
public class ObdService {
    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();

    private ScheduledExecutorService producerExecutorService;
    private Executor consumerExecutor;

    private ObdCommandsProducer producer;
    private ObdCommandsConsumer consumer;

    private SerialPort serialPort;

    private synchronized ReaderObserver read(){
        return job -> {
            job.getObdCommand().run(serialPort.getInputStream(), serialPort.getOutputStream());

            //hack for waiting for inputstream
            long start = System.currentTimeMillis();
            while (serialPort.getInputStream().available() == 0){
                if(System.currentTimeMillis() - start > 1000){
                    return;
                }
            }
            log.info(job.getObdCommand().getFormattedResult());
        };
    };

    public ObdService(String port) {

        serialPort = connectSerial(port);

        producer = new ObdCommandsProducer(jobsQueue);
        consumer = new ObdCommandsConsumer(jobsQueue, read());

        producerExecutorService = new ScheduledThreadPoolExecutor(1);
        consumerExecutor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        consumerExecutor.execute(consumer);

       // initConnection();
        producerExecutorService.scheduleAtFixedRate(producer, 0, 1, TimeUnit.SECONDS);
    }

    private SerialPort connectSerial(String port) {
        SerialPort serialPort = null;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
            serialPort = (SerialPort) portIdentifier.open("OBD2-connection", 0);
        } catch (NoSuchPortException | PortInUseException e) {
            log.error(e.getMessage());
        }
        return serialPort;
    }

    private void initConnection() {
        // Let's configure the connection.
        log.debug("Queueing jobs for connection configuration..");
        queueJob(new ObdResetCommand());

        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        queueJob(new EchoOffCommand());

    /*
     * Will read second-time based on tests.
     *
     * TODO this can be done w/o having to queue jobs by just issuing
     * command.run(), command.getResult() and validate the result.
     */
        queueJob(new EchoOffCommand());
        queueJob(new LineFeedOffCommand());
        queueJob(new TimeoutCommand(62));

        // Get protocol from preferences
//        final String protocol = sharedPreferences.getString(Config.PROTOCOLS_LIST_KEY, "AUTO");
        queueJob(new SelectProtocolCommand(ObdProtocols.valueOf("AUTO")));

        // Job for returning dummy data
        queueJob(new AmbientAirTemperatureCommand());
        log.debug("Initialization jobs queued.");
    }

    private void queueJob(ObdCommand job) {
        producer.queueJob(job);
    }
}