package com.example.costi.ardubrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


public class ArduBridControl extends Activity
{
    private final int REQUEST_CODE_SPEECH = 404;
    private TextView ip;
    private TextView mac;
    private TextView vendor;

    private Button MicBtn;
    private Button r1_off, r1_on, r2_off, r2_on, r3_off, r3_on, r4_off, r4_on;

    private Bundle bundle;

    private URL urlx;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        bundle = getIntent().getExtras();

        ip = (TextView) findViewById(R.id.esp_ip);
        mac = (TextView) findViewById(R.id.esp_mac);
        vendor = (TextView) findViewById(R.id.esp_vendor);

        r1_off = (Button) findViewById(R.id.relay1_off);
        r2_off = (Button) findViewById(R.id.relay2_off);
        r3_off = (Button) findViewById(R.id.relay3_off);
        r4_off = (Button) findViewById(R.id.relay4_off);

        r1_on = (Button) findViewById(R.id.relay1_on);
        r2_on = (Button) findViewById(R.id.relay2_on);
        r3_on = (Button) findViewById(R.id.relay3_on);
        r4_on = (Button) findViewById(R.id.relay4_on);

       if(bundle != null)
       {
           ip.setText(String.valueOf(bundle.get("Title")));
           mac.setText(String.valueOf(bundle.get("Subtitle1")));
           vendor.setText(String.valueOf(bundle.get("Subtitle2")));
       }

       MicBtn = (Button) findViewById(R.id.voice_recognition);

       MicBtn.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View v)
           {
               CreateSpeachText();
           }
       });

        r1_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_off");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r2_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_off");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r3_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_off");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r4_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_off");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r1_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_on");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r2_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_on");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r3_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_on");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });

        r4_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    urlx = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_on");
                    new ExecuteESP_URL().execute(urlx);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        });


    }

    private void CreateSpeachText()
    {
        // speech dialog intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say an command action");

        try
        {
            startActivityForResult(intent, REQUEST_CODE_SPEECH);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH:
            {
                if(resultCode == RESULT_OK)
                {
                    if(data != null)
                    {
                        ArrayList<String> dataText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String result = dataText.get(0);

                        bundle = getIntent().getExtras();

                        URL url;

                        switch (result)
                        {
                            case "pornește releu 1":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releul 1":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releu 1":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releul 1":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r1_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releu 2":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releul 2":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releu 2":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releul 2":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r2_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releu 3":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releul 3":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releu 3":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releul 3":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r3_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releu 4":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "pornește releul 4":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_on");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releu 4":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case "oprește releul 4":
                            {
                                try
                                {
                                    url = new URL("http", String.valueOf(bundle.get("Title")), "/vc_r4_off");
                                    new ExecuteESP_URL().execute(url);
                                }
                                catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            default:
                            {
                                Toast.makeText(getApplicationContext(), "Command action invalid !", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "There is no input command", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Command action canceled", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private class ExecuteESP_URL extends AsyncTask<URL, Integer, Void>
    {
        Integer result = -1;

        Integer httpRequestResult;

        @Override
        protected Void doInBackground(URL... url)
        {
            try
            {
                HttpURLConnection connection = (HttpURLConnection) url[0].openConnection();
                connection.setConnectTimeout(60000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    result = Integer.valueOf(line);
                }

                publishProgress(result);
                connection.disconnect();

            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer...values)
        {
            httpRequestResult = values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(httpRequestResult == 1)
            {
                Toast.makeText(ArduBridControl.this,"Command action executed succesfuly", Toast.LENGTH_SHORT).show();
            }
            else if(httpRequestResult == 0)
            {
                Toast.makeText(ArduBridControl.this,"Relay is already in that state", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
