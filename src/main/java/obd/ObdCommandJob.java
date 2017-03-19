package obd;

import com.github.pires.obd.commands.ObdCommand;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ObdCommandJob {

    private Long id;
    private ObdCommand obdCommand;
    private ObdCommandJobState obdCommandJobState;

    public ObdCommandJob(ObdCommand command) {
        obdCommand = command;
        obdCommandJobState = ObdCommandJobState.NEW;
    }

    public enum ObdCommandJobState {
        NEW,
        RUNNING,
        FINISHED,
        EXECUTION_ERROR,
        BROKEN_PIPE,
        QUEUE_ERROR,
        NOT_SUPPORTED
    }
}
