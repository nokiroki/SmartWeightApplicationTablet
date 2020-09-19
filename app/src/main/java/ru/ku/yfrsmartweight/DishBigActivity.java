package ru.ku.yfrsmartweight;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import ru.ku.yfrsmartweight.ServerConnection.JsonEncoderDecoder;

public class DishBigActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "AppLogs";

    private String dishName;
    private int mass;
    private int idImage;

    private Timer timer;
    private Intent intent;

    private TextView massText;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service bind from DishPanelActivity");
            mWebSocket = IWebSocketInterface.Stub.asInterface(iBinder);
            try {
                mWebSocket.bindListener(new MWebSocketListener());
                mWebSocket.sendMSG(JsonEncoderDecoder.queryActivateDeactivateReader(true).toString());
            } catch (RemoteException | JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "Server connection error!");
        }
    };

    private IWebSocketInterface mWebSocket;

    private class MWebSocketListener extends IWebSocketListener.Stub {

        @Override
        public void omMessageGet(String message) throws RemoteException {
            try {
                final JSONObject json = new JSONObject(message);
                String query = json.getString("query");
                if (query.equals("ActivateReader") && MainActivity.isInitToRaspberry) {
                    DishBigActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = findViewById(R.id.panel_big_info_text);
                            text.setText(R.string.card_got);
                        }
                    });
                } else if (query.equals("GetReader") && MainActivity.isInitToRaspberry) {
                    timer.cancel();
                    if (!json.getJSONObject("data").getBoolean("isOK")) {
                        missingNFCInformation();
                    } else {
                        startFinalActivity(json);
                    }
                } else if (query.equals("CurrentWeight") && MainActivity.isInitToRaspberry) {
                    changedWeight(json.getJSONObject("data").getInt("weight"));
                } else {
                    if (!MainActivity.isInitToRaspberry) {
                        Log.e(TAG, "Tablet unknown!");
                    } else {
                        Log.e(TAG, "Unknown query!");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_big);
        this.setFinishOnTouchOutside(false);

        getIntentExtra();

        bindService(new Intent(MainActivity.INTENT_ACTION).setPackage(getPackageName()), sc, 0);



        TextView dish = findViewById(R.id.panel_big_description);
        massText = findViewById(R.id.panel_big_mass_compare);
        ImageView photo = findViewById(R.id.panel_big_image);

        dish.setText(dishName);
        massText.setText(String.format(massText.getText().toString(), mass, MainActivity.CURR_MASS));
        photo.setImageResource(idImage);

        Button finishButton = findViewById(R.id.panel_big_cancel_button);
        finishButton.setOnClickListener(this);

        timer = new Timer();
        Log.d(TAG, "Timer on 10s started");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 15000);
    }


    // Получаем параметры вызываемой панели
    private void getIntentExtra() {
        Intent intent = getIntent();
        dishName = intent.getStringExtra("dishName");
        mass = intent.getIntExtra("mass", 0);
        idImage = intent.getIntExtra("idImage", 0);
        Log.d(TAG, dishName + " " + mass + " " + idImage);
    }

    private void missingNFCInformation () {
        //TODO диалоговое окно с информацией
        Log.d(TAG, "Unknown NFC");
        finish();
    }

    private void startFinalActivity (JSONObject json) throws JSONException {
        JSONObject json_ = json.getJSONObject("data");
        // TODO загрузка картинки с сервера
        intent = new Intent(this, FinalActivity.class)
                .putExtra("dishName", dishName)
                .putExtra("mass", mass)
                .putExtra("currMass", MainActivity.CURR_MASS)
                .putExtra("fullName", json_.getString("fullName"))
                .putExtra("department_name", json_.getString("department_name"))
                .putExtra("photo_id", json_.getString("photo_id"));
        startActivity(intent);

    }

    private synchronized void changedWeight(int weight) {
        MainActivity.CURR_MASS = weight;
        massText.setText(String.format(massText.getText().toString(), mass, MainActivity.CURR_MASS));

    }

    private void sendDeactivateJson() throws JSONException, RemoteException {
        mWebSocket.sendMSG(JsonEncoderDecoder.queryActivateDeactivateReader(false).toString());
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        unbindService(sc);
        try {
            sendDeactivateJson();
        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }
}
