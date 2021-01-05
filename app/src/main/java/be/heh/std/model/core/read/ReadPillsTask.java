package be.heh.std.model.core.read;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ActivityPlcPillsBinding;
import be.heh.std.imported.simaticS7.S7;

public class ReadPillsTask extends ReadTask {

    private ActivityPlcPillsBinding binding;

    public ReadPillsTask(ActivityPlcPillsBinding binding, Activity current_activity, int datablock) {
        super(binding.connectionTestPills, current_activity, datablock);
        this.binding = binding;

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
                binding.rNbPills.setText(context.getString(R.string.nb_pills, values[0]));
                binding.rNbBottles.setText(context.getString(R.string.nb_bottles, values[1]));
                binding.rSupplyPills.setText(context.getString(R.string.supply_asked, values[2]));
                binding.isRemotePills.setText(context.getString(R.string.remote_ctrl, onOrOff(values[3])));
                binding.inServicePills.setText(context.getString(R.string.in_service, onOrOff(values[4])));
                binding.referencePills.setText(context.getString(R.string.cpu_ref, values[5]));
            }
        });
    }

    @Override
    protected void downloadOnPostExecute() {
        current_activity.runOnUiThread(() -> {
            String none = "-";
            binding.rNbPills.setText(none);
            binding.rNbBottles.setText(none);
            binding.rSupplyPills.setText(none);
            binding.isRemotePills.setText(none);
            binding.inServicePills.setText(none);
            binding.referencePills.setText(none);
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
