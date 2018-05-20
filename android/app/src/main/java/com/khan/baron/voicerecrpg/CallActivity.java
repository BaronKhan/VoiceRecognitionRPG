package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khan.baron.voicerecrpg.call.CallState;

import java.io.File;
import java.net.URL;

public class CallActivity extends AppCompatActivity {
    public VoiceControl mVoiceControl;

    public CallState mCallState;

    private RecyclerView mRecyclerViewParticipants;
    private ImageView mImageViewVideo;
    private ImageView mImageViewAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mCallState = new CallState(this);
        mVoiceControl = new VoiceControl(this,
                (TextView) findViewById(R.id.txtInput),
                (TextView) findViewById(R.id.txtOutput),
                (TextView) findViewById(R.id.txtTimer),
                mCallState, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.microphoneButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voice request in progress...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mVoiceControl.promptSpeechInput();
            }
        });

        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            Snackbar.make(findViewById(R.id.activity_call), "Found WordNet database",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            try {
                URL url = new URL("file", null, dictFile.getPath());
                mCallState.addDictionary(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Snackbar.make(findViewById(R.id.activity_call),
                    "Error: could not download WordNet database", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        mRecyclerViewParticipants = (RecyclerView) findViewById(R.id.recyclerViewParticipants);
        mRecyclerViewParticipants.setAdapter(new CallActivity.ParticipantsAdapter());
        mRecyclerViewParticipants.setLayoutManager(new LinearLayoutManager(this));

        mImageViewVideo = (ImageView) findViewById(R.id.imageViewMyVideo);
        mImageViewAudio = (ImageView) findViewById(R.id.imageViewMyAudio);
    }

    public ImageView getImageViewAudio() {
        return mImageViewAudio;
    }

    public void setImageViewAudio(ImageView mImageViewAudio) {
        this.mImageViewAudio = mImageViewAudio;
    }

    public ImageView getImageViewVideo() {
        return mImageViewVideo;
    }

    public void setImageViewVideo(ImageView mImageViewVideo) {
        this.mImageViewVideo = mImageViewVideo;
    }

    // Taken from example at: https://guides.codepath.com/android/using-the-recyclerview
    public class ParticipantsAdapter extends RecyclerView.Adapter<CallActivity.ParticipantsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView participantTextView;
            public ImageView participantAudioImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                participantTextView = (TextView) itemView.findViewById(R.id.particpantTextView);
                participantAudioImageView = (ImageView) itemView.findViewById(R.id.imageViewParticipantAudio);
            }
        }

        @Override
        public CallActivity.ParticipantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View participantView = inflater.inflate(R.layout.layout_participants, parent, false);

            CallActivity.ParticipantsAdapter.ViewHolder viewHolder =
                    new CallActivity.ParticipantsAdapter.ViewHolder(participantView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CallActivity.ParticipantsAdapter.ViewHolder viewHolder, int position) {
            TextView textView = viewHolder.participantTextView;
            textView.setText("In Call: "+mCallState.getParticipants().get(position).getName());
            ImageView imageView = viewHolder.participantAudioImageView;
            imageView.setImageResource((mCallState.getParticipantsAudio().get(position))
                    ? R.drawable.ic_audio
                    : R.drawable.ic_audio_off);
        }

        @Override
        public int getItemCount() {
            int size = mCallState.getParticipants().size();
            if (size > 0) {
                mRecyclerViewParticipants.setVisibility(View.VISIBLE);
                mImageViewAudio.setVisibility(View.VISIBLE);
                mImageViewVideo.setVisibility(View.VISIBLE);
            }
            else {
                mRecyclerViewParticipants.setVisibility(View.INVISIBLE);
                mImageViewAudio.setVisibility(View.INVISIBLE);
                mImageViewVideo.setVisibility(View.INVISIBLE);
            }
            return size;
        }
    }

    public void updateParticipants() {
        mRecyclerViewParticipants.setAdapter(new CallActivity.ParticipantsAdapter());
        mRecyclerViewParticipants.invalidate();
    }
}
