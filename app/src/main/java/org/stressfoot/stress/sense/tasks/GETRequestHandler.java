package org.stressfoot.stress.sense.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.stressfoot.stress.sense.util.Constants;
import org.stressfoot.stress.sense.util.StressSensePreferences;

public class GETRequestHandler extends AsyncTask<Void, Void, Void> {
    private final String TAG = GETRequestHandler.this.getClass().getSimpleName();
    private Context context;
    private String request;
    private RequestQueue requestQueue;
    private StressSensePreferences stressSensePreferences;

    public GETRequestHandler(Context context, String request, RequestQueue requestQueue) {
        this.context = context;
        this.request = request;
        this.requestQueue = requestQueue;
        stressSensePreferences = StressSensePreferences.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        String requestUrl = "http://" + stressSensePreferences.getIpAddress() + "/hello?values=" + request;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl, response -> {
            Log.i(TAG, "response: " + response);
            notifyResults(Constants.ACTION_RECEIVED_RESPONSE, response);

            StringBuilder stringBuilder = new StringBuilder().append(new Date().getTime()).append(',').append(request).append(',').append(response);
            writeToFile(stringBuilder.toString());

        }, error -> Log.w(TAG, error.getMessage(), error)
        );

        requestQueue.add(stringRequest);

        return null;
    }

    private void writeToFile(String csvLine) {
        String rootDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        File baseDir = new File(rootDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayDataPath = rootDir + "/" + sdf.format(new Date()) + ".csv";
        File csvFile = new File(todayDataPath);
        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error while writing csv.", e);
            }
        }

        try (FileWriter fw = new FileWriter(todayDataPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(csvLine);
        } catch (IOException e) {
            Log.e(TAG, "Error while writing csv.", e);
        }
    }

    private void notifyResults(final String action, String response) {
        final Intent intent = new Intent(action);
        intent.putExtra(Intent.EXTRA_TEXT, response);
        context.sendBroadcast(intent);

    }
}
