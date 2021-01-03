package be.heh.std.model.core.write;

public class WritePillsTask extends WriteTask {

    public WritePillsTask(int datablock) {
        super(datablock);

        dbb.put(5, new byte[16]);
        dbb.put(6, new byte[16]);
        dbb.put(7, new byte[16]);
        dbb.put(8, new byte[16]);
        dbw.put(18, new byte[16]);
    }

    @Override
    public WriteAutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    public class PillsAutomateS7 extends WriteAutomateS7 {

        @Override
        protected void toRun() {
            Integer writePlc = 0;

            writePlc = writeBytes();
            writePlc = writeWords();
        }
    }
}
