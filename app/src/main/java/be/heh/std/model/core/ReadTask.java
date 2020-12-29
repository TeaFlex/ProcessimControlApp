package be.heh.std.model.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;

public abstract class ReadTask {

    protected static final int MESSAGE_PRE_EXECUTE = 1;
    protected static final int MESSAGE_PROGRESS_UPDATE = 2;
    protected static final int MESSAGE_POST_EXECUTE = 3;

    protected AtomicBoolean isRunning = new AtomicBoolean(false);

    protected AutomateS7 plcS7;
    protected Thread readThread;

    protected S7Client comS7;
    protected String[] param = new String[10];
    protected byte[] datasPLC = new byte[512];

    private int datablock;
    protected ArrayList<byte[]> dbb;

    //Text view giving network state of the plc.
    private TextView net_status;

    public ReadTask(TextView net_status, int datablock) {
        this.net_status = net_status;
        comS7 = new S7Client();
        plcS7 = getAutomateS7();
        readThread = new Thread(plcS7);
        dbb = new ArrayList<>();
        this.datablock = datablock;
    }

    public int getDatablock() {
        return datablock;
    }

    public boolean isReading() {
        return isRunning.get();
    }

    public void stop() {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void start(String ip, String rack, String slot) {
        if(!readThread.isAlive()) {
            param[0] = ip;
            param[1] = rack;
            param[2] = slot;


            readThread.start();
            isRunning.set(true);
        }
    }

    protected abstract AutomateS7 getAutomateS7();

    protected abstract void downloadOnPreExecute(int... values);

    protected abstract void downloadOnProgressUpdate(int progress);

    protected abstract void downloadOnPostExecute();

    protected Handler monHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.getData().getIntArray("value_list"));
                    break;
                case MESSAGE_PROGRESS_UPDATE:
                    downloadOnProgressUpdate(msg.arg1);
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
                    break;
                default:
                    break;
            }
        }
    };

    protected abstract class AutomateS7 implements Runnable {

        protected Integer connect() {
            comS7.SetConnectionType(S7.S7_BASIC);
            Integer res = comS7.ConnectTo(param[0],Integer.parseInt(param[1]),Integer.parseInt(param[2]));
            net_status.setText(res.toString().equals("0") ? "UP" : "DOWN");
            return res;
        }

        protected void sendPostExecuteMessage() {
            Message postExecuteMsg = new Message();
            postExecuteMsg.what = MESSAGE_POST_EXECUTE;
            monHandler.sendMessage(postExecuteMsg);
        }

        protected void sendPreExecuteMessage(int... values) {
            Message preExecuteMsg = new Message();
            Bundle data = new Bundle();
            data.putIntArray("value_list", values);
            preExecuteMsg.what = MESSAGE_PRE_EXECUTE;
            preExecuteMsg.setData(data);
            monHandler.sendMessage(preExecuteMsg);
        }

        protected void sendProgressMessage(int i) {
            Message progressMsg = new Message();
            progressMsg.what = MESSAGE_PROGRESS_UPDATE;
            progressMsg.arg1 = i;
            monHandler.sendMessage(progressMsg);
        }
    }
}
