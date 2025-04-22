package ar.edu.uade.deremateapp.data;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.R;

public class UiUtils {
    public static void showErrorSnackbar(Activity activity, String message) {
        View anchor = activity.findViewById(ar.edu.uade.deremateapp.R.id.snackbar_anchor);

        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(activity, R.color.design_default_color_error))
                .setTextColor(Color.WHITE)
                .setAnchorView(anchor);

        snackbar.show();
    }
}
