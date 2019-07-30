package com.example.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int score = 0;
    View view;
    View nextBlockView;
    Random rand = new Random();
    private int dropTime = 1000; //  5000ms
    private int[][] map;
    Paint paint;
    Paint boxPaint;
    // define box
    Point[] boxs;
    private int offset = 1;
    Point[] nextboxs;
    int boxSize;
    int blockNumberType = 5;
    // we should represent the blocks;
    int height, width;

    public void changeOffset() {
        for (int i = 0; i < boxs.length; i++ ) {
            boxs[i].y -= offset;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        dropAutomatical();

    }

    public Point[] initBlock() {
        int random = rand.nextInt(blockNumberType);
        Block block = new Block();
        return block.generateBlock(random);
    }

    public void initData() {
        int screenWidth = getScreenWidth(this);
        System.out.println(screenWidth);
        width = screenWidth * 2 / 3;

        height = width * 2;

        map = new int[10][20];

        boxs = initBlock();
        changeOffset();
        nextboxs = initBlock();

        boxSize = width / map.length;
    }

    public void initEventListener() {
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.speed).setOnClickListener(this);
        findViewById(R.id.reverse).setOnClickListener(this);
        onClick(view);
        view.invalidate();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.left:
                moveHorizontal(-1);
                break;
            case R.id.right:
                moveHorizontal(1);
                break;
            case R.id.reverse:
                rotate();
                break;
            case R.id.speed:
                accelerate(50);
                dropAutomatical();
                break;

            default:

                break;
        }
    }

    public void moveHorizontal(int x) {
        if (!checkMoveIfBoundary(x, 0) && !checkMoveIfCollide(x,0))
            for (int i = 0; i < boxs.length; i++)
                boxs[i].x += x;

    }
    public void moveVertical(int y) {
        if (!checkMoveIfBoundary(0, y) && !checkMoveIfCollide(0 ,y))
            for (int i = 0; i < boxs.length; i++)
                boxs[i].y += y;
        else {
            // save the block on the map to repaint it.
            for (int i = 0; i < boxs.length; i++) {
                if (boxs[i].x >=0 && boxs[i].y >= 0)
                    map[boxs[i].x][boxs[i].y] = 1;
            }
            // remove the earliest block;
            boxs  = nextboxs;
            changeOffset();
            nextboxs = initBlock();

        }


    }


    public boolean checkMoveIfCollide(int offsetX, int offsetY) {

        for (int i = 0; i < boxs.length; i++) {
            if (boxs[i].x >=0 && boxs[i].y >= 0)
                if (map[boxs[i].x+offsetX][boxs[i].y] == 1) return true;
        }
        for (int i = 0; i < boxs.length; i++) {
            if (boxs[i].x >=0 && boxs[i].y >= 0)
                if (map[boxs[i].x][boxs[i].y+offsetY] == 1) return true;
        }
        return false;
    }

    public boolean checkRotateIfCollide() {

        for (int i = 0; i < boxs.length; i++) {
            int newX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int newY = boxs[i].x - boxs[0].x + boxs[0].y;
            if (map[newX][newY] == 1) return true;
        }
        return false;
    }
    public boolean checkMoveIfBoundary(int offsetX, int offsetY) {
        for (int i = 0; i < boxs.length; i++) {
            if (boxs[i].x >=0 && boxs[i].y >= 0)
                if (boxs[i].x + offsetX < 0 || boxs[i].x + offsetX >= map.length) return true;
                if (boxs[i].y + offsetY < 0 || boxs[i].y + offsetY >= map[0].length) return true;
        }
        return false;
    }

    public boolean chechRotateIfBoundary() {
        for (int i = 0; i< boxs.length; i++) {
            int newX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int newY = boxs[i].x - boxs[0].x + boxs[0].y;
            if (newX < 0 || newX >= map.length) return true;
            if (newY < 0 || newY >= map[0].length) return true;
        }
        return false;
    }
    public void rotate() {
        if (!chechRotateIfBoundary() && !checkRotateIfCollide())
            for (int i = 0; i< boxs.length;i++) {
                int newX = -boxs[i].y + boxs[0].y + boxs[0].x;
                int newY = boxs[i].x - boxs[0].x + boxs[0].y;
                boxs[i].x = newX;
                boxs[i].y = newY;
            }
    }

    public void adjustMap(int colIndex) {
        // if the top of the board is 0, do nothing.
        if (colIndex == 0) return;

        for (int i = 0; i < map.length; i++ ){
            for (int j = colIndex; j > 0; j--) {
                //swap
                int temp = map[i][j];
                map[i][j] = map[i][j-1];
                map[i][j-1] = temp;
            }
        }

    }
    public int sumByCol(int columnIndex) {
        int sum = 0;
        for (int i = 0; i < map.length; i++){
            sum += map[i][columnIndex];
        }
        return sum;
    }
    public void removeBlock(int colIndex) {
        for (int i = 0; i < map.length; i++) map[i][colIndex] = 0;
    }

    public void Score() {

        for (int y = map[0].length - 1; y >= 0; y--) {
            if (sumByCol(y) >= map.length) {
                System.out.println("is full!!!");
                removeBlock(y);
                adjustMap(y);
                TextView textView = findViewById(R.id.score);
                score += map.length;
                String text = "score: "+score+" points";
                textView.setText(text);
                view.invalidate();
            }
        }
    }

    void accelerate(int offset) {
        if (dropTime <= 0) return;
        dropTime -= offset;
    }
    void dropAutomatical() {
        Timer timer = new Timer();
        int time = dropTime;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                moveVertical(1);
            }
        };
        timer.schedule(timerTask,0, time);
    }
    void initView() {
        // get the main game panel
        FrameLayout MainGame = findViewById(R.id.MainGame);
        FrameLayout nextBlock = findViewById(R.id.nextBlock);

        paint = new Paint();
        paint.setColor(Color.BLACK);

        boxPaint = new Paint();
        boxPaint.setColor(Color.MAGENTA);

        dropAutomatical();

        view = new View(this){
            // draw line of the game board;
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                initEventListener();
                Score();

                for (int x = 0; x < map.length; x++ ) {
                    for (int y = 0; y < map[0].length; y++)
                        if (map[x][y] == 1)
                            canvas.drawRect(x*boxSize, y*boxSize, x*boxSize+boxSize, y*boxSize+boxSize, boxPaint);
                }
                for (int x = 0; x < map.length; x++ ) {
                    for (int y = 0; y < map[0].length; y++)
                        if (map[x][y] == 1)
                            canvas.drawRect(x*boxSize, y*boxSize, x*boxSize+boxSize, y*boxSize+boxSize, boxPaint);
                }
                for (int i = 0; i < boxs.length; i++) {
                    if (boxs[i].x >= 0 && boxs[i].y >= 0)
                        canvas.drawRect(
                            boxs[i].x * boxSize,
                            boxs[i].y * boxSize,
                            boxs[i].x* boxSize+boxSize,
                            boxs[i].y* boxSize+boxSize,
                            boxPaint);
                }
                // draw vertical line
                for (int x = 0; x < map.length; x++) {
                    canvas.drawLine(x*boxSize, 0, x*boxSize, view.getHeight(), paint);
                }
                // draw vertical line
                for (int y = 0; y < map[0].length; y++) {
                    canvas.drawLine(0,y*boxSize, view.getWidth(), y*boxSize, paint);
                }

            }


        };
        nextBlockView = new View(this) {
            @Override
            public void onDraw(Canvas canvas) {
                // draw next block
                for (int i = 0; i < nextboxs.length; i++) {
                    canvas.drawRect(
                            nextboxs[i].x * boxSize,
                            nextboxs[i].y * boxSize,
                            nextboxs[i].x* boxSize+boxSize,
                            nextboxs[i].y* boxSize+boxSize,
                            boxPaint);
                }
                nextBlockView.invalidate();
            }
        };

        view.setBackgroundColor(Color.GRAY);
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        nextBlockView.setBackgroundColor(Color.WHITE);
        nextBlockView.setLayoutParams(new ViewGroup.LayoutParams(width/2, height));

        // add into parent
        MainGame.addView(view);
        nextBlock.addView(nextBlockView);

    }


    // can be changed using Point
    public static int getScreenWidth(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}