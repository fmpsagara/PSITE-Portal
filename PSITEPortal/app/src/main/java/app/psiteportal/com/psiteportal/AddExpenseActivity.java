package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 9/26/2015.
 */
public class AddExpenseActivity extends AppCompatActivity {

    private int year, month, day;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String IMAGE_DIRECTORY_NAME = "PSITE Portal";
    Button uploadBtn, save_expense;
    TextView date;
    ImageView imgPreview;
    EditText item;
    EditText amount;
    EditText check_number;
    Spinner liq_type;
    ProgressDialog pDialog;
    String encodedImage;
    String pid;
    String sid;
    CheckBox cash_check;
    static String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            pid = extras.getString("pid");
            sid = extras.getString("sid");
        }

        item = (EditText) findViewById(R.id.item_et);
        amount = (EditText) findViewById(R.id.amount_et);
        check_number = (EditText) findViewById(R.id.cash_num_et);
        date = (TextView) findViewById(R.id.date_text);
        imgPreview = (ImageView) findViewById(R.id.expense_image_preview);
        uploadBtn = (Button) findViewById(R.id.upload_btn);
        save_expense = (Button) findViewById(R.id.expense_save_btn);
        liq_type = (Spinner) findViewById(R.id.spinner);
        cash_check = (CheckBox) findViewById(R.id.check_cb);

        liq_type.setSelection(0);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));


        cash_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    check_number.setEnabled(true);
                }else{
                    check_number.setEnabled(false);
                }
            }
        });



        save_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddExpense().execute();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera.",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }


    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //success captured image
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                //cancelled
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            imgPreview.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            uploadBtn.setText("Change");

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.e(IMAGE_DIRECTORY_NAME, "Failed creating " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile=null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

            fileName = "IMG_" + timeStamp + ".jpg";
        }

        return mediaFile;
    }

    //date dialog methods ------------------------------------------------------------


    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class AddExpense extends AsyncTask<String, String, String>{

        int success;
        String message;
        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ADD_EXPENSE_URL = res.getString(R.string.add_expense_url);
        String dateValue =  date.getText().toString();
        String itemValue = item.getText().toString();
        String amountValue = amount.getText().toString();
        String checkValue = check_number.getText().toString();
        String transType = String.valueOf(liq_type.getSelectedItem());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddExpenseActivity.this);
            pDialog.setMessage("Uploading transaction. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("pid", pid));
                params.add(new BasicNameValuePair("transaction_item", itemValue));
                params.add(new BasicNameValuePair("transaction_amount", amountValue));
                params.add(new BasicNameValuePair("check_number", checkValue));
                params.add(new BasicNameValuePair("image_name", fileName));
                params.add(new BasicNameValuePair("transaction_photo", encodedImage));
                params.add(new BasicNameValuePair("transaction_type", transType));
                params.add(new BasicNameValuePair("transaction_date", dateValue));

                Log.e("params to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(ADD_EXPENSE_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if(success == 1){
//                    Toast.makeText(AddExpenseActivity.this, json.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    message = json.getString(TAG_MESSAGE);
//                    finish();
                }else{
//                    Toast.makeText(AddExpenseActivity.this, json.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    message = json.getString(TAG_MESSAGE);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if(success == 1){
                Toast.makeText(AddExpenseActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
