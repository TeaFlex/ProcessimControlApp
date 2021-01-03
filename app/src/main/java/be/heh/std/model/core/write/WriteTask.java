package be.heh.std.model.core.write;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.model.core.read.ReadTask;

public abstract class WriteTask {

    protected AtomicBoolean isRunning = new AtomicBoolean(false);

    protected Thread writeThread;
    protected WriteAutomateS7 plcS7;

    protected S7Client comS7;
    private String[] param = new String[10];

    private int datablock;
    protected HashMap<Integer, byte[]> dbb;
    protected HashMap<Integer, byte[]> dbw;
    protected HashMap<Integer, byte[]> dbbyte;

    public WriteTask(int datablock) {
        this.datablock = datablock;
        comS7 = new S7Client();
        plcS7 = getAutomateS7();
        writeThread = new Thread(plcS7);
        dbb = new HashMap<>();
        dbw = new HashMap<>();
    }

    public abstract WriteAutomateS7 getAutomateS7();

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

    public S7Client getComS7() {
        return comS7;
    }

    public void setComS7(S7Client comS7) {
        this.comS7 = comS7;
    }

    public boolean isWriting() {
        return isRunning.get();
    }

    public void setWriteBoolDbb(int bit, boolean value, int index_dbb) throws IndexOutOfBoundsException {
        if(dbb.containsKey(index_dbb)) {
            if (value) dbb.get(index_dbb)[0] = (byte) (bit | dbb.get(index_dbb)[0]);
            else dbb.get(index_dbb)[0] = (byte) (~bit & dbb.get(index_dbb)[0]);
        }
        else throw new IndexOutOfBoundsException();
    }

    public void setWordAtDbw(int value,int pos,  int index_dbw) throws IndexOutOfBoundsException {
        if(dbw.containsKey(index_dbw))
            S7.SetWordAt(dbw.get(index_dbw), pos, value);
        else throw new IndexOutOfBoundsException();
    }

    public void setBitAtDbb(int bit, boolean value,int pos, int index_dbb) throws IndexOutOfBoundsException {
        if(dbb.containsKey(index_dbb))
            S7.SetBitAt(dbb.get(index_dbb), pos, bit, value);
        else throw new IndexOutOfBoundsException();
    }

    public void setByteAtDbb(boolean value, int pos,  int index_dbb) throws IndexOutOfBoundsException {
        if(!dbb.containsKey(index_dbb))
            throw new IndexOutOfBoundsException();

        for(int i = 0; i < 8; i++)
            S7.SetBitAt(dbb.get(index_dbb), pos, i, value);
    }

    protected abstract class WriteAutomateS7 implements Runnable{

        @Override
        public void run() {
            try {
                Integer res = connect();
                while(res != 0){
                    Log.e("CONNECTION FAILED", String.format("Connection to %s failed.", param[0]));
                    res = connect();
                    Thread.sleep(1000);
                }
                while(isRunning.get() && res.equals(0)){
                    toRun();
                    Log.i(String.format("WRITING ON %s", param[0]), "Operational");
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        protected abstract void toRun();

        protected Integer connect() {
            comS7.SetConnectionType(S7.S7_BASIC);
            Integer res = comS7.ConnectTo(param[0],Integer.parseInt(param[1]),
                    Integer.parseInt(param[2]));
            return res;
        }

        protected int writeWords() {
            Integer writePlc = 0;
            for (Integer key : dbw.keySet())
                writePlc = comS7.WriteArea(S7.S7AreaDB, getDatablock(), key, 2, dbw.get(key));
            return writePlc;
        }

        protected int writeBytes() {
            Integer writePlc = 0;
            for (Integer key : dbb.keySet())
                writePlc = comS7.WriteArea(S7.S7AreaDB, getDatablock(), key, 1, dbb.get(key));
            return writePlc;
        }

    }


}
