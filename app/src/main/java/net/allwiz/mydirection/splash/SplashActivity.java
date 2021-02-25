package net.allwiz.mydirection.splash;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.allwiz.mydirection.MainActivity;
import net.allwiz.mydirection.R;
import net.allwiz.mydirection.shared.PreferenceManager;
import net.allwiz.mydirection.shared.SharedApplication;
import net.allwiz.mydirection.util.LogEx;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    protected SharedApplication mSharedApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        checkShortcut();
        showMainActivity();
    }


    private void checkShortcut() {
        if (!PreferenceManager.isShortCut(getApplicationContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ShortcutManager shortcutManager = getApplicationContext().getSystemService(ShortcutManager.class);
                if(shortcutManager.isRequestPinShortcutSupported()) {
                    ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(getApplicationContext(), getResources().getString(R.string.app_name))
                            .setShortLabel(getResources().getString(R.string.app_name))
                            .setLongLabel(getResources().getString(R.string.app_name))
                            .setIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_mydirection_launcher))
                            .setIntent(new Intent(getApplicationContext(), SplashActivity.class).setAction(Intent.ACTION_VIEW))
                            .build();
                    Intent resultIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, resultIntent, 0);
                    shortcutManager.requestPinShortcut(pinShortcutInfo, pendingIntent.getIntentSender());
                }
            } else {
                Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
                shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shortcutIntent.setClassName(getApplicationContext(), getClass().getName());
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
                intent.putExtra("duplicate", false);
                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                sendBroadcast(intent);
            }

            PreferenceManager.setShortCut(getApplicationContext(), true);
        }
    }


    private void showMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 400);

    }
}
