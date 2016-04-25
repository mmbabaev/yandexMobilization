package artistsinfo.view.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import artistsinfo.model.Artist;
import artistsinfo.model.Helper;
import artistsinfo.view.ArtistAdapter;
import artistsinfo.R;

public class ArtistsListActivity extends AppCompatActivity {
    final String YANDEX_JSON_URL = "http://cache-default03d.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    final String ARTISTS_JSON = "artists.json";

    EditText searchEditText;
    ProgressDialog jsonProgressDialog;

    private void createJsonProgressDialog() {
        jsonProgressDialog = new ProgressDialog(ArtistsListActivity.this);
        jsonProgressDialog.setTitle("Загрузка необходимого файла");
        jsonProgressDialog.setMessage("Идет загрузка необходимого файла, подождите");
        jsonProgressDialog.setCancelable(false);
        jsonProgressDialog.show();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_list);
        setTitle("Музыканты");

        try {
            File artistsFile = new File(getCacheDir(), ARTISTS_JSON);
            String jsonContent = Helper.readTextFromFile(artistsFile);
            List<Artist> artists = Artist.loadArtists(jsonContent);
            setArtists(artists);
        } catch (Exception e) { // if file doesn't exist
            createJsonProgressDialog();

            class JsonLoadTask extends AsyncTask<Void, Void, List<Artist>> {
                @Override
                protected List<Artist> doInBackground(Void... voids) {
                    try {
                        String jsonContent = Helper.getTextFromUrl(YANDEX_JSON_URL);
                        tryWriteJsonFile(jsonContent);
                        return Artist.loadArtists(jsonContent);
                    }
                    catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<Artist> artists) {
                    if (artists != null) {
                        jsonProgressDialog.dismiss();
                        setArtists(artists);
                    }
                    else {
                        jsonProgressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArtistsListActivity.this);
                        builder.setTitle("Ошибка!");
                        builder.setMessage("При загрузке файла произошла ошибка.");
                        builder.setCancelable(false);
                        builder.setNeutralButton("Попробовать снова",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        createJsonProgressDialog();
                                        //recursive call of json download task, try download again
                                        new JsonLoadTask().execute();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }

            new JsonLoadTask().execute();
        }
    }

    /**
     * Setup main view after artists list downloading
     *
     * @param artists list of artists to show
     */
    @SuppressWarnings("ConstantConditions")
    public void setArtists(final List<Artist> artists) {
        final ListView listView = (ListView) findViewById(R.id.artistsListView);

        final ArtistAdapter adapter = new ArtistAdapter(this, artists);
        listView.setAdapter(adapter);

        Collections.sort(artists);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = adapter.getItem(i);
                Intent intent = new Intent(ArtistsListActivity.this, ArtistInfoActivity.class);
                intent.putExtra("current_artist", artist);
                startActivity(intent);
            }
        });

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString().toLowerCase());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // hide keyboard when list view on touch
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputManager =
                        (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        ArtistsListActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
    }

    /**
     * Try to write content of json file in cache memory
     *
     * @param json content of json file to write
     */
    void tryWriteJsonFile(String json) {
        FileOutputStream outputStream = null;
        try {
            File file = new File(getCacheDir(), ARTISTS_JSON);
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                outputStream.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
