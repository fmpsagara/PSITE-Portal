package app.psiteportal.com.model;

import android.graphics.Bitmap;
import android.widget.CheckBox;

/**

 * Created by fmpdroid on 1/28/2016.
 */
public class Nominee{

    private String imageUrl, name, institution, contact, email, address;
    private Bitmap bitmap;
    private boolean isSelected;
    public Nominee(){
    }

    public Nominee(String imageUrl, String name, String institution, String contact, String email, String address) {
        this.institution = institution;
        this.imageUrl = imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }
    public Nominee(String imageUrl, String name, String institution, String contact, String email, String address, boolean isSelected) {
        this.institution = institution;
        this.imageUrl = imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.isSelected = isSelected;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}