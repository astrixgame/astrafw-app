package com.astrix.astrafwapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private androidx.appcompat.app.AlertDialog permissionDialog;
    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        List<String> permissionsToRequest = new ArrayList<>();
        for(String permission : permissions)
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission);

        if(!permissionsToRequest.isEmpty())
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 1);
    }

    private boolean arePermissionsGranted() {
        String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };

        for(String permission : permissions)
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;

        return true;
    }

    private void showPermissionDeniedDialog() {
        if(permissionDialog != null && permissionDialog.isShowing())
            return;

        permissionDialog = new MaterialAlertDialogBuilder(this)
            .setTitle("Bluetooth Access Required")
            .setMessage("To connect with your AstraFW watch, we need permission to use Bluetooth and location services. These are required for BLE communication.")
            .setCancelable(false)
            .setPositiveButton("Settings", (dialog, which) -> {
                android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                android.net.Uri uri = android.net.Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            })
            .setNegativeButton("Close", (dialog, which) -> finishAffinity())
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!arePermissionsGranted()) {
            showPermissionDeniedDialog();
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            boolean allGranted = true;
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if(allGranted) {

            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkPermissions();
    }
}