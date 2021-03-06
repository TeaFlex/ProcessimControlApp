package be.heh.std.model.core.read;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ActivityPlcLiquidBinding;
import be.heh.std.imported.simaticS7.S7;

public class ReadLiquidTask extends ReadTask {

    private ActivityPlcLiquidBinding binding;
    private  HashMap<Integer, TextView> valves;

    public ReadLiquidTask(ActivityPlcLiquidBinding binding, Activity current_activity, int datablock) {
        super(binding.connectionTestLiquid, current_activity, datablock);
        this.binding = binding;
        this.valves = new HashMap<>();
        valves.put(1, binding.rValve1Liquid);
        valves.put(2, binding.rValve2Liquid);
        valves.put(3, binding.rValve3Liquid);
        valves.put(4, binding.rValve4Liquid);

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
    protected ReadAutomateS7 getAutomateS7() {
        return new LiquidAutomateS7();
    }

    @Override
    protected void downloadOnPreExecute(int... values) {
        current_activity.runOnUiThread(() -> {
            if(values.length == 11) {
                for(Map.Entry<Integer, TextView> entry : valves.entrySet()) {
                    Integer key = entry.getKey();
                    String current_valve = context.getString(R.string.valve_n, key);
                    String v_state = context.getString((values[key - 1] == 0)? R.string.closed : R.string.open);
                    valves.get(key).setText(String.format("%s : %s", current_valve, v_state));
                }
                binding.rManualLiquid.setText(context.getString(R.string.manual_deposit, values[4]));
                binding.rAutoLiquid.setText(context.getString(R.string.auto_deposit, values[5]));
                binding.rLvlLiquid.setText(context.getString(R.string.liquid_lvl, values[6]));
                binding.rPilotLiquid.setText(context.getString(R.string.pilot, values[7]));
                binding.rModeLiquid.setText(context.getString(R.string.mode, context.getString((values[8] == 0)? R.string.auto: R.string.manual)));
                binding.rIsRemoteLiquid.setText(context.getString(R.string.remote_ctrl, context.getString((values[9] == 0)? R.string.on: R.string.off)));
                binding.reference.setText(context.getString(R.string.cpu_ref, values[10]));
            }
        });
    }

    @Override
    protected void downloadOnPostExecute() {
        current_activity.runOnUiThread(() -> {
            String none = "-";
            for(Map.Entry<Integer, TextView> entry : valves.entrySet()) {
                Integer key = entry.getKey();
                valves.get(key).setText(none);
            }
            binding.rManualLiquid.setText(none);
            binding.rAutoLiquid.setText(none);
            binding.rLvlLiquid.setText(none);
            binding.rPilotLiquid.setText(none);
            binding.rModeLiquid.setText(none);
            binding.rIsRemoteLiquid.setText(none);
            binding.reference.setText(none);
        });
    }

    private class LiquidAutomateS7 extends ReadAutomateS7 {

        @Override
        protected void toRun(int numCPU) {
            int retInfo = comS7.ReadArea(S7.S7AreaDB, getDatablock(),
                    9,2,datasPLC);

            retInfo = Math.max(byteReader(), retInfo);
            retInfo = Math.max(intReader(), retInfo);

            Log.i(String.format("READING LIQUID ON [%s]", param[0]), String.format("code: %d", retInfo));

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
                automatic_deposit_data = S7.GetWordAt(dbw.get(22), 0);
                manual_deposit_data = S7.GetWordAt(dbw.get(20),0);
                pilot_data = S7.GetWordAt(dbw.get(18),0);

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
