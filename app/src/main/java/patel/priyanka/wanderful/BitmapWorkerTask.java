package patel.priyanka.wanderful;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String data = strings[0];
        return decodeSampledBitmapFromResource(data);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewWeakReference != null && bitmap != null) {
            final ImageView imageView = imageViewWeakReference.get();

            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(String bitmapString) {
        Bitmap groupIcon = null;
                try {
            byte[] encodeByte = Base64.decode(bitmapString, Base64.DEFAULT);
            groupIcon = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
        }
            return groupIcon;
    }
}
