package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.model.Announcement;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Contents;
import app.psiteportal.com.utils.QRCodeEncoder;

/**
 * Created by fmpdroid on 2/29/2016.
 */
public class QRFragment extends Fragment{
    private String user_pid;
    private String url = "http://www.psite7.org/portal/webservices/get_user.php";
    private ImageView myImage;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);
        myImage = (ImageView) rootView.findViewById(R.id.qr_imageview);

        Bundle bundle = getArguments();
        user_pid = bundle.getString("user_pid");
        getQR();
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        return rootView;
    }

    private void getQR() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                hidePDialog();
                String e_pid, e_username, e_name, e_email, e_institution, e_contact, e_address, e_points, prof_pic;
                int has_voted;
                SimpleDateFormat formatter;
                try {
                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    JSONObject object = new JSONObject(s.toString());
                    Log.wtf("testing", object.getString("prof_pic"));
                    e_pid = object.getString("pid");
                    e_name = object.getString("firstname") + " " + object.getString("lastname");
                    e_email = object.getString("email");
                    e_institution = object.getString("institution");
                    e_contact = object.getString("contact");
                    e_address = object.getString("address");
                    e_points = object.getString("points");
                    prof_pic = object.getString("prof_pic");

                    String toQR = e_pid + ", " + e_name + ", " + e_email + ", " + e_institution;

                    WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3/4;

                    //Encode with a QR Code image

                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(toQR,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(),
                            smallerDimension);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        myImage.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.wtf("qr!", e.getMessage());
                    }

                } catch (Exception e) {
                    Log.wtf("testing", e.getMessage());
                    Log.wtf("HEREEE", s.substring(0));
                }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("pid", user_pid);
                return params;
            }
        };queue.add(sr);
    }
    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
