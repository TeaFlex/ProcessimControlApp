package be.heh.std.model.core.write;

import android.widget.TextView;

import be.heh.std.imported.simaticS7.S7;

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
            //Integer writePlc = comS7.WriteArea(S7.S7AreaDB, getDatablock(), 0, 1)
        }
    }
}
