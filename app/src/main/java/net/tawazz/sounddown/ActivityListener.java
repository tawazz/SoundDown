package net.tawazz.sounddown;

import android.support.design.widget.FloatingActionButton;

public interface ActivityListener{

    public void destroy();
    public void playPause(FloatingActionButton playBtn,Track song);
    public void playNext();
    public void playPrev();
}
