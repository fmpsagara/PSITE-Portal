package app.psiteportal.com.fragments;


import android.app.Dialog;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


import java.util.Calendar;

/**
 * Created by eloisevincent on 9/26/2015.
 */
public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener timeSetListener; // listener object to get calling fragment listener
    TimePickerDialog myDatePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        timeSetListener = (TimePickerDialog.OnTimeSetListener) getTargetFragment(); // getting passed fragment
        myDatePicker = new TimePickerDialog(getActivity(),timeSetListener,hour,minute,false);
        // Create a new instance of DatePickerDialog and return it
        return myDatePicker;
    }
}
