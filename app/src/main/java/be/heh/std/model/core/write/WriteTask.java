package be.heh.std.model.core.write;

import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.app.R;
import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;

public abstract class WriteTask {

    protected AtomicBoolean isRunning = new AtomicBoolean(false);

    protected Thread writeThread;
    protected AutomateS7 plcS7;

    protected S7Client comS7;
    private String[] param = new String[10];

    private int datablock;
    protected HashMap<Integer, byte[]> dbb;

    private TextView net_status;

    public WriteTask(TextView net_status, int datablock) {
        this.net_status = net_status;
        this.datablock = datablock;
        comS7 = new S7Client();
        plcS7 = getAutomateS7();
        writeThread = new Thread(plcS7);
        dbb = new HashMap<Integer, byte[]>();
    }

    public abstract AutomateS7 getAutomateS7();

    public void start(String ip, String rack, String slot) {
        if(!writeThread.isAlive()) {
            param[0] = ip;
            param[1] = rack;
            param[2] = slot;

            writeThread.start();
            isRunning.set(true);
        }
    }

    public void stop() {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    public int getDatablock() {
        return datablock;
    }

    public boolean isWriting() {
        return isRunning.get();
    }

    protected abstract class AutomateS7 implements Runnable{

        @Override
        public void run() {
            try {
                Integer res = connect();
                if(isRunning.get() && res.equals(0))
                    toRun();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        protected abstract void toRun();

        protected Integer connect() {
            comS7.SetConnectionType(S7.S7_BASIC);
            Integer res = comS7.ConnectTo(param[0],Integer.parseInt(param[1]),Integer.parseInt(param[2]));
            net_status.setText(res.toString().equals("0") ? R.string.up : R.string.down);
            return res;
        }

        protected void setWriteBoolDbb(int b, int v, byte[]dbb) {
            if (v == 1) dbb[0] = (byte) (b | dbb[0]);
            else dbb[0] = (byte) (~b & dbb[0]);
        }

        protected void setBitAtDbb(int b, int v, byte[]dbb) {
            byte[] powerOf2 = {(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x16,
                    (byte) 0x32, (byte) 0x64, (byte) 0xA0};

            b = b < 0 ? 0 : b;
            b = b > 7 ? 7 : b;

            if (v == 1) dbb[0] = (byte) (dbb[0] | powerOf2[b]);
            else dbb[0] = (byte) (dbb[0] & ~powerOf2[b]);
        }
    }
}
