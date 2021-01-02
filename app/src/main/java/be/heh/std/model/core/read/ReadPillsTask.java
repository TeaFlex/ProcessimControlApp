package be.heh.std.model.core.read;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import be.heh.std.app.R;
import be.heh.std.imported.simaticS7.S7;

public class ReadPillsTask extends ReadTask {

    private TextView reference;
    private TextView in_service;
    private TextView supply_asked;
    private TextView is_remote_controlled;
    private TextView nb_pills;
    private TextView nb_bottles;


    public ReadPillsTask(Activity current_activity, TextView reference, TextView in_service, TextView supply_asked,
                         TextView is_remote_controlled, TextView nb_pills, TextView nb_bottles,
                         TextView net_status, int datablock) {

        super(net_status, current_activity, datablock);
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
    protected ReadAutomateS7 getAutomateS7() {
        return new PillsAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {
        current_activity.runOnUiThread(() -> {
            if(values.length == 6) {
                nb_pills.setText(context.getString(R.string.nb_pills, values[0]));
                nb_bottles.setText(context.getString(R.string.nb_bottles, values[1]));
                supply_asked.setText(context.getString(R.string.supply_asked, values[2]));
                is_remote_controlled.setText(context.getString(R.string.remote_ctrl, onOrOff(values[3])));
                in_service.setText(context.getString(R.string.in_service, onOrOff(values[4])));
                reference.setText(context.getString(R.string.cpu_ref, values[5]));
            }
        });
    }

    @Override
    protected void downloadOnPostExecute() {
        current_activity.runOnUiThread(() -> {
            String none = "-";
            nb_pills.setText(none);
            nb_bottles.setText(none);
            supply_asked.setText(none);
            is_remote_controlled.setText(none);
            in_service.setText(none);
            reference.setText(none);
        });
    }

    private class PillsAutomateS7 extends ReadAutomateS7 {

        @Override
        protected void toRun(int numCPU) {
            int retInfo = comS7.ReadArea(S7.S7AreaDB,getDatablock(),9,2, datasPLC);

            retInfo = Math.max(byteReader(), retInfo);
            retInfo = Math.max(intReader(), retInfo);

            Log.i(String.format("READING PILLS ON [%s]", param[0]), String.format("code: %d", retInfo));
            
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
