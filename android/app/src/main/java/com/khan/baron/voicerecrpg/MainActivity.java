package com.khan.baron.voicerecrpg;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.khan.baron.voicerecrpg.game.GameState;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    private final int PERMISSIONS_REQUEST_DOWNLOAD = 20;

    private GameState mGameState;
    public VoiceControl mVoiceControl;

    public boolean mCanDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyPosTaggerModel();

        mGameState = new GameState(this);
        mGameState.initState();

        mVoiceControl = new VoiceControl(this,
                (TextView) findViewById(R.id.txtInput),
                (TextView) findViewById(R.id.txtOutput),
                (TextView) findViewById(R.id.txtTimer),
                mGameState);
        mVoiceControl.setOutputText(mGameState.getInitOutput());

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
            mVoiceControl.setCanRecord((grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED));
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
        boolean foundWordNetDatabase = false;
        if (!isSDCardPresent()) {
            Snackbar.make(findViewById(R.id.activity_main),
                    "Error: SD card not mounted. Cannot access WordNet database.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if(dictFile.exists()) {
            Snackbar.make(findViewById(R.id.activity_main), "Found WordNet database",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            foundWordNetDatabase = true;

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
                    registerReceiver(onComplete, new IntentFilter(
                            DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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

                    foundWordNetDatabase = true;
                }

            } else {
                Snackbar.make(findViewById(R.id.activity_main),
                        "Error: could not download WordNet database", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
        }

        if (foundWordNetDatabase) {
            URL url = new URL("file", null, dictFile.getPath());
            mGameState.addDictionary(url);
        }
    }

    private boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public void checkDownloadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    private void copyPosTaggerModel() {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        String filename = "english-left3words-distsim.tagger";
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(
                    Environment.getExternalStorageDirectory().getPath() + "/" + filename);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Snackbar.make(findViewById(R.id.activity_main),
                    "Error: could not copy POS tagger model", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /// Reserved: may need to use C++ functions in the future
    public native String jniDummy();

    static {
        System.loadLibrary("native-lib");
    }
}
