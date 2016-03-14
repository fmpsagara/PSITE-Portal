package app.psiteportal.com.utils;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.Date;

/**
 * Created by fmpdroid on 2/8/2016.
 */
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private int has_voted = 0;
    private int has_nominated = 0;
    private int num_positions_needed = 0;
    private String electionDate, electionStartTime, electionEndTime;
    private String nominationDate, nominationStartTime, nominationEndTime;
    private int indicator = 0;
    private int nomination_sid;
    private String GCM_registration_id;
    private int activated;

    //private boolean

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public String getGCM_registration_id() {
        return GCM_registration_id;
    }

    public void setGCM_registration_id(String GCM_registration_id) {
        this.GCM_registration_id = GCM_registration_id;
    }

    public int getNomination_sid() {
        return nomination_sid;
    }

    public void setNomination_sid(int nomination_sid) {
        this.nomination_sid = nomination_sid;
    }

    public int getIndicator() {
        return indicator;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public String getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(String electionDate) {
        this.electionDate = electionDate;
    }

    public String getElectionStartTime() {
        return electionStartTime;
    }

    public void setElectionStartTime(String electionStartTime) {
        this.electionStartTime = electionStartTime;
    }

    public String getElectionEndTime() {
        return electionEndTime;
    }

    public void setElectionEndTime(String electionEndTime) {
        this.electionEndTime = electionEndTime;
    }

    public String getNominationEndTime() {
        return nominationEndTime;
    }

    public void setNominationEndTime(String nominationEndTime) {
        this.nominationEndTime = nominationEndTime;
    }

    public String getNominationStartTime() {
        return nominationStartTime;
    }

    public void setNominationStartTime(String nominationStartTime) {
        this.nominationStartTime = nominationStartTime;
    }

    public String getNominationDate() {
        return nominationDate;
    }

    public void setNominationDate(String nominationDate) {
        this.nominationDate = nominationDate;
    }

    public void set_has_voted(int has_voted){
        this.has_voted = has_voted;
    }

    public int get_has_voted(){
        return has_voted;
    }

    public void set_has_nominated(int has_nominated){
        this.has_nominated = has_nominated;
    }

    public int get_has_nominated(){
        return has_nominated;
    }

    public int getNum_positions_needed() {
        return num_positions_needed;
    }

    public void setNum_positions_needed(int num_positions_needed) {
        this.num_positions_needed = num_positions_needed;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
