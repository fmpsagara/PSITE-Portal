package app.psiteportal.com.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;

/**
 * Created by fmpdroid on 1/28/2016.
 */
public class CustomListAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    private List<Nominee> nominees;

    public CustomListAdapter(Context context, List<Nominee> nominees){
        this.context = context;
        this.nominees = nominees;
    }


    @Override
    public int getCount() {
        return nominees.size();
    }

    @Override
    public Object getItem(int position) {
        return nominees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return nominees.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.gp_nominees, null);
        }
        //convertView = inflater.inflate(R.layout.gp_nominees, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.pic);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView institution = (TextView) convertView.findViewById(R.id.institution);

        Nominee n = nominees.get(position);

//        URL myUrl = null;
//        try {
//            myUrl = new URL(n.getImageUrl());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        InputStream inputStream = null;
//        try {
//            inputStream = (InputStream)myUrl.getContent();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Drawable drawable = Drawable.createFromStream(inputStream, null);
//        imageView.setImageDrawable(drawable);
//        imageView.setText(n.getImageUrl());

        //second comment starts here
//        Bitmap bitmap;
//
//        URL imageURL = null;
//
//        try {
//            imageURL = new URL(n.getImageUrl());
//            Log.d("image",String.valueOf(imageURL));
//        }
//
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            HttpURLConnection connection= (HttpURLConnection)imageURL.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream inputStream = connection.getInputStream();
//
//            bitmap = BitmapFactory.decodeStream(inputStream);//Convert to bitmap
//            imageView.setImageBitmap(bitmap);
//        }
//        catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        new LoadImage(imageView).execute(n.getImageUrl());
        imageView.setImageBitmap(n.getBitmap());
        name.setText(n.getName());
        institution.setText(n.getInstitution());
        return convertView;
    }

    class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView imv;
        private String path;
        private Bitmap bitmap;

        public LoadImage(ImageView imv) {
            this.imv = imv;
        }

        @Override
        protected Bitmap doInBackground(String... params) {


            URL imageURL = null;

            try {
                imageURL = new URL(params[0]);
                Log.d("image", String.valueOf(imageURL));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                bitmap = BitmapFactory.decodeStream(inputStream);//Convert to bitmap
                //bitmap.createScaledBitmap(bitmap, 500, 500, true);
            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            imv.setImageBitmap(bitmap);
        }
//            if (!imv.getTag().toString().equals(path)) {
//               /* The path is not same. This means that this
//                  image view is handled by some other async task.
//                  We don't do anything and return. */
//                return;
//            }
//
//            if(result != null && imv != null){
//                imv.setVisibility(View.VISIBLE);
//                imv.setImageBitmap(result);
//            }else{
//                imv.setVisibility(View.GONE);
//            }
    }

    }
