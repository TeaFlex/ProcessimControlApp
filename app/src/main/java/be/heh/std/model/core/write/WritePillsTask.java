package be.heh.std.model.core.write;

public class WritePillsTask extends WriteTask {

    public WritePillsTask(int datablock) {
        super(datablock);
    }

    @Override
    public AutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    public class PillsAutomateS7 extends AutomateS7 {

        @Override
        protected void toRun() {

        }
    }
}
