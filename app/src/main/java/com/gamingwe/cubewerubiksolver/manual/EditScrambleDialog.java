package com.gamingwe.cubewerubiksolver.manual;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.gamingwe.cubewerubiksolver.R;

public class EditScrambleDialog extends DialogFragment {

    public static final String SCRAMBLE_TAG = "scramble_tag";
    EditScrambleDialogListener editScrambleDialogListener;
    String scramble = "";

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        scramble = args.getString(SCRAMBLE_TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        String currentScramble = getArguments().getString(SCRAMBLE_TAG);
        final View view = inflater.inflate(R.layout.edit_scramble_dialog_body, null);
        ((EditText) view.findViewById(R.id.scramble_editor)).setText(currentScramble);

        builder.setView(view)
                .setTitle("Edit Scramble")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        editScrambleDialogListener.onDialogPositiveClick(EditScrambleDialog.this,
                                ((EditText) view.findViewById(R.id.scramble_editor)).getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editScrambleDialogListener.onDialogNegativeClick(EditScrambleDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            editScrambleDialogListener = (EditScrambleDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement EditScrambleDialogListener");
        }
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface EditScrambleDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String scramble);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
