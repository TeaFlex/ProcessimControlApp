package be.heh.std.model.core.write;

public class WritePillsTask extends WriteTask {

    public WritePillsTask(int datablock) {
        super(datablock);
    }

    @Override
    public WriteAutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    public class PillsAutomateS7 extends WriteAutomateS7 {

        @Override
        protected void toRun() {

        }
    }
}
