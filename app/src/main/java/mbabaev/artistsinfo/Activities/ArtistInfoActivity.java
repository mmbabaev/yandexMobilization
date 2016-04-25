package mbabaev.artistsinfo.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import mbabaev.artistsinfo.ImageDownloaderTask;
import mbabaev.artistsinfo.Model.Artist;
import mbabaev.artistsinfo.R;

public class ArtistInfoActivity extends AppCompatActivity {

    TextView infoTextView;
    ImageView artistImageView;
    TextView descriptionTextView;
    TextView genresTextView;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
        artistImageView = (ImageView) findViewById(R.id.artist_big_image);

        Drawable defaultArtist = ContextCompat.getDrawable(this, R.drawable.default_artist);
        artistImageView.setImageDrawable(defaultArtist);

        infoTextView = (TextView) findViewById(R.id.artist_info);
        descriptionTextView = (TextView) findViewById(R.id.artist_description);
        genresTextView = (TextView) findViewById(R.id.artist_genres);

        ImageDownloaderTask task = new ImageDownloaderTask(artistImageView);

        Artist artist = (Artist) getIntent().getSerializableExtra("current_artist");

        this.setTitle(artist.getName());

        genresTextView.setText(TextUtils.join(", ", artist.getGenres()));
        infoTextView.setText(artist.getInfo());
        descriptionTextView.setText(artist.getDescription());

        task.execute(artist.getBigImageURL());
    }
}
