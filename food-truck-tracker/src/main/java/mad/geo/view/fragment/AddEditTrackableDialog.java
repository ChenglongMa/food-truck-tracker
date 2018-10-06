package mad.geo.view.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.FoodTruck;

//import android.support.v4.app.DialogFragment;

/**
 * Activities that contain this fragment must implement the
 * {@link AddEditTrackableDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEditTrackableDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditTrackableDialog extends DialogFragment implements View.OnClickListener {
    private static final String ARG_TITLE = "title";
    private int mTitle;
    private AbstractTrackable mItem;
    private OnFragmentInteractionListener mListener;

    public AddEditTrackableDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title     The title to be displayed.
     * @param trackable The trackable to be handled.
     * @return A new instance of fragment AddEditTrackableDialog.
     */
    public static AddEditTrackableDialog newInstance(int title, AbstractTrackable trackable) {
        AddEditTrackableDialog fragment = new AddEditTrackableDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        fragment.mItem = trackable;
        return fragment;
    }

    private void bindingItem(View view, AbstractTrackable trackable) {
        if (trackable == null) {
            return;
        }
        EditText txtId = ((EditText) view.findViewById(R.id.t_id_value));
        txtId.setText(trackable.getIdString());
        txtId.setEnabled(false);
        ((EditText) view.findViewById(R.id.t_name_value)).setText(trackable.getName());
        ((EditText) view.findViewById(R.id.t_category_value)).setText(trackable.getCategory());
        ((EditText) view.findViewById(R.id.t_url_value)).setText(trackable.getUrl());
        ((EditText) view.findViewById(R.id.t_description_value)).setText(trackable.getDescription());
        view.setTag(trackable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(mTitle);
        View view = inflater.inflate(R.layout.fragment_add_edit_trackable_dialog, container, false);
        Button btnOk = (Button) view.findViewById(R.id.t_ok);
        Button btnCancel = (Button) view.findViewById(R.id.t_cancel);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        bindingItem(view, mItem);
        return view;
    }

    public void onButtonPressed(AbstractTrackable trackable) {
        if (mListener != null) {
            mListener.onFragmentInteraction(mTitle, trackable);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getInt(ARG_TITLE);
        }

    }

    @Override
    public void onClick(View view) {
        try {
            View rootView = view.getRootView();
            switch (view.getId()) {
                case R.id.t_ok:
                    EditText txtId = (EditText) rootView.findViewById(R.id.t_id_value);
                    int id = Integer.parseInt(txtId.getText().toString());
                    EditText txtName = (EditText) rootView.findViewById(R.id.t_name_value);
                    String name = txtName.getText().toString();
                    EditText txtCate = (EditText) rootView.findViewById(R.id.t_category_value);
                    String cate = txtCate.getText().toString();
                    EditText txtUrl = (EditText) rootView.findViewById(R.id.t_url_value);
                    String url = txtUrl.getText().toString();
                    EditText txtDes = (EditText) rootView.findViewById(R.id.t_description_value);
                    String des = txtDes.getText().toString();
                    AbstractTrackable trackable = new FoodTruck();
                    trackable.setId(id);
                    trackable.setName(name);
                    trackable.setCategory(cate);
                    trackable.setUrl(url);
                    trackable.setDescription(des);
                    onButtonPressed(trackable);
                    dismiss();
                    Toast.makeText(getContext(), "Item added/edited successfully", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.t_cancel:
                    getDialog().cancel();
                    break;
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int dialogType, AbstractTrackable trackable);
    }
}
