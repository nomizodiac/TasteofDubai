package org.progos.tasteofdubaicms.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains utility functions
 */
public class Utils {

    //********************************************************************************************************************************/

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    //********************************************************************************************************************************/

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    //********************************************************************************************************************************/

    public static Bitmap convertStreamToImage(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        try {
            // instream is content got from httpentity.getContent()
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = baos.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bmp;
    }

    //********************************************************************************************************************************/

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    //********************************************************************************************************************************/

    public static boolean isPhoneValid(String phoneNo) {
        String expression = "^[0-9-+]{9,15}$";
        CharSequence inputStr = phoneNo;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return (matcher.matches()) ? true : false;
    }

    //********************************************************************************************************************************/

    public static void showToast_msg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    //********************************************************************************************************************************/

    public static String md5(String s) {
        try {

            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String computeMD5Hash(String password) {
        String result = "";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            result = MD5Hash.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;


    }

    //********************************************************************************************************************************/

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        //canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
        canvas.drawOval(rectF, paint);
        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        //canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
        canvas.drawOval(rectF, paint);
        return output;
    }

    //********************************************************************************************************************************/

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }

    //********************************************************************************************************************************/

    public static Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //********************************************************************************************************************************/

    public static String getFormattedProfileName(String givenName, String familyName) {

        String name = "";
        if (!givenName.equals("") && !familyName.equals("")) {
            givenName = Character.toUpperCase(givenName.charAt(0)) + givenName.substring(1);
            familyName = Character.toUpperCase(familyName.charAt(0)) + familyName.substring(1);
            name = givenName + " " + familyName;

        } else if (givenName.equals("") && !familyName.equals("")) {
            familyName = Character.toUpperCase(familyName.charAt(0)) + familyName.substring(1);
            name = familyName;
        } else if (!givenName.equals("") && familyName.equals("")) {
            givenName = Character.toUpperCase(givenName.charAt(0)) + givenName.substring(1);
            name = givenName;
        }
        return name;
    }

    //********************************************************************************************************************************/

    public static String getFormattedJobTitleAndCompany(String jobTitle, String company) {

        String detail = "";

        if (!jobTitle.equals("") && !company.equals("")) {
            jobTitle = jobTitle.toUpperCase();
            company = Character.toUpperCase(company.charAt(0)) + company.substring(1);
            detail = jobTitle + ", " + company;
        } else if (jobTitle.equals("") && !company.equals("")) {
            company = Character.toUpperCase(company.charAt(0)) + company.substring(1);
            detail = company;
        } else if (!jobTitle.equals("") && company.equals("")) {
            jobTitle = jobTitle.toUpperCase();
            detail = jobTitle;
        }

        return detail;
    }

    //********************************************************************************************************************************/

    public static String getFormattedEmailAndPhoneNo(String email, String phoneNo) {
        String detail = null;
        if (!email.equals("") && !phoneNo.equals(""))
            detail = email + ", " + phoneNo;
        else if (email.equals("") && !phoneNo.equals(""))
            detail = phoneNo;
        else if (!email.equals("") && phoneNo.equals(""))
            detail = email;

        return detail;
    }

    //********************************************************************************************************************************/

    public static String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //********************************************************************************************************************************/

    public static String getCurrentDateTime() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return currentDateTimeString;
    }

    public static Date convertStringToDate(String strDate) {
        Date dateFormat = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
        try {
            dateFormat = format.parse(strDate);
            System.out.println(dateFormat);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateFormat;
    }

    public static boolean isCurrentYear(String msgDate) {
        // TODO Auto-generated method stub

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        String[] msgDateParts = msgDate.split("-");
        String msgDateYear = msgDateParts[0];
        String[] currentDateParts = currentDate.split("-");
        String currentDateYear = currentDateParts[0];

        if (msgDateYear.equalsIgnoreCase(currentDateYear))
            return true;

        return false;
    }


    public static long getDateDifference(String msgDate) {
        // TODO Auto-generated method stub
        long remainingDays = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

        long d1 = 0, d2 = 0;
        try {
            d1 = formater.parse(currentDate).getTime();
            d2 = formater.parse(msgDate).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        remainingDays = Math.abs((d1 - d2) / (1000 * 60 * 60 * 24));
        return remainingDays;
    }

    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable) {
        // TODO Auto-generated method stub
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            view.setBackgroundDrawable(drawable);
        else
            view.setBackground(drawable);
    }


    public static void openEmailClient(Context context, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        Intent mailer = Intent.createChooser(intent, null);
        context.startActivity(mailer);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static void savePreferences(Activity activity, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readPreferences(Activity activity, String key, String defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sp.getString(key, defaultValue);
    }

    public static String readPreferences(Context applicationContext, String key, String defaultValue) {
        // TODO Auto-generated method stub
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return sp.getString(key, defaultValue);
    }

    public static void savePreferences(Context applicationContext, String key, String value) {
        // TODO Auto-generated method stub
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static byte[] toByteArray(File file) {
        // TODO Auto-generated method stub

        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            // convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

            for (int i = 0; i < bFile.length; i++) {
                System.out.print((char) bFile[i]);
            }

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFile;
    }

    public static float convertDpToPixel(float dp, Context context) {
        // TODO Auto-generated method stub
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static void hideSoftKeyboard(Context context) {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static float getImageOrientation(File f) {
        // TODO Auto-generated method stub
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(f.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /**
     * Checking device has camera hardware or not
     *
     * @param mContext
     */
    public static boolean isDeviceSupportCamera(Context mContext) {
        if (mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static String getApplicationLabel(Context con) {
        PackageManager pm = con.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(con.getPackageName(), 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }

    public static void printDeviceScreenCredentials(Context context) {
        String screenSize = "", screenDensity = "";
        int size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (size) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                screenSize = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                screenSize = "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                screenSize = "Small screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                screenSize = "Extra Large screen";
                break;
            default:
                screenSize = "Screen size is neither large, normal or small";
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                screenDensity = "ldpi";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                screenDensity = "mdpi";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                screenDensity = "hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                screenDensity = "xhdpi";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                screenDensity = "xxhdpi";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                screenDensity = "xxxhdpi";
                break;
        }

        Log.i("Device Screen Credentials", "ScreenSize: " + screenSize);
        Log.i("Device Screen Credentials", "ScreenResolution: " + width + "×" + height);
        Log.i("Device Screen Credentials", "ScreenDensity: " + screenDensity);
    }


}


