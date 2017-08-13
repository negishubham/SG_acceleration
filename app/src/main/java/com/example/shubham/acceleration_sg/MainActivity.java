package com.example.shubham.acceleration_sg;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import mr.go.sgfilter.SGFilter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int on = 0;
    int count = 0;
    int offset=0;
    private SGFilter sgFilter;

    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series1;

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
        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        series.appendData(new DataPoint(0,0), true, 50);

        series1 = new LineGraphSeries<>();
        series1.appendData(new DataPoint(0,0), true, 50);

        graph.addSeries(series);

        graph.addSeries(series1);
        sgFilter = new SGFilter(5, 5);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent == null) return;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z= sensorEvent.values[2];
        count++;
        //double [] w[count]= Math.sqrt(x*x+y*y+z*z);
        double [] w;
        w[count]= Math.sqrt(x*x+y*y+z*z);

        TextView a = (TextView) findViewById(R.id.x_vl);
        TextView b = (TextView) findViewById(R.id.y_vl);
        TextView c = (TextView) findViewById(R.id.z_vl);
        TextView d = (TextView) findViewById(R.id.w_vl);

        a.setText(String.format(Locale.getDefault(), "%.9f", x));
        b.setText(String.format(Locale.getDefault(), "%.9f", y));
        c.setText(String.format(Locale.getDefault(), "%.9f", z));
        d.setText(String.format(Locale.getDefault(), "%.9f", w[count]));


        GraphView graph = (GraphView) findViewById(R.id.graph);
        series.appendData(new DataPoint(count,w[count]), true, 50);
        graph.addSeries(series);

        if (count%49==0)
        {
            int [] count1=new int[50];

            for (int i=0;i <50;++i)
            {
                count1[i]=i+offset;
            }
            double[] smooth = sgFilter.smooth(w, SGFilter.computeSGCoefficients(5, 5, 3));
            //GraphView graph = (GraphView) findViewById(R.id.graph);
            series1.appendData(new DataPoint(count1,smooth), true, 50);
            graph.addSeries(series1);
            offset=offset+50;
        }
    }







}


