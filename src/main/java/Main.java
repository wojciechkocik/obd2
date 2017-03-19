import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Wojciech on 18.03.2017.
 */
public class Main{

    public static void main(String[] args){
        System.out.print(new Date());
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();


        while(ports.hasMoreElements()){
            System.out.println(((CommPortIdentifier)ports.nextElement()).getName());
        }

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/rfcomm0");
            SerialPort serialPort = (SerialPort) portIdentifier.open("NameOfConnection-whatever", 0);
            System.out.println("asf");
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
    }
}
