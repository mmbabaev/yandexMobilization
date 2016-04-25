package artistsinfo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import artistsinfo.model.Artist;
import artistsinfo.R;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    private LayoutInflater layoutInflater;
    private List<Artist> artists;
    private List<Artist> filteredArtists;

    public ArtistAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
        this.artists = artists;
        this.filteredArtists = artists;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredArtists.size();
    }

    // for searching artists by name
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                final ArrayList<Artist> filteredList = new ArrayList<>();

                for (Artist a : artists) {
                    if (a.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        filteredList.add(a);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredArtists = (ArrayList<Artist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistRowHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.artist_row_layout, null);
            holder = new ArtistRowHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.artistImage);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.genres = (TextView) convertView.findViewById(R.id.genres);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        }
        else {
            holder = (ArtistRowHolder) convertView.getTag();
        }

        Artist artist = filteredArtists.get(position);
        holder.name.setText(artist.getName());
        holder.genres.setText(TextUtils.join(", ", artist.getGenres()));
        holder.info.setText(artist.getInfo());

        if (cancelPotentialDownload(artist.getSmallImageURL(), holder.image)) {
            ImageDownloaderTask task = new ImageDownloaderTask(holder.image);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            holder.image.setImageDrawable(downloadedDrawable);
            task.execute(artist.getSmallImageURL());
        }

        return convertView;
    }

    @Override
    public Artist getItem(int position) {
        return filteredArtists.get(position);
    }

    /**
     * stop the possible download in progress on this imageView since a new one is about to start
     *
     * @return false is image already have been downloaded
     */

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageDownloaderTask imageDownloaderTask = getImageDownloaderTask(imageView);

        if (imageDownloaderTask != null) {
            String imageUrl = imageDownloaderTask.url;
            if ((imageUrl == null) || (!imageUrl.equals(url))) {
                imageDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }


    private static ImageDownloaderTask getImageDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    // model of row in list view
    static class ArtistRowHolder {
        ImageView image;
        TextView name;
        TextView genres;
        TextView info;
    }

    // bind download task with content of image view
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<ImageDownloaderTask> imageDownloaderTaskReference;

        public DownloadedDrawable(ImageDownloaderTask imageDownloaderTask) {
            super(Color.GRAY);
            imageDownloaderTaskReference = new WeakReference<>(imageDownloaderTask);
        }

        public ImageDownloaderTask getBitmapDownloaderTask() {
            return imageDownloaderTaskReference.get();
        }
    }
}