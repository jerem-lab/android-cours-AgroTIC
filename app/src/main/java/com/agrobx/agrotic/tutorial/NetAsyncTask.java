package com.agrobx.agrotic.tutorial;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jérémy on 15/12/2017.
 */

public class NetAsyncTask extends AsyncTask<Void, Void, ArrayList<PersonDataModel> > {

    private DownloadListener downloadListener;

    public interface DownloadListener {
        public void onLoadFinished(String result);
    }


    @Override
    protected ArrayList<PersonDataModel> doInBackground(Void... ignore) {
        ArrayList<PersonDataModel> result ;
        try {
//result=getHttpStreamAsString("http://www.google.fr");
            String jsontext = getHttpStreamAsString("https://jsonplaceholder.typicode.com/users");
            result = parseJSON(jsontext);
        } catch (Exception e) {
            Log.e("NET", "Err", e);
            result = null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<PersonDataModel> result)
    {
/*
if(result!=null)
{
Log.i("NET", result);
}
*/
        super.onPostExecute(result);
//downloadListener.onLoadFinished(result);
    }

    public boolean checkConnectivity(Context context) {
        boolean available = false;
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = (networkInfoWifi != null) && networkInfoWifi.isConnected();
        NetworkInfo networkInfoMobile =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = (networkInfoMobile != null) && networkInfoMobile.isConnected();
        Log.i("NET", "Wifi connected: " + isWifiConn);
        Log.i("NET", "Mobile connected: " + isMobileConn);
        if (isWifiConn == true) {
            available = true;
            connMgr.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
        } else if (isMobileConn == true) {
            available = true;
        }
        return available;
    }

    private String getHttpStreamAsString(String url) throws IOException {
        InputStreamReader streamReader = null;
        HttpURLConnection connection = null;
        try {
//1
            URL myUrl = new URL(url);
//2
            connection = (HttpURLConnection) myUrl.openConnection();
//3
            connection.setRequestMethod("GET");
            connection.setReadTimeout(1 * 1000);
            connection.setConnectTimeout(5 * 1000);
//4
            connection.connect();
            int responseCode = connection.getResponseCode();
//5
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);

                }

                return stringBuilder.toString();
            } else {
//6
                throw new IOException("HTTP error:" + responseCode);
            }
        } finally {
//7
            try {
                streamReader.close();
            } catch (Exception e) {
            }
            try {
                connection.disconnect();
            } catch (Exception e) {
            }
        }
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    private ArrayList<PersonDataModel> parseJSON(String result) throws JSONException {
        ArrayList<PersonDataModel> models = new ArrayList<PersonDataModel>();
        JSONArray jsonUsers = new JSONArray(result);
        for (int i = 0; i < jsonUsers.length(); i++) {
            JSONObject jsonUser = jsonUsers.getJSONObject(i);
            String firstName = jsonUser.getString("username");
            String lastName = jsonUser.getString("name");
            PersonDataModel model = new PersonDataModel(firstName, lastName, null);
            models.add(model);
        }
        Log.i("NET", models.toString());
        return models;
    }
}
