package com.example.metal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView updateDate = findViewById(R.id.updateDate);

        TextView goldValue = findViewById(R.id.goldValue);
        TextView silverVale = findViewById(R.id.silverValue);
        TextView platinumValue = findViewById(R.id.platinumValue);
        TextView palladiumValue = findViewById(R.id.palladiumValue);

        Observable<String> getData = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection urlConnection;

                try {
                    URL url = new URL("https://api.metals.live/v1/spot/");
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    String result;

                    if (scanner.hasNext()){
                        result = scanner.next();
                        return result;
                    }
                    else {
                        return null;
                    }
                }
                catch (Exception e){
                    return null;
                }
            }
        });

        getData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (s != null){
                Log.d("AAA", s);
                JSONArray data = new JSONArray(s);

                goldValue.setText(data.getJSONObject(0).getString("gold") + " $");
                silverVale.setText(data.getJSONObject(1).getString("silver") + " $");
                platinumValue.setText(data.getJSONObject(2).getString("platinum") + " $");
                palladiumValue.setText(data.getJSONObject(3).getString("palladium") + " $");

                String timestamp = data.getJSONObject(4).getString("timestamp");

                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                calendar.setTimeInMillis(Long.parseLong(timestamp));

                Integer day = calendar.get(Calendar.DAY_OF_MONTH);
                Integer month = (calendar.get(Calendar.MONTH) + 1);
                Integer year = calendar.get(Calendar.YEAR);
                Integer hours = calendar.get(Calendar.HOUR_OF_DAY);
                Integer minutes = calendar.get(Calendar.MINUTE);

                String dayString = day + "";
                String monthString = month + "";
                String hourString = hours + "";
                String minuteString = minutes + "";


                if (day < 10) dayString = "0" + day;
                if (month < 10) monthString = "0" + month;
                if (hours < 10) hourString = "0" + hours;
                if (minutes < 10) minuteString = "0" + minutes;

                updateDate.setText("Обновлено " + dayString + "." + monthString + "." + year + " в " + hourString + ":" + minuteString);
            }
            else {
                updateDate.setText("Данные не загрузились");
            }
        });
    }


}
