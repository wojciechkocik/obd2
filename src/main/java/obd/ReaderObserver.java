package obd;

import java.io.IOException;

/**
 * Created by Wojciech on 19.03.2017.
 */
public interface ReaderObserver {
    void read(ObdCommandJob job) throws IOException, InterruptedException;
}
