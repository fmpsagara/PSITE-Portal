package app.psiteportal.com.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.JSONParser;


public class CaptureImageFragment extends Fragment{

    private static final String TAG = "CallCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    String encodedImage;
    Uri fileUri = null;
    ImageView photoImage = null;
    Button send;
    String pass1, pass2;
    String name, price;
    Resources res;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_upload_image = "http://psite7.org/portal/add_image.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public CaptureImageFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_image_layout, container, false);

        send = (Button) rootView.findViewById(R.id.send);
        photoImage = (ImageView) rootView.findViewById(R.id.photo_image);
        Button callCameraButton = (Button) rootView.findViewById(R.id.button_callcamera);

        res = getResources();

        callCameraButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Uri.fromFile(getOutputPhotoFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ );
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CreateNewProduct().execute();
            }

        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                Uri photoUri = null;
                if (data == null) {
                    photoUri = fileUri;
                } else {
                    photoUri = data.getData();
                    Toast.makeText(getActivity(), "Image saved successfully in: " + data.getData(),
                            Toast.LENGTH_LONG).show();


                }
                showPhoto(photoUri.getPath());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Callout for image capture failed!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPhoto(String photoUri) {
        File imageFile = new File (photoUri);
        if (imageFile.exists()){

            name = imageFile.getName().toString();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            BitmapDrawable drawable = new BitmapDrawable(this.getResources(), bitmap);
            photoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoImage.setImageDrawable(drawable);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);


        }

    }


    private File getOutputPhotoFile() {

        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getActivity().getPackageName());

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());

        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        String message;
        int success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Uploading image . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("price", encodedImage));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_upload_image,
                    "POST", params);

            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    message = json.getString(TAG_MESSAGE);
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            photoImage.setImageResource(R.drawable.photo_holder);
            if(success == 1){
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
