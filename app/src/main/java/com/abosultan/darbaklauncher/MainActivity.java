package com.abosultan.darbaklauncher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(16,20,25);
    private static final int SURFACE = Color.rgb(26,32,39);
    private static final int GOLD = Color.rgb(214,173,92);
    private static final int CYAN = Color.rgb(93,183,199);
    private LinearLayout content;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        showHome();
    }

    private TextView text(String s, int sp, int color) {
        TextView v=new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(color); v.setGravity(Gravity.CENTER); v.setPadding(12,8,12,8); return v;
    }

    private void showHome() {
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setGravity(Gravity.CENTER); content.setPadding(28,20,28,12);
        root.addView(content,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout cards=new LinearLayout(this); cards.setGravity(Gravity.CENTER); cards.setOrientation(LinearLayout.HORIZONTAL);
        String time=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());
        String[] data={time,"—\nكم/س","GPS\nغير متاح","Wi‑Fi\nالحالة"};
        for(String s:data){ TextView c=text(s,26,Color.WHITE); c.setBackgroundColor(SURFACE); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,150,1); p.setMargins(8,8,8,8); cards.addView(c,p); }
        content.addView(cards,new LinearLayout.LayoutParams(-1,-2));
        TextView title=text("دربك",34,GOLD); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(title);
        content.addView(text("واجهة موحدة .. تجربة أفضل",18,Color.LTGRAY));
        LinearLayout nav=new LinearLayout(this); nav.setOrientation(LinearLayout.HORIZONTAL); nav.setGravity(Gravity.CENTER); nav.setBackgroundColor(SURFACE);
        String[] tabs={"الرئيسية","التطبيقات","الموسيقى","الرحلة","الإعدادات"};
        for(String t:tabs){ TextView v=text(t,16,t.equals("الرئيسية")?GOLD:Color.WHITE); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,70,1); nav.addView(v,p); }
        root.addView(nav,new LinearLayout.LayoutParams(-1,70));
        setContentView(root);
    }
}
