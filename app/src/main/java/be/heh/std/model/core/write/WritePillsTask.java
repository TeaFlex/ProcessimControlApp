package be.heh.std.model.core.write;

import android.widget.TextView;

public class WritePillsTask extends WriteTask {

    public WritePillsTask(TextView net_status, int datablock) {
        super(net_status, datablock);
    }

    @Override
    public AutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    public class PillsAutomateS7 extends AutomateS7 {

        @Override
        protected void toRun() {
            //S7.Set
        }
    }
}
