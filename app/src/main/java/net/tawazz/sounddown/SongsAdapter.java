package net.tawazz.sounddown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;

import net.tawazz.sounddown.helpers.FontAwesome;
import net.tawazz.sounddown.helpers.ScalingUtilities;
import net.tawazz.sounddown.helpers.WebRequest;


/**
 * Created by tawanda on 2/10/15.
 */
class SongsAdapter extends ArrayAdapter<Track> {

    private Context activity;
    private AdapterListener listener;
    private MediaPlayer mediaPlayer;

    public interface AdapterListener {

        public void getSong(Track song, int pos);

        public void primarySeekBarProgressUpdater(SeekBar seekBarProgress, final MediaPlayer mediaPlayer, final int mediaFileLengthInMilliseconds);
    }

    public SongsAdapter(Context context, Track[] songs) {
        super(context, R.layout.custom_row, songs);
        activity = context;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        View customView = convertView;
        final int pos = position;
        if (customView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            customView = inflater.inflate(R.layout.custom_row, parent, false);

            holder = new Holder();
            holder.title = (TextView) customView.findViewById(R.id.textView_title);
            holder.user = (TextView) customView.findViewById(R.id.textView_user);
            holder.time = (IconTextView) customView.findViewById(R.id.textView_time);
            holder.likes = (IconTextView) customView.findViewById(R.id.textView_likes);
            holder.artwork = (ImageView) customView.findViewById(R.id.imageView_artwork);
            holder.download = (IconTextView) customView.findViewById(R.id.download);
            holder.preview = (IconTextView) customView.findViewById(R.id.textView_play);
            customView.setTag(holder);
        } else {
            holder = (Holder) customView.getTag();
        }
        final Track song = getItem(position);
        if (song.getArtwork() != null) {
            
            final Holder h = holder;
            Runnable scalling = new Runnable() {
                @Override
                public void run() {
                    int width = h.artwork.getWidth();
                    int height = h.artwork.getHeight();
                    Bitmap image = ScalingUtilities.createScaledBitmap(song.getArtwork(), width, height, ScalingUtilities.ScalingLogic.FIT);
                    h.artwork.setImageBitmap(image);
                }
            };
            Handler handler = new Handler();
            handler.post(scalling);


        }
        String likesText = getContext().getString(R.string.heart) + " " + song.getLike();
        holder.title.setText(song.getTitle());
        holder.user.setText(song.getUser());
        holder.time.setText(song.getTime());
        holder.likes.setText(likesText);


        final IconTextView preview = holder.preview;
        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        preview(song.getPreviewUrl(), preview);
                    } else {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        preview.setText(getContext().getString(R.string.play));
                    }
                } else {
                    mediaPlayer = new MediaPlayer();
                }


                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        preview.setText(getContext().getString(R.string.play));
                    }
                });

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        preview.setText(getContext().getString(R.string.pause));
                    }
                });
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "clicked");
                try {
                    listener.getSong(song, pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return customView;
    }

    public void callback(AdapterListener listener) {
        this.listener = listener;
    }

    public MediaPlayer preview(String streamUrl, IconTextView preview) {

        try {
            mediaPlayer.setDataSource(streamUrl); // URL to mediaplayer data source
            mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            preview.setText(getContext().getString(R.string.buffering));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    public static class Holder {
        public TextView title, user;
        public ImageView artwork;
        public IconTextView preview, download, time, likes;
    }
}
