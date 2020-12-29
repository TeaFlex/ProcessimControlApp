package be.heh.std.model.core;

import android.widget.TextView;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadLiquidTask extends ReadTask {


    public ReadLiquidTask(TextView net_status, int datablock) {
        super(net_status, datablock);
    }

    @Override
    protected AutomateS7 getAutomateS7() {
        return new LiquidAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {

    }

    @Override
    protected void downloadOnProgressUpdate(int progress) {

    }

    @Override
    protected void downloadOnPostExecute() {

    }

    private class LiquidAutomateS7 extends AutomateS7 {
        @Override
        public void run() {
            try {
                Integer res = connect();
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                int numCPU = -1;
                if (res.equals(0) && result.equals(0)) {
                    numCPU = Integer.parseInt(orderCode.Code().substring(5, 8));
                }
                else numCPU = 0000;
                sendPreExecuteMessage(numCPU);

                while(isRunning.get()){
                    if (res.equals(0)){
                        int retInfo = comS7.ReadArea(S7.S7AreaDB, getDatablock(),9,2,datasPLC);
                        int data=0;
                        if (retInfo ==0) {
                            data = S7.GetWordAt(datasPLC, 0);
                            sendProgressMessage(data);
                            //Beginning of working zone

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
                System.out.println(e.getMessage());
            }

        }
    }
}
