package mad.geo.view.dialog;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AbstractTracking;
import mad.geo.model.MealEvent;
import mad.geo.service.TrackableService;

import static mad.geo.utils.DateHelper.dateToString;
import static mad.geo.utils.DateHelper.toDate;

/**
 * The dialog is used to add or edit tracking info
 * <p>
 * Activities that contain this fragment must implement the
 * {@link AddEditTrackingDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEditTrackingDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditTrackingDialog extends DialogFragment
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = AddEditTrackingDialog.class.getName();
    private int mTitle;
    private AbstractTrackable mTrackable;
    private OnFragmentInteractionListener mListener;
    private View mView;
    private int mTimeId;
    private TimePickerDialog timePicker;
    private AbstractTracking mTracking;
    private TrackableService trackableService;

    private TextView txtId;
    private EditText txtTitle;
    private TextView txtTrackableName;
    private Spinner spnLoc;
    private Spinner spnStartTime;
    private Spinner spnEndTime;
    private TextView txtMeetTime;

    public AddEditTrackingDialog() {
        // Required empty public constructor
    }


    public static AddEditTrackingDialog newInstance(AbstractTrackable trackable) {
        AddEditTrackingDialog trackingDialog = LazyHolder.INSTANCE;
        trackingDialog.mTrackable = trackable;
        trackingDialog.mTitle = R.string.add_tracking_dialog;
        return trackingDialog;
    }

    public static AddEditTrackingDialog newInstance(AbstractTracking tracking) {
        AddEditTrackingDialog trackingDialog = LazyHolder.INSTANCE;
        trackingDialog.mTitle = R.string.edit_tracking_dialog;
        trackingDialog.mTracking = tracking;
        return trackingDialog;
    }

    /**
     * Set the selected item for spinner
     *
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter();
        if (value == null || value.isEmpty())
            return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equals(adapter.getItem(i).toString())) {
                spinner.setSelection(i, true);
                break;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(mTitle);
        mView = inflater.inflate(R.layout.fragment_add_edit_tracking_dialog, container, false);
        trackableService = TrackableService.getSingletonInstance(mView.getContext());
        if (mTracking != null) {
            mTrackable = trackableService.getTrackableById(mTracking.getTrackableId());
        }
        setViews(mView);
        bindingItem();
        return mView;
    }

    /**
     * Set the view object from the UI.
     *
     * @param view
     */
    private void setViews(View view) {
        Button btnOk = (Button) view.findViewById(R.id.t_ok);
        Button btnCancel = (Button) view.findViewById(R.id.t_cancel);
        txtMeetTime = (TextView) view.findViewById(R.id.tr_meet_time_value);
        spnEndTime = (Spinner) view.findViewById(R.id.tr_end_time_value);
        spnStartTime = (Spinner) view.findViewById(R.id.tr_start_time_value);
        txtTrackableName = (TextView) view.findViewById(R.id.trackable_name_value);
        txtId = (TextView) view.findViewById(R.id.tr_id_value);
        spnLoc = (Spinner) view.findViewById(R.id.tr_loc_value);
        txtTitle = (EditText) view.findViewById(R.id.tr_title_value);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        txtMeetTime.setOnClickListener(this);
        setSpinnerAdapter(spnLoc, R.layout.spinner_location, trackableService.getLocations(mTrackable));
        setSpinnerAdapter(spnStartTime, R.layout.spinner_start_time, trackableService.getStartTimes(mTrackable));
        setSpinnerAdapter(spnEndTime, R.layout.spinner_end_time, trackableService.getEndTimes(mTrackable));

    }

    /**
     * Set resource and data source for spinner
     *
     * @param spinner
     * @param resId
     * @param dataSource
     * @param <T>
     */
    private <T> void setSpinnerAdapter(Spinner spinner, int resId, List<T> dataSource) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this.getContext(), resId, dataSource);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.spn_loc:
                String location = spnLoc.getSelectedItem().toString();
                List<String> startTimes = trackableService.getStartTimes(mTrackable, location);
                setSpinnerAdapter(spnStartTime, R.layout.spinner_start_time, startTimes);
                List<String> endTimes = trackableService.getEndTimes(mTrackable, location);
                setSpinnerAdapter(spnEndTime, R.layout.spinner_end_time, endTimes);
                break;
            case R.id.spn_start_time:
                //TODO: to be implemented
                break;
            case R.id.spn_end_time:
                //TODO: to be implemented
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }

    /**
     * Binding the object value to UI controls
     */
    private void bindingItem() {
        switch (mTitle) {
            case R.string.edit_tracking_dialog:
                if (mTracking != null) {
                    txtId.setText(mTracking.getTrackingId());
                    txtMeetTime.setText(mTracking.getMeetTimeStr());
                    txtTitle.setText(mTracking.getTitle());
                    setSpinnerItemSelectedByValue(spnLoc, mTracking.getMeetLocation());
                    setSpinnerItemSelectedByValue(spnStartTime, mTracking.getStartTimeStr());
                    setSpinnerItemSelectedByValue(spnEndTime, mTracking.getEndTimeStr());
                }
            case R.string.add_tracking_dialog:
                if (mTrackable != null) {
                    txtTrackableName.setText(mTrackable.getName());
                }
                break;
        }
    }

    public void onButtonPressed(AbstractTracking tracking) {
        if (mListener != null) {
            mListener.onFragmentInteraction(mTitle, tracking);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(final View view) {
        try {
            int trackableId = mTrackable.getId();
            String title = txtTitle.getText().toString();
            String loc = spnLoc.getSelectedItem().toString();
            String startTimeStr = spnStartTime.getSelectedItem().toString();
            String endTimeStr = spnEndTime.getSelectedItem().toString();
            Date startTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(startTimeStr);
            Date endTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(endTimeStr);
            Date meetTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(txtMeetTime.getText().toString());
            switch (view.getId()) {
                case R.id.t_ok:
                    if (startTime.compareTo(endTime) > 0) {
                        throw new Exception("the target end time should be later than the start time");
                    }
                    if (mTracking == null) {
                        mTracking = new MealEvent();
                    }
                    mTracking.setTitle(title);
                    mTracking.setTrackableId(trackableId);
                    mTracking.setMeetLocation(loc);
                    mTracking.setTarStartTime(startTime);
                    mTracking.setTarEndTime(endTime);
                    mTracking.setMeetTime(meetTime);
                    onButtonPressed(mTracking);
                    dismiss();
                    Toast.makeText(getContext(), "New Item added successfully.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.t_cancel:
                    getDialog().cancel();
                    break;
                case R.id.tr_meet_time_value:
                    mTimeId = view.getId();
                    long time = meetTime.getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(time));
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int min = calendar.get(Calendar.MINUTE);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), this, year, month, day);
                    datePickerDialog.show();
                    timePicker = new TimePickerDialog(view.getContext(), this, hour, min, false);
                    break;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(this.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        TextView txtTime = (TextView) mView.findViewById(mTimeId);
        Date date = new Date(year, month, day);
        txtTime.setText(dateToString(date));
        timePicker.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        TextView txtTime = (TextView) mView.findViewById(mTimeId);
        try {
            Date date = toDate(txtTime.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            txtTime.setText(dateToString(calendar.getTime()));
        } catch (ParseException e) {
            Log.i(LOG_TAG, e.getMessage());
        }


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int dialogType, AbstractTracking tracking);
    }

    private static class LazyHolder {
        static AddEditTrackingDialog INSTANCE = new AddEditTrackingDialog();
    }
}
