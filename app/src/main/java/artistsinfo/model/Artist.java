package artistsinfo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Representation of artist from yandex json file
 */
@JsonIgnoreProperties("id")
public class Artist implements Serializable, Comparable<Artist> {

    @JsonProperty("name")
    private String name;

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("tracks")
    private int trackCount;

    @JsonProperty("albums")
    private int albumCount;

    @JsonProperty("link")
    private String link;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover")
    private ArtistImage image;

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallImageURL() {
        return image.small;
    }

    public String getBigImageURL() {
        return image.big;
    }

    public String getInfo() {
        String albums = Helper.units(albumCount, "альбом", "альбома", "альбомов");
        String tracks = Helper.units(trackCount, "песня", "песни", "песен");
        return albums + ", " + tracks;
    }

    @Override
    public int compareTo(Artist artist) {
        return this.getName().compareTo(artist.getName());
    }

    /**
     * Load list of Artist objects from yandex json file
     *
     * @param jsonContent text in yandex file
     * @return list of artists
     * @throws IOException
     */
    public static List<Artist> loadArtists(String jsonContent) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonContent, new TypeReference<List<Artist>>() {} );
    }
}

class ArtistImage implements Serializable {
    @JsonProperty("small")
    public String small;

    @JsonProperty("big")
    public String big;
}