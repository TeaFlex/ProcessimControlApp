package be.heh.std.model.core;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Map;

import be.heh.std.app.R;
import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadPillsTask extends ReadTask {

    private TextView reference;
    private TextView in_service;
    private TextView supply_asked;
    private TextView is_remote_controlled;
    private TextView nb_pills;
    private TextView nb_bottles;
    private ProgressBar progress;

    public ReadPillsTask(TextView reference, TextView in_service, TextView supply_asked,
                         TextView is_remote_controlled, TextView nb_pills, TextView nb_bottles,
                         ProgressBar progress, TextView net_status, int datablock) {

        super(net_status, datablock);
        this.reference = reference;
        this.in_service = in_service;
        this.nb_bottles = nb_bottles;
        this.nb_pills = nb_pills;
        this.progress = progress;
        this.supply_asked = supply_asked;
        this.is_remote_controlled = is_remote_controlled;

        //in service
        dbb.put(0, new byte[16]);
        //remote
        dbb.put(1, new byte[16]);
        //supply of pills(5, 10 or 15)
        dbb.put(4, new byte[16]);
        //pills per bottle
        dbb.put(15, new byte[16]);
        //bottles
        dbb.put(16, new byte[16]);
    }

    @Override
    protected AutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {
        if(values.length == 6) {
            nb_pills.setText(String.valueOf(values[0]));
            nb_bottles.setText(String.valueOf(values[1]));
            supply_asked.setText(String.valueOf(values[2]));
            is_remote_controlled.setText((values[3] == 0)? R.string.off : R.string.on);
            in_service.setText((values[4] == 0)? R.string.off : R.string.on);
            reference.setText(String.valueOf(values[5]));
        }
    }

    @Override
    protected void downloadOnProgressUpdate(int prog) {
        progress.setProgress(prog);
    }

    @Override
    protected void downloadOnPostExecute() {
        nb_pills.setText("-");
        nb_bottles.setText("-");
        supply_asked.setText("-");
        is_remote_controlled.setText("-");
        in_service.setText("-");
        reference.setText("-");
        progress.setProgress(0);
    }

    private class PillsAutomateS7 extends AutomateS7 {
        @Override
        public void run() {
            try {
                Integer res = connect();
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                int numCPU = -1;

                if (res.equals(0) && result.equals(0))
                    numCPU = Integer.parseInt(orderCode.Code().substring(5, 8));
                else
                    numCPU = 0000;

                sendPreExecuteMessage(numCPU);

                while(isRunning.get()){
                    if (res.equals(0)){
                        int retInfo = comS7.ReadArea(S7.S7AreaDB,getDatablock(),9,2, datasPLC);
                        comS7.ReadArea(S7.S7AreaDB, getDatablock(), 16, 2, dbb.get(16));
                        comS7.ReadArea(S7.S7AreaDB, getDatablock(), 15, 2, dbb.get(15));

                        int retInfoBis = 0;
                        for(Map.Entry<Integer, byte[]> entry : dbb.entrySet()) {
                            Integer key = entry.getKey();
                            int[] bits = {0, 1, 4};//dbbs containing bit values
                            int amount = (Arrays.asList(bits).contains(key.intValue()))? 8 : 2;
                            retInfoBis = comS7.ReadArea(S7.S7AreaDB, getDatablock(), key, amount, dbb.get(key));
                            if (retInfoBis != 0) {
                                Log.i("ERROR", String.valueOf(retInfoBis));
                                break;
                            }
                        }


                        int init_data = 0;

                        int nb_pills_data = 0;
                        int nb_bottles_data = 0;
                        int supply_asked_data = 0;
                        int remote_data = 0;
                        int in_service = 0;

                        if (retInfo == 0) {
                            init_data = S7.GetWordAt(datasPLC, 0);
                            sendProgressMessage(init_data);
                            //Beginning of working zone

                            nb_pills_data = S7.GetWordAt(dbb.get(15), 0);
                            nb_bottles_data = S7.GetWordAt(dbb.get(16), 0);

                            supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 3)? 0 : 5;
                            supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 4)? 0 : 10;
                            supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 5)? 0 : 15;
                            in_service = S7.GetBitAt(dbb.get(0), 0, 0)? 0 : 1;
                            remote_data = S7.GetBitAt(dbb.get(1), 0, 7)? 0 : 1;

                            sendPreExecuteMessage(nb_pills_data, nb_bottles_data, supply_asked_data,
                                    remote_data, in_service, numCPU);
                            //End of working zone
                        }
                    }
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
    }
}
