package be.heh.std.model.core.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.app.R;
import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.imported.simaticS7.S7OrderCode;

public abstract class ReadTask {

    protected static final int MESSAGE_PRE_EXECUTE = 1;
    protected static final int MESSAGE_PROGRESS_UPDATE = 2;
    protected static final int MESSAGE_POST_EXECUTE = 3;

    protected AtomicBoolean isRunning = new AtomicBoolean(false);

    protected ReadAutomateS7 plcS7;
    protected Thread readThread;

    protected S7Client comS7;
    protected String[] param = new String[10];
    protected byte[] datasPLC = new byte[512];

    private int datablock;
    protected HashMap<Integer, byte[]> dbb;
    protected HashMap<Integer, byte[]> dbw;

    //Text view giving network state of the plc.
    private TextView net_status;
    protected Activity current_activity;
    protected Context context;

    public ReadTask(TextView net_status, Activity current_activity, int datablock) {
        this.net_status = net_status;
        this.context = current_activity.getApplicationContext();
        comS7 = new S7Client();
        plcS7 = getAutomateS7();
        readThread = new Thread(plcS7);
        dbb = new HashMap<>();
        dbw = new HashMap<>();
        this.datablock = datablock;
        this.current_activity = current_activity;
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

    protected String onOrOff(int value) {
        return context.getString(value == 0 ? R.string.off: R.string.on);
    }

    protected abstract ReadAutomateS7 getAutomateS7();

    protected abstract void downloadOnPreExecute(int... values);

    protected abstract void downloadOnPostExecute();

    @SuppressLint("HandlerLeak")
    protected Handler monHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.getData().getIntArray("value_list"));
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
                    break;
                default:
                    break;
            }
        }
    };

    protected abstract class ReadAutomateS7 implements Runnable {

        protected abstract void toRun(int numCPU);

        @Override
        public void run() {
            try {
                Integer res = connect();

                while(res != 0){
                    Log.e("CONNECTION FAILED", String.format("Connection to %s failed.", param[0]));
                    res = connect();
                    Thread.sleep(1000);
                }

                Log.i(String.format("CONNECT STATE TO %s", param[0]), String.format("code %d", res));
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                int numCPU = -1;

                if (res.equals(0) && result.equals(0))
                    numCPU = Integer.parseInt(orderCode.Code().substring(5, 8));
                else
                    numCPU = 0000;

                while(isRunning.get()){
                    if (res.equals(0))
                        toRun(numCPU);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendPostExecuteMessage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Integer connect() {
            comS7.SetConnectionType(S7.S7_BASIC);
            Integer res = comS7.ConnectTo(param[0],Integer.parseInt(param[1]),Integer.parseInt(param[2]));
            if(!res.toString().equals("0")) downloadOnPostExecute();
            current_activity.runOnUiThread(() -> net_status.setText(context.getString(R.string.net_status, context.getString(
                    res.toString().equals("0") ? R.string.up : R.string.down))));
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

        protected int byteReader() {
            int retInfoBis = 0;
            for(Map.Entry<Integer, byte[]> entry : dbb.entrySet()) {
                Integer key = entry.getKey();
                retInfoBis = comS7.ReadArea(S7.S7AreaDB, getDatablock(), key, 8, dbb.get(key));
                if (retInfoBis != 0) {
                    Log.i("ERROR read dbb", String.valueOf(retInfoBis));
                    break;
                }
            }
            return retInfoBis;
        }

        protected int intReader() {
            int retInfoBis = 0;
            for(Map.Entry<Integer, byte[]> entry : dbw.entrySet()) {
                Integer key = entry.getKey();
                retInfoBis = comS7.ReadArea(S7.S7AreaDB, getDatablock(), key, 2, dbw.get(key));
                if (retInfoBis != 0) {
                    Log.i("ERROR read dbw", String.valueOf(retInfoBis));
                    break;
                }
            }
            return retInfoBis;
        }
    }
}
