package com.android.promosee.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;

import com.android.promosee.activities.WebViewActivity;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by angga on 16/12/16.
 */

public class Utils {

    public static String addThousandSeparator(double value) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        else
            result = Html.fromHtml(html);

        return result;
    }

    public static RoundingParams setCircleImage(SimpleDraweeView simpleDraweeView) {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);
        return roundingParams;
    }

    public static double getDistance(LatLng originLocation, LatLng destinationLocation) {
        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(destinationLocation.latitude - originLocation.latitude);
        Double lonDistance = Math.toRadians(destinationLocation.longitude - originLocation.longitude);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(originLocation.latitude))
                * Math.cos(Math.toRadians(destinationLocation.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }

    public static void setPhotoProfile(Preferences preferences, SimpleDraweeView simpleDraweeView) {
        simpleDraweeView.setImageURI(Uri.parse(preferences.getString("imageUrl")));
        simpleDraweeView.getHierarchy().setRoundingParams(
                Utils.setCircleImage(simpleDraweeView));
    }

    public static void openWebView(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}

