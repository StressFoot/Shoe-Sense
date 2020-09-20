package org.stressfoot.stress.sense.ui.notifications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.stressfoot.stress.sense.R;
import org.stressfoot.stress.sense.models.Connection;
import org.stressfoot.stress.sense.models.ConnectionListAdapter;
import org.stressfoot.stress.sense.ui.IftttHelperActivity;
import org.stressfoot.stress.sense.util.StressSensePreferences;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {
    private final String IP = "IP Address";
    private final String MAC = "MAC Address";

    private ListView connectionList;
    private ArrayList<Connection> connections = new ArrayList<>();
    private ConnectionListAdapter adapter;
    private Button ifttHelpButton;
    private Button calibrateButton;
    private String inputText;
    private StressSensePreferences stressSensePreferences;

    private Connection ipConnection;
    private Connection macConnection;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        connectionList = (ListView) root.findViewById(R.id.connectionList);
        ifttHelpButton = root.findViewById(R.id.helpButton);
        calibrateButton = root.findViewById(R.id.calibrateButton);

        stressSensePreferences = StressSensePreferences.getInstance(getContext());

//        ipConnection = new Connection();
//        ipConnection.setConnectionType(IP);
//        ipConnection.setAddress(stressSensePreferences.getIpAddress());
//        connections.add(ipConnection);

        macConnection = new Connection();
        macConnection.setConnectionType(MAC);
        macConnection.setAddress(stressSensePreferences.getMacAddress());
        connections.add(macConnection);

        adapter = new ConnectionListAdapter(getContext(), connections);
        connectionList.setAdapter(adapter);

        connectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Connection connection = adapter.getItem(position);
                showInputDialog(connection.getConnectionType());
            }
        });

        calibrateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("");
                dialog.setMessage("Calibrating...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        ifttHelpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IftttHelperActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void showInputDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        final EditText input = new EditText(getContext());
        switch (title){
            case IP:
                input.setText(stressSensePreferences.getIpAddress());
                break;
            case MAC:
                input.setText(stressSensePreferences.getMacAddress());
                break;
        }
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputText = input.getText().toString();
                switch (title) {
                    case IP:
                        stressSensePreferences.setIpAddress(inputText);
                        ipConnection.setAddress(stressSensePreferences.getIpAddress());
                        break;
                    case MAC:
                        stressSensePreferences.setMacAddress(inputText);
                        macConnection.setAddress(stressSensePreferences.getMacAddress());
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}