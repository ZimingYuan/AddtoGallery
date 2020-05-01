package com.yzm.addtogallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int filenum;
    private Button show;
    private FilePickerDialog dialog;
    private boolean get_permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        properties.show_hidden_files = false;
        dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Choose files");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                ArrayList<String> list = new ArrayList<>();
                for (String i: files) {
                    File f = new File(i);
                    if (f.isDirectory()) {
                        for (File j: f.listFiles())
                            if (check(j)) list.add(j.toString());
                    } else if (check(f)) list.add(f.toString());
                }
                show.setVisibility(View.INVISIBLE);
                filenum = list.size();
                Toast.makeText(MainActivity.this, "Adding now...", Toast.LENGTH_SHORT).show();
                MediaScannerConnection.scanFile(MainActivity.this, list.toArray(new String[list.size()]), null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        filenum--;
                        if (filenum == 0) {
                            show.post(new Runnable() {
                                @Override
                                public void run() {
                                    show.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Adding successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        String[] permissions = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };
        requestPermissions(permissions, FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT);
        show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    if (get_permission) dialog.show();
                    else Toast.makeText(MainActivity.this, "You didn't grant the permission!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(requestCode == FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                get_permission = true;
        }
    }

    private boolean check(File f) {
        String[] suffix = new String[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".mp4", ".avi" };
        for (String i : suffix)
            if (f.getName().endsWith(i)) return true;
        return false;
    }

}
