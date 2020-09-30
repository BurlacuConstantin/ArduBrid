package com.example.costi.ardubrid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.support.constraint.Constraints.TAG;

public class ChooseIP extends Activity
{
    private WifiManager wifiManager;
    private Button checkip;
    private ListView showip;

    private ScanNetwork scanNetwork;

    ArrayList<PaternModel> dataPatern;
    private CustomListAdapter adapter;

    private ProgressDialog dialog;

    private TextView localIP;
    private TextView externIP;
    private TextView SSID;
    private TextView DhcpServer;
    private TextView WifiSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ip);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
        checkip = (Button) findViewById(R.id.create_connection);
        showip  = (ListView) findViewById(R.id.list_recent_connections);

        localIP = (TextView) findViewById(R.id.device_ip_show);
        externIP = (TextView) findViewById(R.id.external_ip_show);
        SSID = (TextView) findViewById(R.id.this_ssid_show);
        DhcpServer = (TextView) findViewById(R.id.dhcp_show);
        WifiSpeed = (TextView) findViewById(R.id.wifi_speed_show);

        if(wifiManager.isWifiEnabled())
        {
            WifiInfo info = wifiManager.getConnectionInfo();

            if(info.getNetworkId() == -1)
            {
                Toast.makeText(getApplicationContext(), "You are not connected to any WiFi network, please connect to your ArduBrid system network !", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        dataPatern = new ArrayList<>();

        adapter = new CustomListAdapter(ChooseIP.this, dataPatern);

        showip.setAdapter(adapter);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid_name = wifiInfo.getSSID();
        ssid_name = ssid_name.replace("\"", "");
        localIP.setText(Formatter.formatIpAddress(wifiInfo.getIpAddress()));
        externIP.setText(getExternalIP().trim());
        SSID.setText(ssid_name);
        DhcpServer.setText(Formatter.formatIpAddress(wifiManager.getDhcpInfo().gateway));
        WifiSpeed.setText(String.valueOf(wifiInfo.getLinkSpeed()));


        new ScanNetwork().execute();

        checkip.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new ScanNetwork().execute();
            }
        });

        showip.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                PaternModel choose = dataPatern.get(position);

                if(choose.getSecondSubtitle().contains("Espressif"))
                {
                    Intent intent = new Intent(ChooseIP.this, ArduBridControl.class);
                    intent.putExtra("Title", choose.getTitle());
                    intent.putExtra("Subtitle1", choose.getFirstSubtitle());
                    intent.putExtra("Subtitle2", choose.getSecondSubtitle());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Not supported, this is not ArduBrid System !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class ProgressManager
    {
        public final String hostname;
        public final String hostip;
        public final String hostCanonicalName;
        public final int DisplayIcon;

        public ProgressManager(String hostname, String hostip, String hostCanonicalName, int DisplayIcon)
        {
            this.hostname = hostname;
            this.hostip = hostip;
            this.hostCanonicalName = hostCanonicalName;
            this.DisplayIcon = DisplayIcon;
        }
    }

    private class ScanNetwork extends AsyncTask<Void, ProgressManager, Void>
    {
        //ProgressDialog dialog = new ProgressDialog(ChooseIP.this);

        private ProgressManager manager;

        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(ChooseIP.this);
            dialog.setMessage("Scanning this network..");
            //dialog.setTitle();
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();

            dataPatern.clear();

            adapter.notifyDataSetInvalidated();

        }

        @Override
        protected Void doInBackground(Void... params)
        {
            WifiInfo info = wifiManager.getConnectionInfo();
            String client_ipAddress = Formatter.formatIpAddress(info.getIpAddress());

            String subnet = client_ipAddress.substring(0, client_ipAddress.lastIndexOf(".") + 1);
            String host;
            ArrayList<String> database = new ArrayList<>();
            String vendor;
            String gateway = Formatter.formatIpAddress(wifiManager.getDhcpInfo().gateway);

            database = GetMacVendorList("https://gitlab.com/wireshark/wireshark/raw/master/manuf");

            int percentProgress;

            for(short i = 0; i < 255; i++)
            {
                if(isCancelled())
                {
                    dialog.dismiss();
                    break;
                }

                host = subnet + String.valueOf(i);

                try
                {

                    InetAddress address = InetAddress.getByName(host);

                    if (address.toString().equals("/" + client_ipAddress))
                    {
                        if (address.isReachable(400))
                        {
                            vendor = returnVendorFromList(database, returnMacPrefix(getMacAddr()));

                            manager = new ProgressManager(address.getHostName(), getMacAddr(), vendor, R.drawable.android02);

                            publishProgress(manager);
                        }
                    }
                    else if(address.toString().equals("/" + gateway))
                    {
                        if (address.isReachable(400))
                        {
                            vendor = returnVendorFromList(database, returnMacPrefix(getMacFromArpCache(address.getHostAddress())));

                            manager = new ProgressManager(address.getHostName(), getMacFromArpCache(address.getHostAddress()), vendor, R.drawable.router);

                            publishProgress(manager);
                        }
                    }
                    else
                    {
                        if(address.isReachable(400))
                        {
                            vendor = returnVendorFromList(database, returnMacPrefix(getMacFromArpCache(address.getHostAddress())));

                            manager = new ProgressManager(address.getHostName(), getMacFromArpCache(address.getHostAddress()), vendor, vendor.contains("Espressif") ? R.drawable.mcu : R.drawable.unknown);

                            publishProgress(manager);
                        }
                    }

                    percentProgress = (int)((float) i / 254 * dialog.getMax());
                    dialog.setProgress(percentProgress);
                }
                catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }



            return null;
        }

        @Override
        protected void onProgressUpdate(ProgressManager... values)
        {
            dataPatern.add(new PaternModel(manager.hostname, manager.hostip, manager.hostCanonicalName, manager.DisplayIcon));
            adapter.notifyDataSetInvalidated();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            dialog.dismiss();
        }

    }

    public static String getMacAddr()
    {
        try
        {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacFromArpCache(String ip) throws Exception
    {
        if (ip == null)
            throw new Exception("ip is null");

        BufferedReader buffreader = null;
        try
        {
            buffreader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = buffreader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0]))
                {
                    // Basic sanity check
                    String macaddr = splitted[3];
                    if (macaddr.matches("..:..:..:..:..:.."))
                    {
                        return macaddr;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                buffreader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static ArrayList<String> GetMacVendorList(String urlLink)
    {
        ArrayList<String> lines = new ArrayList<String>();

        try
        {
            URL url = new URL(urlLink);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith("#"))
                    continue;

                line = line.replaceAll("\t", " ").trim();
                //line.replaceAll("\\s", "");
                lines.add(line);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return lines;
    }

    private static String returnMacPrefix(String macAddress) throws Exception
    {
        //40:b8:9a:78:e9:5a
        if(macAddress == null)
            throw new Exception("macAddress is null");

        String[] parts = macAddress.split(":", 4);
        String result = "";

        for(byte index = 0; index < (parts.length - 1); index++)
        {
            result += parts[index];

            if (index < (parts.length - 1) - 1) result += ":";
        }

        return result;
    }

    private static String returnVendorFromList(ArrayList<String> list, String macID) throws Exception
    {
        if(macID == null || list == null)
            throw new Exception("macID or List is null");

        String[] parts;
        String inter = "";
        String result = "";

        for(int i = 0; i < list.size(); i++)
        {
            if(list.get(i).startsWith("#") || list.get(i).isEmpty())
                            continue;

            if(list.get(i) != null)
            {
                inter = list.get(i);

                if(inter.contains(macID.toUpperCase()))
                {
                    inter = inter.substring(macID.length());

                    parts = inter.split(" ");

                    for(int z = 0; z < parts.length; z++)
                    {
                        if(parts[z] != null)
                        {
                           if(z > 1)
                           {
                                result += parts[z] + " ";
                          }
                        }
                    }

                    break;
                }

            }
        }

        return result.isEmpty() ? null : result;
    }

    private static String getExternalIP()
    {
        String value = null;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> result = es.submit(new Callable<String>()
        {
            public String call() throws Exception
            {
                try
                {
                    URL url = new URL("http://whatismyip.akamai.com/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        urlConnection.disconnect();
                        return total.toString();
                    }finally {
                        urlConnection.disconnect();
                    }
                }
                catch (IOException e)
                {
                    Log.e("Public IP: ",e.getMessage());
                }
                return null;
            }
        });
        try
        {
            value = result.get();
        }
        catch (Exception e)
        {
            // failed
        }
        es.shutdown();
        return value;
    }




    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dialog.dismiss();

    }

}
