package com.gamingwe.cubewerubiksolver.manual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gamingwe.cubewerubiksolver.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CubeView extends View  {

public final static int DELAY = 1500;
//Default scramble
private final String DEFAULT_SCRAMBLE = getResources().getString(R.string.default_scramble);
        TextView toPerformView, performedView;
private Timer frameTimer;
private TimerTask animationTask;
private boolean animationStopped;
private int speedMultiplier;
private Cube cube = new Cube();
private boolean hasColorInput;
private char[][][] colorsInputted;
private String scramble = DEFAULT_SCRAMBLE,
        sunflower = "",
        whiteCross = "",
        whiteCorners = "",
        secondLayer = "",
        yellowCross = "",
        OLL = "",
        PLL = "";
private String[] moveSet;
private String movesToPerform = "",
        movesPerformed = "";

    private int size;
    private int velocitySlow, velocityNormal;
    private List<Bitmap> confettoBitmaps;

/*
 * Respective stages of the solution w.r.t the phase variable
 * 0 = sunflower
 * 1 = whiteCross
 * 2 = whiteCorners
 * 3 = secondLayer
 * 4 = yellowCross
 * 5 = OLL
 * 6 = PLL
 * The phase is updated in updatePhase() to reflect the stage at which the solution is
 */
private int phase = 0;
private  String phaseString;
//Helps keep track of moves to perform, and allows for painting of moves
private int movesIndex = 0;
private Paint bluePaint;
private Paint greenPaint;
private Paint redPaint;
private Paint orangePaint;
private Paint whitePaint;
private Paint yellowPaint;
private Paint strokePaint;
private int cubieSize;
private int gap;

public CubeView(Context context) {
        super(context);
        setWillNotDraw(false);
        init();
        }

public CubeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init();
        }

public CubeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        init();
        }

public CubeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init();
        }

private void init() {
        cube = new Cube();
        moveSet = new String[8];
        resetScramble(scramble);

        Context context = getContext();
        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(ContextCompat.getColor(context, R.color.cubeBlue));

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setColor(ContextCompat.getColor(context, R.color.cubeGreen));

        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(ContextCompat.getColor(context, R.color.cubeRed));

        orangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setColor(ContextCompat.getColor(context, R.color.cubeOrange));

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(ContextCompat.getColor(context, R.color.cubeWhite));

        yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setStyle(Paint.Style.FILL);
        yellowPaint.setColor(ContextCompat.getColor(context, R.color.cubeYellow));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(6);

        speedMultiplier = 1;
        cubieSize = (int) dpToPx(22);
        gap = (int) dpToPx(4);
        animationStopped = true; //Wait until the user clicks on the cube to start animation

        setOnTouchListener(new OnTouchListener() {

private final int MIN_DRAG_DISTANCE = 15;
private final int MIN_DRAG_TIME = 1000;
private long initTime;
private float initX;
private float initY;

@Override
public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {

        case MotionEvent.ACTION_DOWN:
        initTime = System.currentTimeMillis();
        initX = motionEvent.getX();
        initY = motionEvent.getY();
        break;

        case MotionEvent.ACTION_UP:
        float finalX = motionEvent.getX(), finalY = motionEvent.getY();
        long eventTime = System.currentTimeMillis() - initTime;
        int distance = (int) pxToDp(distance(initX, initY, finalX, finalY));
        if (eventTime >= MIN_DRAG_TIME || distance >= MIN_DRAG_DISTANCE) {
        if (finalX > initX + dpToPx(MIN_DRAG_DISTANCE)) { //Swipe to the right
        if (phase < 7) { //Only do stuff if the solve is not complete yet
        performNextMove();
        invalidate();

        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }
        } else if (initX > finalX + dpToPx(MIN_DRAG_DISTANCE)) { //Swipe to the left
        boolean flag = false;
        int prevIndex = movesIndex;
        while (movesIndex > 1 && !flag) {
        movesIndex--;
        if (movesToPerform.substring(movesIndex - 1, movesIndex).equals(" ")) {
        flag = !flag;
        }
        }
        if (movesIndex == 1) {
        movesIndex = 0;
        }
        movesPerformed = movesToPerform.substring(0, movesIndex);
        if (movesPerformed.length() >= 35) {
        movesPerformed = movesPerformed.substring(movesPerformed.length() - 33);
        }
        cube.reverseMoves(movesToPerform.substring(movesIndex, prevIndex));
        invalidate();


        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }
        } else { //Just a click
        if (animationStopped && phase < 7) {
        startAnimation(speedMultiplier);
        } else if (!animationStopped) {
        stopAnimation();
        }
        }

        break;
        }
        return true;
        }

private float distance(float initX, float initY, float finalX, float finalY) {
        float rise = finalY - initY;
        float run = finalX - initX;
        return (float) Math.sqrt(run * run + rise * rise);
        }

private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
        }

        });

        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }

