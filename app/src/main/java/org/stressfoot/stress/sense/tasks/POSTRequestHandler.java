package org.stressfoot.stress.sense.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.stressfoot.stress.sense.util.Constants;
import org.stressfoot.stress.sense.util.StressSensePreferences;

import java.io.File;
import java.io.FileOutputStream;

public class POSTRequestHandler extends AsyncTask<Void, Void, Void> {
    private final String TAG = POSTRequestHandler.this.getClass().getSimpleName();
    private Context context;
    private String times;
    private String sitValues;
    private String stressValues;
    private String events;
    private boolean backupEnabled;
    private RequestQueue requestQueue;
    private StressSensePreferences stressSensePreferences;

    public POSTRequestHandler(Context context, String times, String sitValues, String stressValues, String events, boolean backupEnabled, RequestQueue requestQueue) {
        this.context = context;
        this.times = times;
        this.sitValues = sitValues;
        this.stressValues = stressValues;
        this.events = events;
        this.backupEnabled = backupEnabled;
        this.requestQueue = requestQueue;
        stressSensePreferences = StressSensePreferences.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        String paramA = "t=" + times;
        String paramB = "&s=" + stressValues + ",0";
        String paramC = "&p=" + sitValues + ",0";
        String paramD = "&a=" + events + ",stop";

        String requestUrl = "http://" + stressSensePreferences.getIpAddress() + "/newpredict?" + paramA + paramB + paramC + paramD;
        Log.i(TAG, "HTTP POST: " + requestUrl);

        String tmpImg = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/StressShoe_temp.png";

        ImageRequest postRequest = new ImageRequest(requestUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        File file = new File(tmpImg);
                        if (backupEnabled) saveImage(response);

                        try (FileOutputStream out = new FileOutputStream(file)) {
                            response.compress(Bitmap.CompressFormat.PNG, 100, out);
                            context.sendBroadcast(new Intent(Constants.ACTION_UPDATE_GRAPH));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, error.getMessage(), error);
                    }
                }
        );

        requestQueue.add(postRequest);

        return null;
    }

    private void saveImage(Bitmap response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String rootDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File baseDir = new File(rootDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        String todayImagePath = rootDir + "/" + sdf.format(new Date()) + ".png";
        File file = new File(todayImagePath);

        try (FileOutputStream out = new FileOutputStream(file)) {
            response.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
