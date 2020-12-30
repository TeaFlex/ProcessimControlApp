package be.heh.std.model.core.read;

import android.content.res.Resources;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import be.heh.std.app.R;
import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadLiquidTask extends ReadTask {

    private HashMap<Integer, TextView> valves;
    private TextView liquid_lvl;
    private TextView reference;
    private TextView mode;
    private TextView pilot;
    private TextView auto_deposit;
    private TextView manual_deposit;
    private TextView is_remote_controlled;

    public ReadLiquidTask(HashMap<Integer, TextView>valves, TextView liquid_lvl, TextView reference,
                          TextView mode, TextView pilot, TextView auto_deposit,
                          TextView manual_deposit, TextView is_remote_controlled,
                          TextView net_status, int datablock) {
        super(net_status, datablock);
        this.valves = valves;
        this.liquid_lvl = liquid_lvl;
        this.reference = reference;
        this.mode = mode;
        this.pilot = pilot;
        this.auto_deposit = auto_deposit;
        this.manual_deposit = manual_deposit;
        this.is_remote_controlled = is_remote_controlled;

        //valves, mode and remote
        dbb.put(0, new byte[16]);
        //liquid
        dbw.put(16, new byte[16]);
        //auto
        dbw.put(18, new byte[16]);
        //manual
        dbw.put(20, new byte[16]);
        //pilot
        dbw.put(22, new byte[16]);
    }

    @Override
    protected AutomateS7 getAutomateS7() {
        return new LiquidAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {
        if(values.length == 11) {
            Resources r = Resources.getSystem();
            for(Map.Entry<Integer, TextView> entry : valves.entrySet()) {
                Integer key = entry.getKey();
                valves.get(key).setText(String.valueOf(values[key-1]));
            }
            manual_deposit.setText(String.valueOf(values[4]));
            auto_deposit.setText(String.valueOf(values[5]));
            liquid_lvl.setText(String.valueOf(values[6]));
            pilot.setText(String.valueOf(values[7]));
            mode.setText((values[8] == 0)? R.string.off : R.string.on);
            is_remote_controlled.setText((values[9] == 0)? R.string.off : R.string.on);
            reference.setText(String.valueOf(values[10]));
        }
    }

    @Override
    protected void downloadOnPostExecute() {
        Resources r = Resources.getSystem();
        for(Map.Entry<Integer, TextView> entry : valves.entrySet()) {
            Integer key = entry.getKey();
            valves.get(key).setText("-");
        }
        manual_deposit.setText("-");
        auto_deposit.setText("-");
        liquid_lvl.setText("-");
        pilot.setText("-");
        mode.setText("-");
        is_remote_controlled.setText("-");
    }

    private class LiquidAutomateS7 extends AutomateS7 {

        @Override
        protected void toRun(int numCPU) {
            int retInfo = comS7.ReadArea(S7.S7AreaDB, getDatablock(),
                    9,2,datasPLC);

            retInfo = Math.max(byteReader(), retInfo);
            retInfo = Math.max(intReader(), retInfo);

            Log.i("retInfo", String.valueOf(retInfo));

            int init_data = 0;

            int[] valves_data = {0,0,0,0};
            int manual_deposit_data = 0;
            int automatic_deposit_data = 0;
            int liquid_level_data = 0;
            int pilot_data = 0;
            int mode_data = 0;
            int remote_data = 0;


            if (retInfo == 0) {
                init_data = S7.GetWordAt(datasPLC, 0);

                //Beginning of working zone

                //Decimal values
                liquid_level_data = S7.GetWordAt(dbw.get(16),0);
                automatic_deposit_data = S7.GetWordAt(dbw.get(18), 0);
                manual_deposit_data = S7.GetWordAt(dbw.get(20),0);
                pilot_data = S7.GetWordAt(dbw.get(22),0);

                //Binary values
                for (int i = 0; i < valves_data.length; i++)
                    valves_data[i] = S7.GetBitAt(dbb.get(0), 0, i+1)? 1 : 0;
                mode_data = S7.GetBitAt(dbb.get(0), 0,5)? 0 : 1;
                remote_data = S7.GetBitAt(dbb.get(0), 0, 6)? 0 : 1;

                //End of working zone

                sendPreExecuteMessage(valves_data[0], valves_data[1], valves_data[2],
                        valves_data[3], manual_deposit_data, automatic_deposit_data,
                        liquid_level_data, pilot_data, mode_data, remote_data, numCPU);
            }
        }
    }
}
