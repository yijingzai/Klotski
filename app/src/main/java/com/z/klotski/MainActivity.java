package com.z.klotski;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int unit_width = 90;
    private static final int unit_height = 100;
    private static final double constant = 0.48;
    final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
    private RelativeLayout container;
    private TextView score_text;
    private int score = 0;
    private int width_location;
    private int height_location;
    private int button_left,button_top;
    private int key_id;
    private int choice;
    private static int[][] map = new int[5][4];
    String[] name = {"曹操","马超","张飞","黄忠","赵云","关羽","卒"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        choice = intent.getIntExtra(Mission_List.Mission_choice,0);
        container =  findViewById(R.id.container);
        if(savedInstanceState!=null) {
            for(int i=0;i<5;i++)
                map[i] = savedInstanceState.getIntArray("map"+i);
        }
        if(!getSaveMap())
            map = Mission.get_Mission(choice);
        getSaveScore();
        score_text = findViewById(R.id.score);
        score_text.setText(score+"");
        score_text.setTextColor(Color.WHITE);
        init(container);
        TextView score_name = findViewById(R.id.score_name);
        score_name.setTextColor(Color.WHITE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for(int i=0;i<5;i++) {
            outState.putIntArray("map"+i,map[i]);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Button button = findViewById(view.getId());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                width_location = (int) motionEvent.getX();
                height_location = (int) motionEvent.getY();
                button_left = (int) (changeDP(button.getLeft())/unit_width+constant);
                button_top = (int) (changeDP(button.getTop())/unit_height+constant);
                clearMap(button_left, button_top, button);
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                int dx = x - width_location;
                int dy = y - height_location;
                TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                animation.setDuration(1);
                button.startAnimation(animation);
                button.layout(button.getLeft()+dx,button.getTop()+dy,button.getRight()+dx,button.getBottom()+dy);
                break;
            case MotionEvent.ACTION_UP:
                Move(button);
                RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) button.getLayoutParams();
                rp.topMargin = changePx(button_top * unit_height);
                rp.leftMargin = changePx(button_left * unit_width);
                button.setLayoutParams(rp);
                setMap(button_left,button_top,button);
                if (button.getId()==key_id && victory()) {
                    Toast.makeText(MainActivity.this,"恭喜通关",Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = getSharedPreferences("map", MODE_PRIVATE).edit();
                    editor.putInt("Mission" + choice,1);
                    editor.apply();
                    save(new View(this));
                    Intent intent = new Intent(MainActivity.this, MissionSelectActivity.class);
                    startActivity(intent);
                }
        }
        return true;
    }

    public int changeDP(float x) {
        return (int)(x/scale+0.5f);
    }

    public int changePx(float x) {
        return (int)(x*scale+0.5f);
    }

    public void setMap(int left,int top,Button button) {
        int width = changeDP(button.getWidth())/unit_width;
        int height = changeDP(button.getHeight())/unit_height;
        for(int i = 0;i<width;i++) {
            for(int j =0;j<height;j++) {
                map[top+j][left+i] = width+height*2;
            }
        }
    }

    public void clearMap(int left,int top,Button button) {
        int width = changeDP(button.getWidth())/unit_width;
        int height = changeDP(button.getHeight())/unit_height;
        for(int i = 0;i<width;i++) {
            for(int j =0;j<height;j++) {
                map[top+j][left+i] = 0;
            }
        }
    }

    public void Move(Button button) {
        int left = (int)(changeDP(button.getLeft())/unit_width+constant);
        int top = (int)(changeDP(button.getTop())/unit_height+constant);
        if (Math.abs(button_left-left)+Math.abs(button_top-top)!=1)//移动步数不为1
            return;
        int width = changeDP(button.getWidth())/unit_width;
        int height = changeDP(button.getHeight())/unit_height;
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                if(map[top+j][left+i] > 0)
                    return;
            }
        }
        button_left = left;
        button_top = top;
        score++;
        score_text.setText(score+"");
    }

    public void init(RelativeLayout container) {
        int[][] Map = new int[5][4];
        for(int i=0;i<5;i++) {
            System.arraycopy(map[i], 0, Map[i], 0, 4);
        }
        int name_y = 1;//纵向
        int name_x = 5;//横向
        for(int i=0;i<5;i++) {
            for(int j = 0;j<4;j++)
                switch (Map[i][j]) {
                    case 6:
                        Button view = addView(2, 2, i, j, Map);
                        view.setText(name[0]);
                        view.setBackgroundResource(R.drawable.a0);
                        container.addView(view);
                        key_id = view.getId();
                        break;
                    case 5:
                        Button view1 = addView(1, 2, i, j, Map);
                        view1.setText(name[name_y]);
                        switch (name_y) {
                            case 1:
                                view1.setBackgroundResource(R.drawable.a1);
                                break;
                            case 2:
                                view1.setBackgroundResource(R.drawable.a2);
                                break;
                            case 3:
                                view1.setBackgroundResource(R.drawable.a3);
                                break;
                            case 4:
                                view1.setBackgroundResource(R.drawable.a4);
                                break;
                            case 5:
                                view1.setBackgroundResource(R.drawable.a5);
                        }
                        container.addView(view1);
                        name_y++;
                        break;
                    case 4:
                        Button view2 = addView(2, 1, i, j, Map);
                        view2.setText(name[name_x]);
                        switch (name_x) {
                            case 1:
                                view2.setBackgroundResource(R.drawable.a1);
                                break;
                            case 2:
                                view2.setBackgroundResource(R.drawable.a2);
                                break;
                            case 3:
                                view2.setBackgroundResource(R.drawable.a3);
                                break;
                            case 4:
                                view2.setBackgroundResource(R.drawable.a4);
                                break;
                            case 5:
                                view2.setBackgroundResource(R.drawable.a5);
                        }
                        container.addView(view2);
                        name_x--;
                        break;
                    case 3:
                        Button view3 = addView(1, 1, i, j, Map);
                        view3.setText(name[6]);
                        view3.setBackgroundResource(R.drawable.a6);
                        container.addView(view3);
                }
        }
    }

    public Button addView(int width,int height,int location_y,int location_x,int[][] Map) {
        Button view  = new Button(this);
        view.setId(location_x*10+location_y);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(changePx(width*unit_width),changePx(height*unit_height));
        rp.leftMargin = changePx(location_x*unit_width);
        rp.topMargin = changePx(location_y*unit_height);
        view.setLayoutParams(rp);
        view.setPadding(5,5,5,5);
        view.setOnTouchListener(this);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Map[location_y + j][location_x + i] = 0;
            }
        }
        return view;
    }

    public static boolean victory() {
        for(int i=0;i<3;i++) {
            if(map[4][i]==6)
                return true;
        }
        return false;
    }

    public void reset(View view) {
        container.removeAllViews();
        map = Mission.get_Mission(choice);
        score = 0;
        score_text.setText(score+"");
        SharedPreferences.Editor editor = getSharedPreferences("map", MODE_PRIVATE).edit();
        editor.putInt("Mission" + choice,0);
        editor.apply();
        init(container);
        save(new View(MyApplication.getContext()));
    }

    public void save(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("map", MODE_PRIVATE).edit();
        for(int i=0;i<5;i++)
            editor.putString("Mission"+choice+"map"+i, Arrays.toString(map[i]));
        editor.putInt("Mission"+choice+"score", score);
        editor.apply();
    }

    public boolean getSaveMap() {
        SharedPreferences read = getSharedPreferences("map", MODE_PRIVATE);
        String check = read.getString("Mission"+choice+"map"+0,"null");
        if(check == "null")
            return false;
        for(int i=0;i<5;i++) {
            String temp = read.getString("Mission"+choice+"map"+i,"null");
            temp = temp.replace("[","");
            temp = temp.replace(",","");
            temp = temp.replace("]","");
            String[] tem = temp.split(" ");
            for(int j = 0;j<4;j++) {
                map[i][j] = Integer.parseInt(tem[j]);
            }
        }
        return true;
    }

    public void getSaveScore() {
        SharedPreferences read = getSharedPreferences("map", MODE_PRIVATE);
        score = read.getInt("Mission"+choice+"score",0);
    }

    public void back(View view) {
        save(new View(this));
        Intent intent = new Intent(MainActivity.this, MissionSelectActivity.class);
        startActivity(intent);
    }
}
