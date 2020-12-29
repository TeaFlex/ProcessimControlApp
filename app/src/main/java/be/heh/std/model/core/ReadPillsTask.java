package be.heh.std.model.core;

import android.widget.TextView;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadPillsTask extends ReadTask {

    private TextView nb_pills;
    private TextView nb_bottles;
    private TextView progress;

    public ReadPillsTask(TextView net_status, TextView nb_pills,
                         TextView nb_bottles, TextView progress, int datablock) {
        super(net_status, datablock);
        this.nb_bottles = nb_bottles;
        this.nb_pills = nb_pills;
        this.progress = progress;

        dbb.add(15, new byte[16]);
        dbb.add(16, new byte[16]);
    }

    @Override
    protected AutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {
        nb_pills.setText(String.valueOf(values[0]));
        nb_bottles.setText(String.valueOf(values[1]));
    }

    @Override
    protected void downloadOnProgressUpdate(int prog) {
        progress.setText(String.format("%d %", prog));
    }

    @Override
    protected void downloadOnPostExecute() {
        progress.setText("-");
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
                        int data = 0;
                        if (retInfo == 0) {
                            data = S7.GetWordAt(datasPLC, 0);
                            sendProgressMessage(data);
                            //Beginning of working zone

                            comS7.ReadArea(S7.S7AreaDB, getDatablock(), 16, 2, dbb.get(16));
                            comS7.ReadArea(S7.S7AreaDB, getDatablock(), 15, 2, dbb.get(15));

                            int pills = S7.GetWordAt(dbb.get(15), 0);
                            int bottles = S7.GetWordAt(dbb.get(16), 0);

                            sendPreExecuteMessage(pills, bottles);
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
