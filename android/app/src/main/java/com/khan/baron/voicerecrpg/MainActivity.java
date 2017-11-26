package com.khan.baron.voicerecrpg;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    private final int PERMISSIONS_REQUEST_DOWNLOAD = 20;

    private GameState mGameState;
    private VoiceControl mVoiceControl;

    public boolean mCanDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameState = new GameState(this);

        mVoiceControl = new VoiceControl(this,
                (TextView) findViewById(R.id.txtInput),
                (TextView) findViewById(R.id.txtOutput),
                mGameState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.microphoneButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voice request in progress...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mVoiceControl.promptSpeechInput();
            }
        });



        try {
            getWordNetDatabase();
        } catch (Exception e) {
            mVoiceControl.setOutputText("Error: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            mVoiceControl.mCanRecord = (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        } else if (requestCode == PERMISSIONS_REQUEST_DOWNLOAD) {
            try {
                if (!mCanDownload) {
                    mCanDownload = true;
                    getWordNetDatabase();
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.activity_main), "Error: " + e.getMessage(),
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    public void getWordNetDatabase() throws IOException {
        if (!isSDCardPresent()) {
            Snackbar.make(findViewById(R.id.activity_main),
                    "Error: SD card not mounted. Cannot access WordNet database.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        File dict_file = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if(dict_file.exists()) {
            Snackbar.make(findViewById(R.id.activity_main), "Found WordNet database...",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            if (isDownloadManagerAvailable()) {
                Snackbar.make(findViewById(R.id.activity_main), "Downloading WordNet database...",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                checkDownloadPermission();
                if (!mCanDownload) {
                    Snackbar.make(findViewById(R.id.activity_main),
                            "Error: permission not given to download WordNet database",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                File archive = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS) + "/wn3.1.dict.tar.gz");
                if(!archive.exists()) {
                    String url = "http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz";
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription("Downloading WordNet database...");
                    request.setTitle("WordNet Database");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(
                                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, "wn3.1.dict.tar.gz");
                    DownloadManager manager = (DownloadManager) getSystemService(
                            Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    BroadcastReceiver onComplete=new BroadcastReceiver() {
                        public void onReceive(Context ctxt, Intent intent) {
                            try {
                                getWordNetDatabase();
                            } catch (Exception e) {
                                mVoiceControl.setOutputText("Error: " + e.getMessage());
                            }
                        }
                    };
                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                } else {

                    // Extract tarball
                    File dest = new File(Environment.getExternalStorageDirectory().getPath());
                    TarArchiveInputStream fin = new TarArchiveInputStream(
                            new GzipCompressorInputStream(new FileInputStream(archive.getPath())));
                    TarArchiveEntry entry;
                    while ((entry = fin.getNextTarEntry()) != null) {
                        if (entry.isDirectory()) {
                            continue;
                        }
                        File curfile = new File(dest, entry.getName());
                        File parent = curfile.getParentFile();
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
                        IOUtils.copy(fin, new FileOutputStream(curfile));
                    }

                    Snackbar.make(findViewById(R.id.activity_main), "Downloaded WordNet database",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            } else {
                Snackbar.make(findViewById(R.id.activity_main),
                        "Error: could not download WordNet database", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
        }
    }

    private boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isDownloadManagerAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    public void checkDownloadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_DOWNLOAD);
            }
        } else {
            mCanDownload = true;
        }
    }

    /// Reserved: may need to use C++ functions in the future
    public native String jniDummy();

    static {
        System.loadLibrary("native-lib");
    }
}
