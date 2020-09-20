package org.stressfoot.stress.sense.ui.home;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.stressfoot.stress.sense.R;
import org.stressfoot.stress.sense.ui.IftttHelperActivity;
import org.stressfoot.stress.sense.util.ConsolePreferences;
import org.stressfoot.stress.sense.util.Constants;
import org.stressfoot.stress.sense.util.StressSensePreferences;

public class HomeFragment extends Fragment {
    private ToggleButton sessionToggle;
    private ToggleButton stressToggle;
    private ToggleButton postureToggle;
    private Button timeButton;
    private Button sitTimeButton;

    private Switch notificationsSwitch;
    private Switch  musicSwitch;
    private Switch iftttSwitch;
    private Switch reminderSwitch;

    private Switch sitNotificationsSwitch;
    private Switch  sitMusicSwitch;
    private Switch sitIftttSwitch;
    private Switch sitReminderSwitch;

    private Spinner stressParams;
    private Spinner stressLevels;
    private Spinner postureParams;
    private Spinner postureLevels;

    private ConsolePreferences consolePreferences;
    private StressSensePreferences stressSensePreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        notificationsSwitch = root.findViewById(R.id.notif_switch);
        musicSwitch = root.findViewById(R.id.mus_switch);
        iftttSwitch = root.findViewById(R.id.iftt_switch);
        reminderSwitch = root.findViewById(R.id.reminder_switch);

        sitNotificationsSwitch = root.findViewById(R.id.notif_sit_switch);
        sitMusicSwitch = root.findViewById(R.id.mus_sit_switch);
        sitIftttSwitch = root.findViewById(R.id.iftt_sit_switch);
        sitReminderSwitch = root.findViewById(R.id.reminder_sit_switch);

        sessionToggle = root.findViewById(R.id.toggleButton);
        stressToggle = root.findViewById(R.id.stressToggle);
        postureToggle = root.findViewById(R.id.postureToggle);
        timeButton = root.findViewById(R.id.timeButton);
        sitTimeButton = root.findViewById(R.id.sitTimeButton);

        consolePreferences = ConsolePreferences.getInstance(getContext());
        stressSensePreferences = StressSensePreferences.getInstance(getContext());

        // 0: Stress Level, 1: Posture
        stressParams = root.findViewById(R.id.sSpinner);
        makeParamsDropDown(stressParams, 0);

        stressLevels = root.findViewById(R.id.sSpinnerVal);
        makeLevelsDropDown(stressLevels, 0);

        postureParams = root.findViewById(R.id.pSpinner);
        makeParamsDropDown(postureParams, 1);

        postureLevels = root.findViewById(R.id.pSpinnerVal);
        makeLevelsDropDown(postureLevels, 1);

        getActivity().registerReceiver(httpReceiver, getIntentFilters());

        notificationsSwitch.setChecked(consolePreferences.getNotificationState());
        musicSwitch.setChecked(consolePreferences.getMusicState());
        iftttSwitch.setChecked(consolePreferences.getIftttState());
        reminderSwitch.setChecked(consolePreferences.getReminderState());

        sitNotificationsSwitch.setChecked(consolePreferences.getSitNotificationState());
        sitMusicSwitch.setChecked(consolePreferences.getSitMusicState());
        sitIftttSwitch.setChecked(consolePreferences.getSitIftttState());
        sitReminderSwitch.setChecked(consolePreferences.getSitReminderState());

        sessionToggle.setEnabled(consolePreferences.getButtonState());
        sessionToggle.setChecked(consolePreferences.getSessionState());
        stressToggle.setEnabled(consolePreferences.getButtonState());
        stressToggle.setChecked(consolePreferences.getStressState());
        postureToggle.setEnabled(consolePreferences.getButtonState());
        postureToggle.setChecked(consolePreferences.getPostureState());

        setButtonListeners();

