package be.heh.std.model.core.read;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
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


    public ReadPillsTask(TextView reference, TextView in_service, TextView supply_asked,
                         TextView is_remote_controlled, TextView nb_pills, TextView nb_bottles,
                         TextView net_status, int datablock) {

        super(net_status, datablock);
        this.reference = reference;
        this.in_service = in_service;
        this.nb_bottles = nb_bottles;
        this.nb_pills = nb_pills;
        this.supply_asked = supply_asked;
        this.is_remote_controlled = is_remote_controlled;

        //in service
        dbb.put(0, new byte[16]);
        //remote
        dbb.put(1, new byte[16]);
        //supply of pills(5, 10 or 15)
        dbb.put(4, new byte[16]);
        //pills per bottle
        dbw.put(15, new byte[16]);
        //bottles
        dbw.put(16, new byte[16]);
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
    protected void downloadOnPostExecute() {
        nb_pills.setText("-");
        nb_bottles.setText("-");
        supply_asked.setText("-");
        is_remote_controlled.setText("-");
        in_service.setText("-");
        reference.setText("-");
    }

    private class PillsAutomateS7 extends AutomateS7 {

        @Override
        protected void toRun(int numCPU) {
            int retInfo = comS7.ReadArea(S7.S7AreaDB,getDatablock(),9,2, datasPLC);

            retInfo = Math.max(byteReader(), retInfo);
            retInfo = Math.max(intReader(), retInfo);
            
            int init_data = 0;

            int nb_pills_data = 0;
            int nb_bottles_data = 0;
            int supply_asked_data = 0;
            int remote_data = 0;
            int in_service = 0;

            if (retInfo == 0) {
                init_data = S7.GetWordAt(datasPLC, 0);
                //Beginning of working zone

                nb_pills_data = S7.GetWordAt(dbw.get(15), 0);
                nb_bottles_data = S7.GetWordAt(dbw.get(16), 0);

                supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 3)? 0 : 5;
                if(supply_asked_data == 0)
                    supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 4)? 0 : 10;
                if(supply_asked_data == 0)
                    supply_asked_data = S7.GetBitAt(dbb.get(4), 0, 5)? 0 : 15;
                in_service = S7.GetBitAt(dbb.get(0), 0, 0)? 0 : 1;
                remote_data = S7.GetBitAt(dbb.get(1), 0, 7)? 0 : 1;

                sendPreExecuteMessage(nb_pills_data, nb_bottles_data, supply_asked_data,
                        remote_data, in_service, numCPU);
                //End of working zone
            }
        }
    }
}
