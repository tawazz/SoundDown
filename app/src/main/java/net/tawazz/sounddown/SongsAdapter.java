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

        Holder holder= null;
        View customView = convertView;
        if(customView == null){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            customView = inflater.inflate(R.layout.custom_row, parent, false);

            holder = new Holder();
            holder.title = (TextView) customView.findViewById(R.id.textView_title);
            holder.user = (TextView) customView.findViewById(R.id.textView_user);
            holder.time = (TextView) customView.findViewById(R.id.textView_time);
            holder.likes = (TextView) customView.findViewById(R.id.textView_likes);
            holder.artwork = (ImageView) customView.findViewById(R.id.imageView_artwork);

            customView.setTag(holder);
        }else {
            holder = (Holder) customView.getTag();
        }
        Track song = getItem(position);
        if(song.getArtwork() !=null) {
            holder.artwork.setImageBitmap(song.getArtwork());
        }
        holder.title.setText(song.getTitle());
        holder.user.setText(song.getUser());
        holder.time.setText(song.getTime());
        holder.likes.setText(song.getLike());

        return customView;
    }

    public static class Holder {
        public TextView title,user,time,likes;
        public ImageView artwork;
    }
}
