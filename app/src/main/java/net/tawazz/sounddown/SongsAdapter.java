package net.tawazz.sounddown;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by tawanda on 2/10/15.
 */
class SongsAdapter extends ArrayAdapter<Track>  {


    public SongsAdapter(Context context, Track[] songs) {
        super(context,R.layout.custom_row, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = LayoutInflater.from(getContext());
        View customView = inflator.inflate(R.layout.custom_row, parent, false);

        Track song = getItem(position);
        TextView title = (TextView) customView.findViewById(R.id.textView_title);
        TextView user = (TextView) customView.findViewById(R.id.textView_user);
        TextView time = (TextView) customView.findViewById(R.id.textView_time);
        TextView likes = (TextView) customView.findViewById(R.id.textView_likes);
        final ImageView artwork = (ImageView) customView.findViewById(R.id.imageView_artwork);
        if(song.getArtwork() !=null) {
            artwork.setImageBitmap(song.getArtwork());
        }else {
            artwork.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.music));
        }
        title.setText(song.getTitle());
        user.setText(song.getUser());
        time.setText(song.getTime());
        likes.setText(song.getLike());

        return customView;
    }
}
