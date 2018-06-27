package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        mMediaPlayer.start();

                    }

                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Pause playback
                        releaseMediaPlayer();
                    }
                }
            };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Create an array

        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Friend", "Tomodachi\n友達", R.drawable.family_son, R.raw.friend));
        words.add(new Word("Son", "Musuko\n息子", R.drawable.family_son, R.raw.son));
        words.add(new Word("Daughter", "Musume\n娘", R.drawable.family_daughter, R.raw.daughter));
        words.add(new Word("Wife", "Tsuma\n妻", R.drawable.family_mother, R.raw.wife));
        words.add(new Word("Husband", "Otto\n夫", R.drawable.family_father, R.raw.otto));
        words.add(new Word("Mother", "Okaasan\nお母さん", R.drawable.family_mother, R.raw.mother));
        words.add(new Word("Father", "Otousan\nお父さん", R.drawable.family_father, R.raw.father));
        words.add(new Word("Grandmother", "Obaasan\nおばあさん", R.drawable.family_grandmother, R.raw.grandma));
        words.add(new Word("Grandfather", "Ojiisan\nおじいさん", R.drawable.family_grandfather, R.raw.grandpa));
        words.add(new Word("Older Brother", "Oniisan\nお兄さん", R.drawable.family_older_brother, R.raw.olderbrother));
        words.add(new Word("Younger Sister", "Imouto\n妹", R.drawable.family_younger_sister, R.raw.youngersister));
        words.add(new Word("Older Sister", "Oneesan\n弟", R.drawable.family_older_sister, R.raw.oldersister));
        words.add(new Word("Younger Brother", "Otouto\nお姉さん", R.drawable.family_younger_brother, R.raw.youngerbrother));

        //LinearLayout rootView = (LinearLayout)findViewById(R.id.rootView);

        WordAdapter adapter = new WordAdapter(this, words, R.color.famback);
        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                // Get the {@link Word} object at the given position the user clicked on
                Word word = words.get(position);

                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // Release the media player if it currently exists because we are about to
                    // play a different sound file
                    releaseMediaPlayer();

                    // Create and setup the {@link MediaPlayer} for the audio resource associated
                    // with the current word
                    mMediaPlayer = MediaPlayer.create(FamilyActivity.this, word.getAudioResourceId());

                    // Start the audio file
                    mMediaPlayer.start();

                    // Setup up a onCompletionListener so that we can stop and release the media player once the sound has finished playing
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);

                }


            }
        });

//
    }

    @Override
    protected void onStop() {
        super.onStop();

        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

}
