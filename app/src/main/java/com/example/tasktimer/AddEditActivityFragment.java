package com.example.tasktimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

*
 * A placeholder fragment containing a simple view.


public class AddEditActivityFragment extends Fragment {
    public static final String TAG = "AddEditActivityFragment";

    private enum FragmentEditMode { EDIT, ADD }
    private FragmentEditMode mMode;

    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private final ThreadLocal<Button> mSaveButton = new ThreadLocal<Button>();
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked{
        void onSaveClicked();
    }

    public AddEditActivityFragment() {

        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    public boolean canClose(){
        return false;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        //Activities containing this fragment must implement it's callbacks.
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName()
                  + " must implement AddEditActivityFragment.OnSaveClicked interface");
        }
        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameTextView = view.findViewById(R.id.addedit_name);
        mDescriptionTextView = view.findViewById(R.id.addedit_description);
        mSortOrderTextView = view.findViewById(R.id.addedit_sortorder);
        mSaveButton.set((Button) view.findViewById(R.id.addedit_save));

        Bundle arguments = getArguments();

        final Task task;
        if (arguments != null){
            Log.d(TAG, "onCreateView: retrieiving task details.");

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null){
                Log.d(TAG, "onCreateView: Task details found editing...");
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            }else {
                // No task, so we must be adding a new task, and not editing an existing one
mMode = FragmentEditMode.ADD;

            }
        }else {
            task = null;
            Log.d(TAG, "onCreateView: No argumants, adding new record");
            mMode = FragmentEditMode.ADD;
        }

        mSaveButton.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the database if at least one field has changed
                //There's no need to hit the database unless this has happened.
                int so; //to save repeated conversions to int.
                if (mSortOrderTextView.length() > 0){
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                }else {
                    so = 0;
                }
                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode){
                    case EDIT:

                        if (task == null){
                            // remove lint warnings, will never execute
                            break;
                        }

                        if (!mNameTextView.getText().toString().equals(task.getName())){
                            values.put(TaskContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }

                        if (!mDescriptionTextView.getText().toString().equals(task.getDescription())){
                            values.put(TaskContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }

                        if (so != task.getSortOrder()){
                            values.put(TaskContract.Columns.TASKS_SORTORDER, so);
                        }

                        if (values.size() != 0){
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TaskContract.buildTaskUri(task.getid()), values, null, null);
                        }
                        break;

                    case ADD:

                        if (mNameTextView.length() > 0){
                            Log.d(TAG, "onClick: adding new task");
                            values.put(TaskContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TaskContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TaskContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TaskContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done editing");

                if (mSaveListener != null){
                    mSaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");

        return view;
    }
}
