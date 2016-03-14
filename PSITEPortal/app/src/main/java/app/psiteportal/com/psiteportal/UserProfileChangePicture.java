package app.psiteportal.com.psiteportal;

/**
 * Created by fmpdroid on 3/1/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileChangePicture extends AppCompatActivity {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_prof_pic);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

    }

    /**
     * On load image button click, start pick image chooser activity.
     */
    public void onLoadImageClick(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }

    /**
     * Crop the image and set it back to the cropping view.
     */
    public void onCropImageClick(View view) {
        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        byte[] imageBytes = bytes.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        upload(encodedImage);

        if (cropped != null)
            mCropImageView.setImageBitmap(cropped);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void upload(final String uploadImage){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.psite7.org/portal/webservices/change_pic.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("utot", s.substring(0));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("image", uploadImage);
                params.put("username", username);
                return params;
            }
        };queue.add(sr);
    }

//public class UserProfileChangePicture extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener {
//
//    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
//
//    private static final int ROTATE_NINETY_DEGREES = 90;
//
//    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
//
//    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
//
//    private static final int ON_TOUCH = 1;
//
//    private CropImageView mCropImageView;
//
//    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
//
//    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
//
//    Bitmap croppedImage;
//
//    private Uri mCropImageUri;
//
//    @Override
//    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
//        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
//    }
//
//    // Restores the state upon rotating the screen/restarting the activity
//    @Override
//    protected void onRestoreInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
//        super.onRestoreInstanceState(bundle);
//        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
//        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.change_prof_pic);
//
//        mCropImageView = (CropImageView) findViewById(R.id.crop_image);
//        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
//        mCropImageView.setGuidelines(CropImageView.Guidelines.ON_TOUCH);
//
//        if (savedInstanceState == null) {
//            mCropImageView.setImageResource(R.mipmap.ic_launcher);
//        }
//
//        final Button rotateButton = (Button) findViewById(R.id.Button_rotate);
//        rotateButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
//            }
//        });
//
//        findViewById(R.id.Button_crop).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                mCropImageView.getCroppedImageAsync(mCropImageView.getCropShape());
//                mCropImageView.getCroppedImageAsync(mCropImageView.getCropShape(), 0, 0);
//            }
//        });
//
//        findViewById(R.id.Button_load).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(getPickImageChooserIntent(), 200);
//            }
//        });
//
//    }
//
//    @Override
//    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
//        if (error == null) {
//            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
//        } else {
//            Log.e("AIC", "Failed to load image by URI", error);
//            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
//        if (error == null) {
//            croppedImage = bitmap;
//            ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
//            croppedImageView.setImageBitmap(croppedImage);
//        } else {
//            Log.e("AIC", "Failed to crop image", error);
//            Toast.makeText(this, "Image crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public Intent getPickImageChooserIntent() {
//
//        // Determine Uri of camera image to save.
//        Uri outputFileUri = getCaptureImageOutputUri();
//
//        List<Intent> allIntents = new ArrayList<>();
//        PackageManager packageManager = getPackageManager();
//
//        // collect all camera intents
//        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for (ResolveInfo res : listCam) {
//            Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            }
//            allIntents.add(intent);
//        }
//
//        // collect all gallery intents
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
//        for (ResolveInfo res : listGallery) {
//            Intent intent = new Intent(galleryIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            allIntents.add(intent);
//        }
//
//        // the main intent is the last in the list (fucking android) so pickup the useless one
//        Intent mainIntent = allIntents.get(allIntents.size() - 1);
//        for (Intent intent : allIntents) {
//            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
//                mainIntent = intent;
//                break;
//            }
//        }
//        allIntents.remove(mainIntent);
//
//        // Create a chooser from the main intent
//        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
//
//        // Add all other intents
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
//
//        return chooserIntent;
//    }
//
//    /**
//     * Get URI to image received from capture by camera.
//     */
//    private Uri getCaptureImageOutputUri() {
//        Uri outputFileUri = null;
//        File getImage = getExternalCacheDir();
//        if (getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
//        }
//        return outputFileUri;
//    }
//
//    public Uri getPickImageResultUri(Intent data) {
//        boolean isCamera = true;
//        if (data != null && data.getData() != null) {
//            String action = data.getAction();
//            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
//        }
//        return isCamera ? getCaptureImageOutputUri() : data.getData();
//    }
//
//    /**
//     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
//     */
//    public boolean isUriRequiresPermissions(Uri uri) {
//        try {
//            ContentResolver resolver = getContentResolver();
//            InputStream stream = resolver.openInputStream(uri);
//            stream.close();
//            return false;
//        } catch (FileNotFoundException e) {
//            if (e.getCause() instanceof ErrnoException) {
//                return true;
//            }
//        } catch (Exception e) {
//        }
//        return false;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            Uri imageUri = getPickImageResultUri(data);
//
//            // For API >= 23 we need to check specifically that we have permissions to read external storage,
//            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
//            boolean requirePermissions = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
//                    isUriRequiresPermissions(imageUri)) {
//
//                // request permissions and handle the result in onRequestPermissionsResult()
//                requirePermissions = true;
//                mCropImageUri = imageUri;
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            }
//
//            if (!requirePermissions) {
//                ((CropImageView) findViewById(R.id.crop_image)).setImageUriAsync(imageUri);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            ((CropImageView) findViewById(R.id.crop_image)).setImageUriAsync(mCropImageUri);
//        } else {
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mCropImageView.setOnSetImageUriCompleteListener(this);
//        mCropImageView.setOnGetCroppedImageCompleteListener(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mCropImageView.setOnSetImageUriCompleteListener(null);
//        mCropImageView.setOnGetCroppedImageCompleteListener(null);
//    }
//

//end file





    //    private Uri mImageCaptureUri;
//    private ImageView mImageView;
//
//    private static final int PICK_FROM_CAMERA = 1;
//    private static final int CROP_FROM_CAMERA = 2;
//    private static final int PICK_FROM_FILE = 3;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
//
//        final String [] items			= new String [] {"Take from camera", "Select from gallery"};
//        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
//        AlertDialog.Builder builder		= new AlertDialog.Builder(this);
//
//        builder.setTitle("Select Image");
//        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
//            public void onClick( DialogInterface dialog, int item ) { //pick from camera
//                if (item == 0) {
//                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
//                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
//
//                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//
//                    try {
//                        intent.putExtra("return-data", true);
//
//                        startActivityForResult(intent, CROP_FROM_CAMERA);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                } else { //pick from file
//                    Intent intent = new Intent();
//
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
//                }
//            }
//        } );
//
//        final AlertDialog dialog = builder.create();
//
//        Button button 	= (Button) findViewById(R.id.btn_crop);
//        mImageView		= (ImageView) findViewById(R.id.iv_photo);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
