package com.example.shubham.acceleration_sg;

        import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int on = 0;
    String data = "";
    int count = 0;

    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            Toast.makeText(getApplicationContext(),"Accelerometer", Toast.LENGTH_LONG).show();
            System.exit(1);
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent == null) return;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z= sensorEvent.values[2];
        double w= Math.sqrt(x*x+y*y+z*z);

        TextView xtv = (TextView) findViewById(R.id.x_vl);
        TextView ytv = (TextView) findViewById(R.id.y_vl);
        TextView ztv = (TextView) findViewById(R.id.z_vl);

        xtv.setText(String.format(Locale.getDefault(), "%.9f", x));
        ytv.setText(String.format(Locale.getDefault(), "%.9f", y));
        ztv.setText(String.format(Locale.getDefault(), "%.9f", z));
        if (on==1) {
            data+=(String.format(Locale.getDefault(), "%.9f", x)+" "+String.format(Locale.getDefault(), "%.9f", y)+" "+String.format(Locale.getDefault(), "%.9f", z)+" "+System.currentTimeMillis()+"\n");
            count++;
            TextView saveM = (TextView) findViewById(R.id.NT_SV);
            saveM.setText(String.format(Locale.getDefault(), "No. of data points=%d",count));
            if (count>=100000) start_stop(findViewById(R.id.button));
        }

        GraphView graph= (GraphView) findViewById(R.id.graph);
        series new LineGraphSeries<>();

    }

    public void start_stop(View v) {
        if (on==1) {
            File myDir = new File(Environment.getExternalStorageDirectory(), "acc_data/");
            String filename = "acc_data_"+System.currentTimeMillis()+".txt";
            try {
                boolean res = myDir.mkdirs();
                File file = new File(myDir, filename);
                res = res ^ file.createNewFile();
                System.out.print(res);
                PrintWriter out = new PrintWriter(file);
                out.write(data);
                out.flush();
                out.close();
                Toast.makeText(getApplicationContext(), "file_saved_at"+myDir+"/"+filename, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            TextView saveM = (TextView) findViewById(R.id.NT_SV);
            saveM.setText("Not Saving");
        }
        else {
            count = 0;
            TextView saveM = (TextView) findViewById(R.id.NT_SV);
            saveM.setText(String.format("No. of data points=%d",count));
            Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
            data = "";
        }
        on = 1 - on;
        System.out.print(v);
    }





}


