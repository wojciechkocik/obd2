import gnu.io.CommPortIdentifier;
import lombok.extern.slf4j.Slf4j;
import obd.concurrency.ObdService;

import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * Created by Wojciech on 18.03.2017.
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        System.out.print(new Date());
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();

        log.info("Available ports:");
        while (ports.hasMoreElements()) {
            String portName = ((CommPortIdentifier) ports.nextElement()).getName();
            log.info(String.format("--> %s", portName));
        }

        Scanner scanner = new Scanner(System.in);

        String port;
        if (args.length > 0) {
            port = args[0];
        } else {
            log.info("Enter binded com port:");
            port = scanner.next();
        }
        log.info(String.format("Port %s was chosen", port));

        log.info("Starting service...");
        ObdService obdService = new ObdService(port);
        obdService.start();
    }
}
