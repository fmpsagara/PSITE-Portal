package app.psiteportal.com.psiteportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.CircleImageView;

public class ScrollingElectionFragment extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView txtName;
    private TextView txtInstitution;
    private TextView txtContact;
    private TextView txtEmail;
    private TextView txtAddress;
    private Bitmap bitmap;
    String imageUrl, name, institution, contact, email, address;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.activity_scrolling_election, container, false);
//        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
//        imageView = (ImageView)rootView.findViewById(R.id.prof_image);
//        txtInstitution = (TextView)rootView.findViewById(R.id.info_institution);
//        txtContact = (TextView)rootView.findViewById(R.id.info_contact);
//        txtEmail = (TextView)rootView.findViewById(R.id.info_email);
//        txtAddress = (TextView)rootView.findViewById(R.id.info_location);
//
//        Bundle bundle = this.getArguments();
//        imageUrl = bundle.getString("prof_pic");
//        name = bundle.getString("name");
//        institution = bundle.getString("institution");
//        contact = bundle.getString("contact");
//        email = bundle.getString("email");
//        address = bundle.getString("address");
//
//        toolBarLayout.setTitle(name);
//        imageView.setImageBitmap(bitmap);
//        txtInstitution.setText(institution);
//        txtContact.setText(contact);
//        txtEmail.setText(email);
//        txtAddress.setText(address);
//
//
//        return rootView;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_election);


        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        imageView = (CircleImageView) findViewById(R.id.prof_image);
        txtInstitution = (TextView)findViewById(R.id.info_institution);
        txtContact = (TextView)findViewById(R.id.info_contact);
        txtEmail = (TextView)findViewById(R.id.info_email);
        txtAddress = (TextView)findViewById(R.id.info_location);

        Intent intent = getIntent();

//        byte[] bytes = intent.getByteArrayExtra("prof_pic");
//        bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageUrl = intent.getStringExtra("prof_pic");
        name = intent.getStringExtra("name");
        institution = intent.getStringExtra("institution");
        contact = intent.getStringExtra("contact");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");

        toolBarLayout.setTitle(name);
        imageView.setImageUrl(imageUrl, imageLoader);
        txtInstitution.setText(institution);
        txtContact.setText(contact);
        txtEmail.setText(email);
        txtAddress.setText(address);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK))
//        {
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
