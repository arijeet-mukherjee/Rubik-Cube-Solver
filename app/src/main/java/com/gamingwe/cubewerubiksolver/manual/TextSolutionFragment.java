package com.gamingwe.cubewerubiksolver.manual;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.gamingwe.cubewerubiksolver.R;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.ohoussein.playpause.PlayPauseView;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_BACK;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_DOWN;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_FRONT;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_LEFT;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_RIGHT;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.COLORS_INPUTTED_UP;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.INITIAL_INPUT_TYPE;
import static com.gamingwe.cubewerubiksolver.manual.ManualActivity.MANUAL_COLOR_INPUT;

public class TextSolutionFragment extends Fragment implements View.OnClickListener,
        EditScrambleDialog.EditScrambleDialogListener, SeekBar.OnSeekBarChangeListener {

private View rootView;
private CubeView cubeView;
private RipplePulseLayout mRipplePulseLayout;
private PlayPauseView playpauseButton;
private ImageButton btnLeftmove,btnRightmove;
private int flag=0;
private int flag2=0;
private int flag3=0;

private static final String SHOWCASE_ID = "material showcase cubeview";

    private String inputType;
private char[][][] allColorsInputted = new char[6][3][3];

public TextSolutionFragment() {

        }

@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_solution, container, false);
      //  mRipplePulseLayout= rootView.findViewById(R.id.layout_ripplepulse);

        return rootView;
        }

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cubeView = rootView.findViewById(R.id.cube_view);
        mRipplePulseLayout = rootView.findViewById(R.id.layout_ripplepulse2);
        mRipplePulseLayout.startRippleAnimation();
        rootView.findViewById(R.id.rewind).setOnClickListener(this);
        rootView.findViewById(R.id.skip_forward).setOnClickListener(this);
        SeekBar seekBar = rootView.findViewById(R.id.speed_adjuster);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(14);

    playpauseButton = (PlayPauseView) rootView.findViewById(R.id.play_pause_view);
    btnRightmove=(ImageButton)rootView.findViewById(R.id.btnRightmove);
    btnLeftmove=(ImageButton)rootView.findViewById(R.id.btnLeftmove);


    playpauseButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(flag%2==0){

            cubeView.startAnimation(1);}
            else{
                cubeView.stopAnimation();


            }
            playpauseButton.toggle();
            flag+=1;


        }
    });
    btnLeftmove.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cubeView.skipToPhase(cubeView.getPhase() - 1);

        }
    });
    btnRightmove.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cubeView.skipToPhase(cubeView.getPhase() + 1);
        }
    });







    ShowcaseConfig config = new ShowcaseConfig();
    config.setDelay(500);

    MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);
    sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
        @Override
        public void onShow(MaterialShowcaseView itemView, int position) {
            //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
        }
    });
    sequence.setConfig(config);


    sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(getActivity())
                    .setSkipText("SKIP")
                    .setTarget(cubeView)
                    .setDismissText("GOT IT")
                    .setContentText("This is where you can find current state of the cube after every steps performed. You can swipe left and right on this for previous and next move respectively")
                    .withRectangleShape(true)
                    .setContentTextColor(getResources().getColor(R.color.gradient_color_2))
                    .setMaskColour(getResources().getColor(R.color.gradient_color_1))
                    .build()
    );
    sequence.start();

        Bundle args = getArguments();
        inputType = (args != null) ? args.getString(INITIAL_INPUT_TYPE) : " ";
        if (inputType != null && inputType.equals(MANUAL_COLOR_INPUT)) {
        try {
        cubeView.resetScrambleByColorInputs(allColorsInputted);

        TextView scrambleView = rootView.findViewById(R.id.scramble_view);
        scrambleView.setText("");
        } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(getContext(), "Please make valid inputs", Toast.LENGTH_LONG)
        .show();
        }
        }
        }

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (inputType.equals(MANUAL_COLOR_INPUT)) {
        ((Activity) getContext()).getMenuInflater().inflate(R.menu.menu_color_solution, menu);
        } else {
        ((Activity) getContext()).getMenuInflater().inflate(R.menu.menu_text_solution, menu);
        }
        }

