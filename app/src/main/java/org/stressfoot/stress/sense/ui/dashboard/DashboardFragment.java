package org.stressfoot.stress.sense.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.chrisbanes.photoview.PhotoView;

import org.stressfoot.stress.sense.R;
import org.stressfoot.stress.sense.util.Constants;


public class DashboardFragment extends Fragment {
    private PhotoView imageView;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        imageView = root.findViewById(R.id.photo_view);
        textView = root.findViewById(R.id.text_notifications);

        getActivity().registerReceiver(imageReceiver, new IntentFilter(Constants.ACTION_UPDATE_GRAPH));
        handleImage();

        return root;
    }

    private final BroadcastReceiver imageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_UPDATE_GRAPH)) handleImage();
        }
    };

    private void handleImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMAGE_PATH, options);

            if (bitmap != null) {
                textView.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(imageReceiver);
        super.onDestroy();
    }
}