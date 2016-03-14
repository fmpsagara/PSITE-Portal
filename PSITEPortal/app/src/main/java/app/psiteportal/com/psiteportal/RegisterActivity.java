package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import static app.psiteportal.com.psiteportal.CommonUtilities.SENDER_ID;
import static app.psiteportal.com.psiteportal.CommonUtilities.SERVER_URL;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Personal on 9/7/2015.
 */
public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ProgressDialog pDialog;
    EditText username_txt, password_txt, repassword_txt, fname_txt, lname_txt, contact_txt, email_txt, address_txt;
    RadioGroup gender_radio_group;
    RadioButton gender_radio;
    Spinner institution;
    Button registerUser;
    Button uploadPhoto;
    ImageView photoHolder;
    String picturePath;
    String selectedInstitution;
    String encodedImage;
    String photoName;
    int selectedRadId;
    List<String> instListArr = new ArrayList<String>();
    List<String> instIdListArr = new ArrayList<String>();
    ArrayAdapter<String> spinnerArrayAdapter;
    String username, password, firstname, lastname, gender, contact, email, address;
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    private  static final String TAG_INSTITUTIONS = "institutions";
    private  static final String TAG_INSTI_ID = "institution_id";
    private  static final String TAG_INSTI_NAME = "institution_name";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Registration");

        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        repassword_txt = (EditText) findViewById(R.id.repassword_txt);
        fname_txt = (EditText) findViewById(R.id.firstname_txt);
        lname_txt = (EditText) findViewById(R.id.lastname_txt);
        contact_txt = (EditText) findViewById(R.id.contact_txt);
        email_txt = (EditText) findViewById(R.id.email_txt);
        address_txt = (EditText) findViewById(R.id.address_txt);
        institution = (Spinner) findViewById(R.id.institution_spinner);
        photoHolder = (ImageView) findViewById(R.id.profpic_holder);
        uploadPhoto = (Button) findViewById(R.id.select_photo_btn);
//        registerUser = (Button) findViewById(R.id.register_btn);


        new GetInstitutions().execute();

        spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, instListArr);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        institution.setAdapter(spinnerArrayAdapter);
        institution.setOnItemSelectedListener(this);

        addListenerOnButton();

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

    }

    public void selectImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select photo"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {
            if(Build.VERSION.SDK_INT < 19){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            }else{
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":")+1);
                cursor.close();

                cursor = getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
                picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }

            decodeFile(picturePath);

        }
    }

    public void decodeFile(String filePath) {

        File imageFile = new File(filePath);

        if(imageFile.exists()){

            photoName = imageFile.getName().toString();
            Toast.makeText(this, photoName, Toast.LENGTH_SHORT).show();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(filePath, o2);

            photoHolder.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        }else{
            Toast.makeText(this, "Image file does not exist.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedInstitution = instIdListArr.get(position)+"";

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void addListenerOnButton() {

        gender_radio_group = (RadioGroup) findViewById(R.id.gender_radio_group);
        registerUser = (Button) findViewById(R.id.register_btn);


        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

                registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedRadId = gender_radio_group.getCheckedRadioButtonId();
                Log.i("radio id", selectedRadId + "<<");
                gender_radio = (RadioButton) findViewById(selectedRadId);
                gender = gender_radio.getText().toString();

                if (password_txt.getText().toString().equals(repassword_txt.getText().toString())) {
                    new RegisterTask().execute();
                } else {
                    Toast.makeText(RegisterActivity.this, "Password did not match!", Toast.LENGTH_SHORT).show();
                    password_txt.setText("");
                    repassword_txt.setText("");
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void cancelRegister(View v) {
        finish();
    }

    private class RegisterTask extends AsyncTask<String, String, String> {


        String username = username_txt.getText().toString();
        String password = password_txt.getText().toString();
        String firstname = fname_txt.getText().toString();
        String lastname = lname_txt.getText().toString();
        String contact = contact_txt.getText().toString();
        String email = email_txt.getText().toString();
        String address = address_txt.getText().toString();

        int success;
        Resources res = getResources();
        String REGISTER_URL = res.getString(R.string.register_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setTitle("Creating new account . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("firstname", firstname));
                params.add(new BasicNameValuePair("lastname", lastname));
                params.add(new BasicNameValuePair("gender", gender));
                params.add(new BasicNameValuePair("contact", contact));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("address", address));
                params.add(new BasicNameValuePair("institution", selectedInstitution));
                params.add(new BasicNameValuePair("password", password));
//                params.add(new BasicNameValuePair("image_name", photoName));
                params.add(new BasicNameValuePair("image_name", username+".jpg"));
                params.add(new BasicNameValuePair("profile_pic", encodedImage));

                Log.i("params", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                Log.d("Login attempt. . . ", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Registration success!", json.toString());


                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Registration failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            // Launch Main Activity
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);

            // Registering user on our server
            // Sending registraiton details to OldMainActivity
            i.putExtra("name", firstname);
            i.putExtra("email", email);

            if(success == 1){
                startActivity(i);
                finish();
            }

            if (file_url != null) {
                Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_SHORT).show();
//                Log.d("Login result", file_url);
            }
        }
    }

    public class GetInstitutions extends AsyncTask<String, String, String> {

        JSONArray institutions = null;
        int success;
        Resources res = getResources();
        String GET_INSTITUTIONS_URL = res.getString(R.string.get_institutions_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setTitle("Fetching registered institutions . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(
                    GET_INSTITUTIONS_URL, "GET", params);

            Log.d("get result ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get seminars success!", json.toString());
                    institutions = json.getJSONArray(TAG_INSTITUTIONS);

                    for (int i = 0; i < institutions.length(); i++) {
                        JSONObject c = institutions.getJSONObject(i);

                        String id = c.getString(TAG_INSTI_ID);
                        String name = c.getString(TAG_INSTI_NAME);
                        instIdListArr.add(id);
                        instListArr.add(name);
                    }

                    Log.e("string array", instListArr.toString());

                }else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            spinnerArrayAdapter.notifyDataSetChanged();
            pDialog.dismiss();

            if (file_url != null) {
                Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_SHORT).show();

            }
        }

    }

}