@Override
public void onDetach() {
        super.onDetach();
        }

public void updateMoves(String movesToPerform, String movesPerformed, String phase) {
        TextView toPerformView = rootView.findViewById(R.id.moves_to_perform);
        toPerformView.setText(movesToPerform);
        TextView performedView = rootView.findViewById(R.id.moves_performed);
        performedView.setText(movesPerformed);
        TextView phaseView = rootView.findViewById(R.id.phase_view);
        phaseView.setText(phase);

        }

@Override
public void onClick(View view) {

        switch (view.getId()) {
        case R.id.skip_forward:
        cubeView.skipToPhase(cubeView.getPhase() + 1);
        break;
        case R.id.rewind:
        cubeView.skipToPhase(cubeView.getPhase() - 1);
        break;
        }
        }

@Override
public void onDialogPositiveClick(DialogFragment dialog, String scramble) {
        TextView scrambleView = rootView.findViewById(R.id.scramble_view);
        scrambleView.setText(scramble);
        cubeView.resetScramble(scramble);
        }

@Override
public void onDialogNegativeClick(DialogFragment dialog) {

        }

@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (cubeView.getAnimationStopped()) {
        cubeView.setSpeedMultiplier(1 + progress); //Avoid division by 0, min is 1
        } else {
        cubeView.stopAnimation();

        cubeView.startAnimation(1 + progress);

        }
       // if(fromUser){mRipplePulseLayout.stopRippleAnimation();}
        }

@Override
public void onStartTrackingTouch(SeekBar seekBar) {

        }

@Override
public void onStopTrackingTouch(SeekBar seekBar) {

        }

@Override
public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_random:
        TextView scrambleView = rootView.findViewById(R.id.scramble_view);
        scrambleView.setText(cubeView.randScramble());
        break;
        case R.id.action_reset:
        cubeView.resetCurrentScramble();
        break;
        case R.id.action_edit_scramble:
        if (inputType.equals(MANUAL_COLOR_INPUT)) {
        getActivity().getSupportFragmentManager().popBackStack();
        } else {
        Bundle args = new Bundle();
        args.putString(EditScrambleDialog.SCRAMBLE_TAG,
        ((TextView) rootView.findViewById(R.id.scramble_view)).getText().toString());
        EditScrambleDialog dialog = new EditScrambleDialog();
        dialog.setArguments(args);
        dialog.show(((AppCompatActivity) getContext())
        .getSupportFragmentManager(), "edit scramble");
        break;
        }
        }
        return super.onOptionsItemSelected(item);
        }

@Override
public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
        String initInputType = args.getString(INITIAL_INPUT_TYPE);
        if (initInputType != null && initInputType.equals(MANUAL_COLOR_INPUT)) {
        allColorsInputted[0] = unpackArrays(args.getCharArray(COLORS_INPUTTED_LEFT));
        allColorsInputted[1] = unpackArrays(args.getCharArray(COLORS_INPUTTED_UP));
        allColorsInputted[2] = unpackArrays(args.getCharArray(COLORS_INPUTTED_FRONT));
        allColorsInputted[3] = unpackArrays(args.getCharArray(COLORS_INPUTTED_BACK));
        allColorsInputted[4] = unpackArrays(args.getCharArray(COLORS_INPUTTED_RIGHT));
        allColorsInputted[5] = unpackArrays(args.getCharArray(COLORS_INPUTTED_DOWN));
        }
        }
        }

private char[][] unpackArrays(char[] colorsArray) {
        char[][] unpackedArray = new char[3][3];
        for (int i = 0; i < colorsArray.length; i++) {
        unpackedArray[i / 3][i % 3] = colorsArray[i];
        }
        return unpackedArray;
        }



        }
