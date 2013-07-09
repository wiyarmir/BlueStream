package pl.wiyarmir.bluestream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    BTReceiver mReceiver;
    private Button bDiscovery;
    private ListView lv;
    private SimpleAdapter adapter;
    private List<Map<String, String>> blueData = new ArrayList<Map<String, String>>();

    private static String UUID_1 = "73920C20-E838-11E2-91E2-0800200C9A66";
    private static String UUID_2 = "73920C21-E838-11E2-91E2-0800200C9A66";
    private static String UUID_3 = "73920C22-E838-11E2-91E2-0800200C9A66";
    private static String UUID_4 = "73920C23-E838-11E2-91E2-0800200C9A66";
    private static String UUID_5 = "73923330-E838-11E2-91E2-0800200C9A66";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG);
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
        }
        Log.d("W", "Hey!");
        mReceiver = new BTReceiver();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        bDiscovery = (Button) findViewById(R.id.button_discovery);
        bDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueData.clear();
                bDiscovery.setEnabled(false);
                mBluetoothAdapter.startDiscovery();
            }
        });

        lv = (ListView) findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, blueData, android.R.layout.simple_list_item_2,
                new String[]{"name", "address"},
                new int[]{android.R.id.text1, android.R.id.text2});
        lv.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {

                } else {
                    Toast.makeText(this, "Bluetooth is required to work!", Toast.LENGTH_LONG);
                    finish();
                }
                break;
            default:

        }
    }

    class BTReceiver extends BroadcastReceiver {

        public BTReceiver() {
            Log.d("W", "new BTReceiver");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d("W", "Received: Bluetooth Connected");
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)
                    || BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.d("W", "Received: Bluetooth Disconnected");
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                bDiscovery.setEnabled(true);
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                Log.d("W", device.getName() + "->" + device.getAddress());
                Map<String, String> m = new HashMap<String, String>();
                m.put("name", device.getName());
                m.put("address", device.getAddress());
                blueData.add(m);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
