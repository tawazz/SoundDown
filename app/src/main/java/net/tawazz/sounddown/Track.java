package net.tawazz.sounddown;

/**
 * Created by tawanda on 2/10/15.
 */
public class Track {

    private String details,artworkUrl,streamUrl;

    public Track(String songDetails,String artwork, String mp3){
        details = songDetails;
        artworkUrl = artwork;
        streamUrl = mp3;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
