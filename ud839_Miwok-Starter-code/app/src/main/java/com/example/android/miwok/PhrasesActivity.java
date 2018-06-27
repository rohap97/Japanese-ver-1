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

public class PhrasesActivity extends AppCompatActivity {

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
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Create an array

        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Hello", "Konnichiwa\nこんにちは", R.raw.konnichiwa));
        words.add(new Word("What is your name?", "Onamae wa nan desu ka?\nお名前は何ですか", R.raw.whatsyourname));
        words.add(new Word("My name is …", "Watashi no namae wa … desu\n私の名前は … です", R.raw.mynameis));
        words.add(new Word("Nice to meet you", "Hajimemashite\n初めまして", R.raw.hajime));
        words.add(new Word("Thank you", "Arigatō\nありがとう", R.raw.arigato));
        words.add(new Word("Excuse me", "Sumimasen\nすみません", R.raw.sumimasen));
        words.add(new Word("Sorry", "Gomen Nasai\nごめんなさい", R.raw.gomennasai));
        words.add(new Word("Please", "Dōzo\nどうぞ", R.raw.dozo));
        words.add(new Word("Cheers!", "Kampai!\n乾杯！", R.raw.kampai));
        words.add(new Word("Goodbye", "Sayōnara\nさようなら", R.raw.sayonara));
        words.add(new Word("That's good", "Yokatta\nよかった", R.raw.yokatta));
        words.add(new Word("Where is the bathroom?", "Otearai wa doko desu ka?\nお手洗いはどこですか" ,R.raw.wheresthebathroom));
        words.add(new Word("Yes", "Hai\nはい", R.raw.hai));
        words.add(new Word("No", "Iie\nいいえ", R.raw.iie));
        words.add(new Word("Very good", "Totemo yoi\nとても良い", R.raw.totemoyoi));
        words.add(new Word("Beautiful", "Utsukushii\n美しい", R.raw.utsukushii));
        words.add(new Word("Delicious!", "Oishii\nおいしい", R.raw.oishii));
        words.add(new Word("I like it", "Suki desu\n好きです", R.raw.sukidesu));
        words.add(new Word("Where is …?", "… wa doko desu ka?\n… はどこですか", R.raw.whereis));
        words.add(new Word("What?", "Nani?\n何", R.raw.nani));
        words.add(new Word("When?", "Itsu?\nいつ" ,R.raw.itsu));
        words.add(new Word("Welcome!", "Yōkoso!\nようこそ", R.raw.welcome));
        words.add(new Word("Good morning", "Ohayō\nおはよう", R.raw.ohayo));
        words.add(new Word("Good night", "Oyasumi nasai\nおやすみなさい", R.raw.oyasumi));
        words.add(new Word("See you later", "Ja Matane\nじゃあまたね", R.raw.jamatane));
        words.add(new Word("Thank you very much", "Domo arigatō gozaimasu\nども ありがとうございます", R.raw.domoarigato));
        words.add(new Word("You're welcome", "Dō itashimashite\nどういたしまして", R.raw.doitashimashite));
        words.add(new Word("Congratulations!", "Omedetō gozaimasu!\nおめでとうございます", R.raw.omedeto));

        //LinearLayout rootView = (LinearLayout)findViewById(R.id.rootView);

        WordAdapter adapter = new WordAdapter(this, words, R.color.phrback1);
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
                    mMediaPlayer = MediaPlayer.create(PhrasesActivity.this, word.getAudioResourceId());

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