        return root;
    }

    private final BroadcastReceiver httpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.ACTION_RECEIVED_RESPONSE)){
                String response = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            else if (action.equals(Constants.ACTION_BLE_CONNECTED)){
                sessionToggle.setEnabled(true);
                stressToggle.setEnabled(true);
                postureToggle.setEnabled(true);
                consolePreferences.setButtonState(true);
            }
            else if (action.equals(Constants.ACTION_BLE_DISCONNECTED)){
                sessionToggle.setEnabled(false);
                stressToggle.setEnabled(false);
                postureToggle.setEnabled(false);

                if (stressToggle.isChecked()) stressToggle.performClick();
                if (postureToggle.isChecked()) postureToggle.performClick();

                sessionToggle.setChecked(false);
                consolePreferences.setButtonState(false);
                consolePreferences.setSessionState(false);
            }
        }
    };

    private void setButtonListeners(){
        notificationsSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (notificationsSwitch.isChecked()){
                    consolePreferences.setNotificationState(true);
                    checkNotificationPermissions(0);
                }
                else {
                    consolePreferences.setNotificationState(false);
                }
            }
        });

        sitNotificationsSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sitNotificationsSwitch.isChecked()){
                    consolePreferences.setSitNotificationState(true);
                    checkNotificationPermissions(1);
                }
                else {
                    consolePreferences.setSitNotificationState(false);
                }
            }
        });

        musicSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (musicSwitch.isChecked()){
                    consolePreferences.setMusicState(true);
                    checkSpotifyAvailability(0);
                }
                else {
                    consolePreferences.setMusicState(false);
                }
            }
        });

        sitMusicSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sitMusicSwitch.isChecked()){
                    consolePreferences.setSitMusicState(true);
                    checkSpotifyAvailability(1);
                }
                else {
                    consolePreferences.setSitMusicState(false);
                }
            }
        });

        iftttSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iftttSwitch.isChecked()){
                    consolePreferences.setIftttState(true);
                    if (checkIftttAvailability(0) && stressSensePreferences.getIftttHelp()) showIftttDialog();
                }
                else {
                    consolePreferences.setIftttState(false);
                }
            }
        });

        sitIftttSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sitIftttSwitch.isChecked()){
                    consolePreferences.setSitIftttState(true);
                    if (checkIftttAvailability(1) && stressSensePreferences.getIftttHelp()) showIftttDialog();
                }
                else {
                    consolePreferences.setSitIftttState(false);
                }
            }
        });

        reminderSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (reminderSwitch.isChecked()){
                    consolePreferences.setReminderState(true);
                    showMessageDialog(0);
                }
                else {
                    consolePreferences.setReminderState(false);
                }
            }
        });

        sitReminderSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sitReminderSwitch.isChecked()){
                    consolePreferences.setSitReminderState(true);
                    showMessageDialog(1);
                }
                else {
                    consolePreferences.setSitReminderState(false);
                }
            }
        });

        stressToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (stressToggle.isChecked()) {
                    stressToggle.setChecked(true);
                    consolePreferences.setStressState(true);
                    stressLevels.setEnabled(true);
                    stressParams.setEnabled(true);
                } else {
                    stressToggle.setChecked(false);
                    consolePreferences.setStressState(false);
                    stressLevels.setEnabled(false);
                    stressParams.setEnabled(false);
                }
            }
        });

        postureToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (postureToggle.isChecked()) {
                    postureToggle.setChecked(true);
                    consolePreferences.setPostureState(true);
                    postureLevels.setEnabled(true);
                    postureParams.setEnabled(true);
                } else {
                    postureToggle.setChecked(false);
                    consolePreferences.setPostureState(false);
                    postureLevels.setEnabled(false);
                    postureParams.setEnabled(false);
                }
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimesDialog(0);
            }
        });

        sitTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimesDialog(1);
            }
        });

        sessionToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sessionToggle.isChecked()) {
                    sessionToggle.setChecked(true);
                    consolePreferences.setSessionState(true);
                    broadcastUpdate(Constants.ACTION_START_ACCELEROMETER);
                } else {
                    sessionToggle.setChecked(false);
                    consolePreferences.setSessionState(false);
                    broadcastUpdate(Constants.ACTION_STOP_ACCELEROMETER);
                }
            }
        });

    }

    private void makeLevelsDropDown(Spinner spinner, int flag) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Constants.levelsList);
        spinner.setAdapter(adapter);
        int spinnerPosition;
        if (flag == 0){ // 0: Stress, 1: Posture
            spinnerPosition = adapter.getPosition(consolePreferences.getStressLevel());
            spinner.setEnabled(consolePreferences.getStressState());
        }
        else {
            spinnerPosition = adapter.getPosition(consolePreferences.getPostureLevel());
            spinner.setEnabled(consolePreferences.getPostureState());
        }
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                String item = adapter.getItemAtPosition(i).toString();
                if (flag == 0){ // 0: Stress, 1: Posture
                    consolePreferences.setStressLevel(item);
                }
                else {
                    consolePreferences.setPostureLevel(item);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }
        });
    }

    private void makeParamsDropDown(Spinner spinner, int flag) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Constants.conditionList);
        spinner.setAdapter(adapter);
        int spinnerPosition;
        if (flag == 0){ // 0: Stress, 1: Posture
            spinnerPosition = adapter.getPosition((consolePreferences.getStressParam()));
            spinner.setEnabled(consolePreferences.getStressState());
        }
        else {
            spinnerPosition = adapter.getPosition((consolePreferences.getPostureParam()));
            spinner.setEnabled(consolePreferences.getPostureState());
        }
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                String item = adapter.getItemAtPosition(i).toString();
                if (flag == 0){ // 0: Stress, 1: Posture
                    consolePreferences.setStressParam(item);
                }
                else {
                    consolePreferences.setPostureParam(item);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }
        });
    }

    private void showTimesDialog(int flag){
        final NumberPicker numberPicker = new NumberPicker(getActivity());

        int minValue = 1;
        int maxValue = 24;
        int step = 5;

        String[] numberValues = new String[maxValue - minValue + 1];
        for (int i = 0; i <= maxValue - minValue; i++) {
            numberValues[i] = String.valueOf((minValue + i) * step);
        }

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setDisplayedValues(numberValues);
        if (flag == 0) { // 0: Stress, 1: Sitting
            numberPicker.setValue(stressSensePreferences.getTimeInterval());
        }
        else {
            numberPicker.setValue(stressSensePreferences.getSitTimeInterval());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Intervention Time");
        builder.setMessage("Choose a desired time period (in minutes):");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag == 0) { // 0: Stress, 1: Sitting
                    stressSensePreferences.setTimeInterval(numberPicker.getValue());
                }
                else {
                    stressSensePreferences.setSitTimeInterval(numberPicker.getValue());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setView(numberPicker);
        builder.show();
    }

    private void showMessageDialog(int flag){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reminder message:");

        final EditText input = new EditText(getContext());
        if (flag == 0) {
            input.setText(stressSensePreferences.getReminderMessage());
        } else {
            input.setText(stressSensePreferences.getSitReminderMessage());
        }

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                if (flag == 0) { // 0: Stress, 1: Sitting
                    stressSensePreferences.setReminderMessage(inputText);
                } else {
                    stressSensePreferences.setSitReminderMessage(inputText);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag == 0) { // 0: Stress, 1: Sitting
                    reminderSwitch.setChecked(false);
                    consolePreferences.setReminderState(false);
                }
                else {
                    sitReminderSwitch.setChecked(false);
                    consolePreferences.setSitReminderState(false);
                }
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showIftttDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("View IFTTT help guide?");

        builder.setMultiChoiceItems(new String[]{"Do not show again"}, new boolean[] {false}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if(isChecked) {
                            stressSensePreferences.setIftttHelp(false);
                        }
                        else {
                            stressSensePreferences.setIftttHelp(true);
                        }
                        break;
                }
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), IftttHelperActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void checkSpotifyAvailability(int flag){
        final String appPackageName = "com.spotify.music";
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(appPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            if (flag == 0) { //0:Stress, 1:Sitting
                musicSwitch.setChecked(false);
                consolePreferences.setMusicState(false);
            }
            else {
                sitMusicSwitch.setChecked(false);
                consolePreferences.setSitMusicState(false);
            }
            sendPlayStoreRequest(appPackageName);
            Toast.makeText(getContext(), "Please install Spotify", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIftttAvailability(int flag){
        final String appPackageName = "com.ifttt.ifttt";
        PackageManager pm = getContext().getPackageManager();

        try {
            pm.getPackageInfo(appPackageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e)
        {
            if (flag == 0) { //0:Stress, 1:Sitting
                iftttSwitch.setChecked(false);
                consolePreferences.setIftttState(false);
            }
            else {
                sitIftttSwitch.setChecked(false);
                consolePreferences.setSitIftttState(false);
            }
            sendPlayStoreRequest(appPackageName);
            Toast.makeText(getContext(), "Please install IFTTT", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sendPlayStoreRequest(String appPackageName){
        final String referrer = "adjust_campaign=org.ahlab.stress.sense&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
        try {
            Uri uri = Uri.parse("market://details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException ignored) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private void checkNotificationPermissions(int flag){
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), 0);
            if (flag == 0){ // 0:Stress, 1:Sitting
                notificationsSwitch.setChecked(false);
                consolePreferences.setNotificationState(false);
            }
            else {
                sitNotificationsSwitch.setChecked(false);
                consolePreferences.setSitNotificationState(false);
            }
            Toast.makeText(getContext(), "Please allow Stress Sense to access your notifications", Toast.LENGTH_LONG).show();
        }
    }

    private IntentFilter getIntentFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_RECEIVED_RESPONSE);
        intentFilter.addAction(Constants.ACTION_BLE_CONNECTED);
        intentFilter.addAction(Constants.ACTION_BLE_DISCONNECTED);

        return intentFilter;
    }

    private void broadcastUpdate( final String action)
    {
        final Intent intent = new Intent( action );
        getActivity().sendBroadcast( intent );
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(httpReceiver);
        super.onDestroy();
    }
}