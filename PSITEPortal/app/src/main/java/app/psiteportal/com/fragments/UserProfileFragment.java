package app.psiteportal.com.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.Contents;
import app.psiteportal.com.utils.QRCodeEncoder;

public class UserProfileFragment extends Fragment {

	TextView user_name, institution, address, email, points;


	public UserProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String user_pid = bundle.getString("user_pid");
        String user_fname = bundle.getString("user_fname");
        String user_lname = bundle.getString("user_lname");
        String user_institution = bundle.getString("user_institution");
        String user_address = bundle.getString("user_address");
        String user_email = bundle.getString("user_email");
        int user_points = bundle.getInt("user_points");
        String user_usertype = bundle.getString("user_usertype");

        View rootView = inflater.inflate(R.layout.fragment_userprofile, container, false);

        String toQR = user_pid+","+user_fname+","+user_lname+","+user_institution+","+user_usertype;

        user_name = (TextView) rootView.findViewById(R.id.user_name);
        institution = (TextView) rootView.findViewById(R.id.user_institution);
        address = (TextView) rootView.findViewById(R.id.user_address);
        email = (TextView) rootView.findViewById(R.id.user_email);
        points = (TextView) rootView.findViewById(R.id.user_points);

        user_name.setText(user_fname + " " + user_lname);
        institution.setText(user_institution);
        address.setText(user_address);
        email.setText(user_email);
//        points.setText(user_points);



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
            ImageView myImage = (ImageView) rootView.findViewById(R.id.qr_imageview);
            myImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }



        return rootView;
    }
}
