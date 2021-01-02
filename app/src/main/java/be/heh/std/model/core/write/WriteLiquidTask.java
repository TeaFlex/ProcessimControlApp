package be.heh.std.model.core.write;

public class WriteLiquidTask extends WriteTask{

    public WriteLiquidTask(int datablock) {
        super(datablock);

        dbb.put(2, new byte[16]);
        dbb.put(3, new byte[16]);
        dbw.put(24, new byte[16]);
        dbw.put(26, new byte[16]);
        dbw.put(28, new byte[16]);
        dbw.put(30, new byte[16]);
    }

    @Override
    public WriteAutomateS7 getAutomateS7() {
        return new LiquidAutomateS7();
    }

    private class LiquidAutomateS7 extends WriteAutomateS7 {

        @Override
        protected void toRun() {
            Integer writePlc = 0;

            writePlc = writeBits();
            writePlc = writeInts();
        }
    }
}
