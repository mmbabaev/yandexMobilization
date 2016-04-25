package mbabaev.artistsinfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import mbabaev.artistsinfo.Model.Artist;
import mbabaev.artistsinfo.Model.Helper;

import static org.junit.Assert.*;

public class ArtistsInfoUnitTest {
    final String YANDEX_JSON_URL = "http://cache-default01h.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    @Test
    public void loadArtistsFromJsonTest() throws Exception {
        String jsonContent = Helper.getTextFromUrl(YANDEX_JSON_URL);
        List<Artist> artists = Artist.loadArtists(jsonContent);
        Artist nirvana = artists.get(artists.size() - 2);

        assertEquals(nirvana.getName(), "Nirvana");
        assertEquals(nirvana.getGenres().get(1), "rock");
        assertEquals(nirvana.getTrackCount(), 471);
        assertEquals(nirvana.getAlbumCount(), 28);
        assertEquals(nirvana.getSmallImageURL(), "http://avatars.yandex.net/get-music-content/045190cb.p.9262/300x300");
    }

    @Test
    public void russianUnitsTest() throws Exception {
        String result = Helper.units(14, "яблоко", "яблока", "яблок");
        assertEquals("14 яблок", result);

        result = Helper.units(1, "яблоко", "яблока", "яблок");
        assertEquals("1 яблоко", result);

        result = Helper.units(20, "яблоко", "яблока", "яблок");
        assertEquals("20 яблок", result);

        result = Helper.units(131, "яблоко", "яблока", "яблок");
        assertEquals("131 яблоко", result);

        result = Helper.units(3452, "яблоко", "яблока", "яблок");
        assertEquals("3452 яблока", result);
    }
}