package com.rigadev.nutricapps.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyConfig {

    public static void showToast(Context context, String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static String konvertdate (String a, String b, String c) {
        SimpleDateFormat sdf = new SimpleDateFormat(b);
        Date testDate = null;
        try {
            testDate = sdf.parse(a);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(c);
        String newFormat = formatter.format(testDate);
        return newFormat;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri, String IMAGE_DIRECTORY) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            // create folder if not exists
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream, 1024 * 2);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output, int BUFFER_SIZE) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    public static String priceWithoutDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    public static String priceToString(Double price) {
        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(".") > 0) {
            return priceWithDecimal(price);
        } else {
            return priceWithoutDecimal(price);
        }
    }



    public static String showCurentDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat contoh4 = new SimpleDateFormat("EEEE, d MMMM yyyy");
        String Tanggal4 = contoh4.format(cal.getTime());

        return Tanggal4;
    }

    public static String replacePhoneto62(String no_phone){
        String sbstr = no_phone.substring(1);

        return "+62"+sbstr;
    }

    public static String getCurentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }
    public static String konvertDateIndo(String date){
        String finalDate="";

        String[] bulan={"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober","November", "Desember"};

        String[] split = date.split("-");
        finalDate = split[2] + " " + bulan[Integer.parseInt(split[1])-1]  + " " + split[0];

        return finalDate;
    }

    public static String konvertDateIndoTime(String date){
        String finalDate="";

        String[] bulan={"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober","November", "Desember"};

        String[] split = date.split("-");
        String[] split2 = date.split(" ");
        finalDate = split[2].substring(0,2) + " " + bulan[Integer.parseInt(split[1])-1]  + " " + split[0]+" " + split2[1];

        return finalDate;
    }

    public static String showDateTitle(String date){
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd");
        Date newDate= null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("EEEE, dd MMMM yyyy");
        date = spf.format(newDate);

        return date;
    }

    public static String convertDayNameToIndo(String s){
        String hariIndo="";
        if (s.equals("Sunday")){
            hariIndo= "Minggu";
        }else if (s.equals("Monday")){
            hariIndo= "Senin";
        }else if (s.equals("Tuesday")){
            hariIndo= "Selasa";
        }else if (s.equals("Wednesday")){
            hariIndo= "Rabu";
        }else if (s.equals("Thursday")){
            hariIndo= "Kamis";
        }else if (s.equals("Friday")){
            hariIndo= "Jumat";
        }else if (s.equals("Saturday")){
            hariIndo= "Sabtu";
        }

        return hariIndo;

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void setSnackBar(View root, String textToDisplay) {
        Snackbar snackbar = Snackbar.make(root, textToDisplay, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                //your other action after user hit the "OK" button
            }
        });
        snackbar.show();
    }

    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ImgSuper" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getStringImage(Bitmap bmp, int bitmap_size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static String konversiCurrency(String nilai) {

        String harga="";

        try {
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
            formatSymbols.setDecimalSeparator(',');
            formatSymbols.setGroupingSeparator('.');

            DecimalFormat df = new DecimalFormat("####,###.###", formatSymbols);

            Double d = Double.parseDouble(nilai);

            harga = df.format(d);

        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return harga ;
    }

    public static String formatNumberComma(String s){
        String originalString = s.toString();
        Long longval;
        if (originalString.contains(",")) {
            originalString = originalString.replaceAll(",", "");
        }
        longval = Long.parseLong(originalString);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###.##");
        return formatter.format(longval);
    }


    public static String removeFirst(String input)
    {
        return input.substring(1);
    }

    public static String removeLastChar(String s)
    {
        return s.substring(0, s.length() - 1);
    }

    public static String removeLastFirstChar(String s)

    {
        String first = s.substring(1);
        String finalS = first.substring(0, first.length() - 1);
        return finalS;
    }

    @SuppressLint("NewApi")
    public static String removeSomeChar(String str){

        str = str.replaceAll("(lat/lng:|(|))\\s+", "");
        String temp = str;
        return str.codePoints().mapToObj( Character::toChars ).filter(
                a -> (a.length == 1 && (Character.isLetterOrDigit( a[0] ) || a[0] == ',' || a[0] == '.')) )
                .collect( StringBuilder::new, StringBuilder::append, StringBuilder::append ).toString(); // owl134_-abc

    }

    public static int formatDoublePriceToInt(String cur){
        int curInt = 0;
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number numbers = null;
        try {
            numbers = format.parse(cur);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (numbers==null){
            curInt = 0;
        }else {
            curInt = numbers.intValue();
        }

        return curInt;
    }

}