@Override
protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Paint Reds
        int xVal = gap;
        int yVal = (cubieSize + gap) * 3 + gap * 3;
        for (int y = 2; y >= 0; y--) {
        for (int z = 2; z >= 0; z--) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * Math.abs(z - 2),
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * Math.abs(z - 2) + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(0, y, z, 'L')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * Math.abs(z - 2),
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * Math.abs(z - 2) + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        //Paint Yellows
        xVal += (cubieSize + gap) * 3 + gap;
        for (int x = 0; x <= 2; x++) {
        for (int y = 2; y >= 0; y--) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(x, y, 0, 'U')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        //Paint Blues
        yVal -= (cubieSize + gap) * 3 + gap;
        for (int x = 0; x <= 2; x++) {
        for (int z = 2; z >= 0; z--) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * Math.abs(z - 2),
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(z - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(x, 2, z, 'B')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * Math.abs(z - 2),
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(z - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        //Paint Greens
        yVal += ((cubieSize + gap) * 3 + gap) * 2;
        for (int z = 0; z <= 2; z++) {
        for (int x = 0; x <= 2; x++) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * z,
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * z + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(x, 0, z, 'F')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * x,
        yVal + (cubieSize + gap) * z,
        xVal + (cubieSize + gap) * x + cubieSize,
        yVal + (cubieSize + gap) * z + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        //Paint Oranges
        xVal += (cubieSize + gap) * 3 + gap;
        yVal -= (cubieSize + gap) * 3 + gap;
        for (int y = 2; y >= 0; y--) {
        for (int z = 0; z <= 2; z++) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * z,
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * z + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(2, y, z, 'R')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * z,
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * z + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        //Paint Whites
        xVal += (cubieSize + gap) * 3 + gap;
        for (int x = 2; x >= 0; x--) {
        for (int y = 2; y >= 0; y--) {
        canvas.drawRoundRect(xVal + (cubieSize + gap) * Math.abs(x - 2),
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * Math.abs(x - 2) + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        colorToPaint(cube.getColor(x, y, 2, 'D')));

        canvas.drawRoundRect(xVal + (cubieSize + gap) * Math.abs(x - 2),
        yVal + (cubieSize + gap) * Math.abs(y - 2),
        xVal + (cubieSize + gap) * Math.abs(x - 2) + cubieSize,
        yVal + (cubieSize + gap) * Math.abs(y - 2) + cubieSize,
        cubieSize / 5,
        cubieSize / 5,
        strokePaint);
        }
        }

        }

private Paint colorToPaint(char color) {
        switch (color) {
        case 'W':
        return whitePaint;
        case 'Y':
        return yellowPaint;
        case 'R':
        return redPaint;
        case 'O':
        return orangePaint;
        case 'B':
        return bluePaint;
        case 'G':
        return greenPaint;
        }
        return strokePaint;
        }

private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        getResources().getDisplayMetrics());
        }

public void resetScramble(String s) {
        scramble = s;
        cube = new Cube();
        cube.scramble(scramble);
        sunflower = cube.makeSunflower();
        whiteCross = cube.makeWhiteCross();
        whiteCorners = cube.finishWhiteLayer();
        secondLayer = cube.insertAllEdges();
        yellowCross = cube.makeYellowCross();
        OLL = cube.orientLastLayer();
        PLL = cube.permuteLastLayer();

        movesToPerform = sunflower;
        movesPerformed = "";

        cube = new Cube();
        cube.scramble(scramble);
        //If the cube is being scrambled newly after initializing is complete and animation has begun,
        //be sure to reset all reference indexes
        movesIndex = 0;
        phase = 0;
        phaseString = "Initial Phase";
        invalidate();
        hasColorInput = false;

        moveSet[0] = sunflower;
        moveSet[1] = whiteCross;
        moveSet[2] = whiteCorners;
        moveSet[3] = secondLayer;
        moveSet[4] = yellowCross;
        moveSet[5] = OLL;
        moveSet[6] = PLL;
        moveSet[7] = " ";


        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }

/**
 * After the user inputs their desired colors in color selection mode, pressing the setInputs button
 * will invoke this method, acquiring the required moves necessary to solve the cube. The cube is restored back to
 * the scrambled state after the solution moves are acquired.
 */
public void resetScrambleByColorInputs(char[][][] colorsInputted) {
        this.colorsInputted = colorsInputted;
        cube = new Cube();
        cube.setAllColors(colorsInputted);

        sunflower = cube.makeSunflower();
        whiteCross = cube.makeWhiteCross();
        whiteCorners = cube.finishWhiteLayer();
        secondLayer = cube.insertAllEdges();
        yellowCross = cube.makeYellowCross();
        OLL = cube.orientLastLayer();
        PLL = cube.permuteLastLayer();

        cube = new Cube();
        cube.setAllColors(colorsInputted);
        //If the cube is being scrambled newly after initializing is complete and animation has begun,
        //be sure to reset all reference indexes
        movesIndex = 0;
        phase = 0;
        phaseString = "Current State";
        hasColorInput = true;
        scramble = "";

        moveSet[0] = sunflower;
        moveSet[1] = whiteCross;
        moveSet[2] = whiteCorners;
        moveSet[3] = secondLayer;
        moveSet[4] = yellowCross;
        moveSet[5] = OLL;
        moveSet[6] = PLL;
        moveSet[7] = " ";

        resetCurrentScramble(); //I'm not sure why, but this is necessary for it to work
        }

public void resetCurrentScramble() {
        if (!hasColorInput) {
        cube = new Cube();
        cube.scramble(scramble);

        } else {
        cube = new Cube();
        cube.setAllColors(colorsInputted);
        }

        movesToPerform = sunflower;
        movesPerformed = "";

        movesIndex = 0;
        phase = 0;
        phaseString = "Current State";

        invalidate();
        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }

public String randScramble() {
        scramble = Cube.generateRandScramble();
        resetScramble(scramble);

        return scramble;
        }

/**
 * After updating the phase (if necessary), performs the next move in the String movesToPerform
 * and updates movesPerformed.
 */
public void performNextMove() {
        updatePhase();

        //Get to a character that is not a space
        while (movesIndex < movesToPerform.length() - 1 && movesToPerform.substring(movesIndex, movesIndex + 1).compareTo(" ") == 0) {
        movesIndex++;
        }
        //Same logic as in Cube class's performMoves() method
        if (movesToPerform.length() > 0 && movesToPerform.substring(movesIndex, movesIndex + 1) != " ") {
        if (movesIndex != movesToPerform.length() - 1) {
        if (movesToPerform.substring(movesIndex + 1, movesIndex + 2).compareTo("2") == 0) {
        //Turning twice ex. U2
        cube.turn(movesToPerform.substring(movesIndex, movesIndex + 1));
        cube.turn(movesToPerform.substring(movesIndex, movesIndex + 1));
        movesIndex++;
        } else if (movesToPerform.substring(movesIndex + 1, movesIndex + 2).compareTo("'") == 0) {
        //Making a counterclockwise turn ex. U'
        cube.turn(movesToPerform.substring(movesIndex, movesIndex + 2));
        movesIndex++;
        } else {
        //Clockwise turn
        cube.turn(movesToPerform.substring(movesIndex, movesIndex + 1));
        }
        } else {
        //Clockwise turn
        cube.turn(movesToPerform.substring(movesIndex, movesIndex + 1));
        }
        }
        movesIndex++;
        //Append the moves performed onto the end of movesPerformed
        if (movesToPerform.length() > 0) {
        movesPerformed = movesToPerform.substring(0, movesIndex);
        }
        //Ensure that movesPerformed does not overflow out of the graphical interface
        if (movesPerformed.length() >= 35) {
        movesPerformed = movesPerformed.substring(movesPerformed.length() - 30);
        }
        }

/**
 * Updates the current phase of the solution as necessary
 * Respective States of the solution w.r.t the phase variable
 * 0 = sunflower		 	1 = whiteCross		2 = whiteCorners		3 = secondLayer
 * 4 = yellowCross		5 = OLL				6 = PLL
 */
public void updatePhase() {
        if (movesIndex >= movesToPerform.length()) {
        switch (phase) {
        case 0:
        phaseString = "White Cross";
        break;
        case 1:
        phaseString = "White Corners";
        break;
        case 2:
        phaseString = "Second Layer";
        break;
        case 3:
        phaseString = "Yellow Cross";
        break;
        case 4:
        phaseString = "OLL";
        break;
        case 5:
        phaseString = "PLL";
        break;
        case 6:
        //Failsafe to prevent app from crashing if the user tries to fast forward
        //once the solve is finished
        if (!animationStopped) {
        phaseString = "Solved";
        //add coefitti


        stopAnimation();
        }
        break;
        }
        if (phase < 7) {
        movesPerformed = "";
        phase++;
        movesIndex = 0;
        movesToPerform = moveSet[phase];
        }
        }
        }

public int getPhase() {
        return phase;
        }

public void skipToPhase(int skipTo) {
        UpdateUI skipMoves = new UpdateUI(UpdateUI.SKIP_PHASES, phase, skipTo);
        skipMoves.execute(getContext());

        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());
        }

public void stopAnimation() {
        if (!animationStopped) {
        frameTimer.cancel();
        animationStopped = true;
        }
        }

public void setSpeedMultiplier(int speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        }

public boolean getAnimationStopped() {
        return animationStopped;
        }

public void startAnimation(int multiplySpeed) {
        speedMultiplier = multiplySpeed;

        if (animationStopped) {
        animationTask = new TimerTask() {
synchronized public void run() {
        performNextMove();
        postInvalidate();

        UpdateUI updateMoves = new UpdateUI(UpdateUI.UPDATE_MOVES_ON_UI);
        updateMoves.execute(getContext());

        }
        };

        frameTimer = new Timer();
        frameTimer.scheduleAtFixedRate(animationTask,
        TimeUnit.MILLISECONDS.toMillis(DELAY / speedMultiplier),
        TimeUnit.MILLISECONDS.toMillis(DELAY / speedMultiplier));
        animationStopped = false;
        }
        }



    private class UpdateUI extends AsyncTask<Context, Void, ManualActivity> {
    final static int UPDATE_MOVES_ON_UI = 1;
    final static int SKIP_PHASES = 2;
    private int taskType;
    private int currentPhase, skipTo;

    public UpdateUI(int task) {
        taskType = task;
    }

    public UpdateUI(int task, int curPhase, int skipToPhase) {
        taskType = task;
        currentPhase = curPhase;
        skipTo = skipToPhase;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskType == SKIP_PHASES) {
            stopAnimation();
        }
    }

    @Override
    protected ManualActivity doInBackground(Context... contexts) {
        ManualActivity activity = (ManualActivity) contexts[0];

        if (taskType == SKIP_PHASES) { //At 7 the solve is complete
            if (skipTo > currentPhase && currentPhase < 7) {
                cube.performMoves(movesToPerform.substring(movesIndex));
                currentPhase++;
                movesToPerform = moveSet[phase];

                for (; currentPhase < skipTo; currentPhase++) {
                    cube.performMoves(moveSet[currentPhase]);
                }

                movesPerformed = "";
                movesIndex = 0;
                movesToPerform = moveSet[currentPhase];
            } else if (skipTo < currentPhase && currentPhase > 0) {
                //Log.d("Tried skipping", "true");
                cube = new Cube();
                cube.scramble(scramble);

                for (currentPhase = 0; currentPhase < skipTo; currentPhase++) {
                    cube.performMoves(moveSet[currentPhase]);
                }

                movesPerformed = "";
                movesIndex = 0;
                movesToPerform = moveSet[currentPhase];
            }
            phase = currentPhase;
        }

        return activity;
    }

    @Override
    protected void onPostExecute(ManualActivity activity) {

        if (taskType == SKIP_PHASES) {
            postInvalidate();
        } else if (taskType == UPDATE_MOVES_ON_UI) {
            switch (phase) {
                case 0:
                    phaseString = "Current State";
                    break;
                case 1:
                    phaseString = "White Cross";
                    break;
                case 2:
                    phaseString = "White Corners";
                    break;
                case 3:
                    phaseString = "Second Layer";
                    break;
                case 4:
                    phaseString = "Yellow Cross";
                    break;
                case 5:
                    phaseString = "OLL";
                    break;
                case 6:
                    phaseString = "PLL";
                    break;
                case 7:
                    phaseString = "Solved";
                    break;
            }

            TextSolutionFragment fragment = (TextSolutionFragment) ((AppCompatActivity) getContext())
                    .getSupportFragmentManager().findFragmentById(R.id.container);

            //TODO: separate updating moves from updating title/phase
            if (fragment != null && movesToPerform.length() > 0) {
                fragment.updateMoves(movesToPerform.substring(movesIndex).trim(),
                        movesPerformed.trim(), phaseString);
            }

            super.onPostExecute(activity);
        }
    }
}


}
