package app.psiteportal.com.model;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private int icon;/* */
    private String title;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, int icon/* */, String title) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon = icon;/* */
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
