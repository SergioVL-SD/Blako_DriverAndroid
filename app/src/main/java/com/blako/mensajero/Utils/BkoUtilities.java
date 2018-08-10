package com.blako.mensajero.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoCompleteResponse;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoUserStatusResponse;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.accesibility.CustomAccessibilityService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BkoUtilities {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni!= null && ni.isConnected();
    }


    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(context, "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }


    public static String ensureNotNullString(String value) {
        return (value == null || "null".equals(value)) ? "" : value;
    }

    public static String nowCurrent() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat hours = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return hours.format(now.getTime());
    }

    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }
        return hex.toString();
    }


    public static void createTxt(String orderId, String waypoints, String tripId) {
        File f;

        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "blako_data/files/" + orderId);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }

            f = new File(Environment.getExternalStorageDirectory().toString() + "/blako_data/files/" + orderId + "/" + orderId + "_waypoints_trips_app.txt");

            if (!f.exists())
                f.createNewFile();
        } catch (IOException e) {

            e.printStackTrace();
            return;
        }


        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(f, true));
            buf.append(waypoints + "\r\n");
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int countFileTxt(String orderId) {
        StringBuilder text = new StringBuilder();
        int lineCount = 0;
        try {
            File f = null;
            f = new File(Environment.getExternalStorageDirectory().toString() + "/blako_data/files/" + orderId + "/" + orderId + "_waypoints_trips_app.txt");

            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;


                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');

                    lineCount++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineCount;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception ignored) {

        }
        return versionCode;
    }

    public static String getVersionName(Context context) {
        String versionName = "1";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception ignored) {

        }
        return versionName;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         boolean rotate) {

        Bitmap bitmap = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, 300, 300);

        options.inJustDecodeBounds = false;
        try {

            bitmap = BitmapFactory.decodeFile(path, options);

            if (rotate) {
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            }
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        //reqWidth = ((int) width 50);
        //reqHeight = ((int) height / 5);

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(

                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PHOTO");

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("PHOTO", "failed to create directory");
            }

            // Return the file target for the photo based on filename
          Uri uri=  Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));

            return uri;
        }
        return null;
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path, int orientation) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        //  int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String pathTxt(String orderId, String tripId) {

        try {
            File f = null;
            f = new File(Environment.getExternalStorageDirectory().toString() + "/blako_data/files/" + orderId + "/" + orderId + "_waypoints_trips_app.txt");

            if (f.exists()) {


            } else {
                f.createNewFile();

            }
            return Environment.getExternalStorageDirectory().toString() + "/blako_data/files/" + orderId + "/" + orderId + "_waypoints_trips_app.txt";

        } catch (Exception e) {
            e.printStackTrace();
            //You'll need to add proper error handling here
        }
        return null;
    }

    public static String pathSignature(Context context, String tripId) {

        try {

            File f = new File(Environment.getExternalStorageDirectory().toString() + "/blako_data/" + tripId + ".png");

            if (f.exists())
                return Environment.getExternalStorageDirectory().toString() + "/blako_data/" + tripId + ".png";
            else
                return null;
        } catch (Exception e) {
            LogUtils.debug("Ticket_Image_Error", e.toString());
        }

        return null;

    }


    public static String thirdTry(URL url, FileInputStream fis, String fileName) {

        URL connectURL;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "3rd";
        String response = null;
        try {
            //------------------ CLIENT REQUEST

            Log.e(Tag, "Starting to bad things");
            // Abrimos una conexi�n http con la URL
            connectURL = url;
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

            // Permitimos Inputs
            conn.setDoInput(true);

            // Permitimos Outputs
            conn.setDoOutput(true);

            // Deshabilitamos el uso de la copia cacheada.
            conn.setUseCaches(false);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // creamos un buffer con el tama�o maximo de archivo, lo pondremos en 1MB

            int bytesAvailable = fis.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fis.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }

            // enviar multipart form data

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // cerramos
            Log.e(Tag, "File is written");
            fis.close();
            dos.flush();

            InputStream is = conn.getInputStream();
            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            response = b.toString();
            Log.i("Response HttpFileUpload", response);

            dos.close();
            return response;

        } catch (MalformedURLException ex) {
            Log.e(Tag, "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "error: " + ioe.getMessage(), ioe);
        }
        return response;
    }


    public static String uploadProfilePicture(String pathPhoto, Context context) {


        try {


            BkoUser user = BkoUserDao.Consultar(context);
            Map<String, String> map = new HashMap<String, String>();
            map.put("connectToken", user.getConectToken());
            map.put("workerId", user.getWorkerId());


            URL contentPhoto = HttpRequest
                    .get(Constants.GET_UPLOAD_PHOTO_PROFILE(context), map, true)
                    .connectTimeout(2500).url();
            FileInputStream fisTxt = new FileInputStream(pathPhoto);
            String uploadPhoto = BkoUtilities.thirdTry(contentPhoto, fisTxt, pathPhoto);
            return uploadPhoto;

        } catch (Exception e) {

        }

        return null;
    }

    public static BkoCompleteResponse completeTrip(BkoChildTripVO currentTrip, Context context) {
        BkoCompleteResponse completeResponseVO;
        String ticketReponse, completeResponse;

        try {
            ticketReponse = null;
            completeResponse = null;
            completeResponseVO = null;
            BkoUser user = BkoUserDao.Consultar(context);


            if (user != null) {

                // send firma

                Map<String, String> map = new HashMap<String, String>();
                map.put("connectToken", user.getConectToken());
                map.put("receivedpackage", currentTrip.getReceivedpackage());
                map.put("receivedcomment", currentTrip.getReceivedcomment());
                map.put("receiveddatetime", currentTrip.getReceiveddatetime());
                map.put("tripId", currentTrip.getBko_orders_trips_id());
                map.put("oid", currentTrip.getBko_orders_id());


                //map.put("success","true");
                URL content = HttpRequest
                        .get(Constants.GET_SEND_TICKET(context), map, true)
                        .connectTimeout(2500).url();

                LogUtils.debug("Send_Ticket_Url", content.toString());

                String path = BkoUtilities.pathSignature(context, currentTrip.getBko_orders_trips_id());
                if (path == null)
                    return null;

                FileInputStream fis = new FileInputStream(path);
                ticketReponse = BkoUtilities.thirdTry(content, fis, path);

                LogUtils.debug("Send_Ticket_Response", ticketReponse);

                if (ticketReponse != null) {

                    try {
                        Gson gson = new Gson();
                        BkoUserStatusResponse response = gson.fromJson(ticketReponse, BkoUserStatusResponse.class);

                        if (response != null) {
                            if (response.isResponse()) {
                                currentTrip.setIsTickectSended(true);
                                BkoDataMaganer.setCurrentTrip(currentTrip, context);

                                final BkoPushRequest serviceClienteStatus = BkoDataMaganer.getCurrentStatusRequest(context);
                                Map<String, String> mapComplete = new HashMap<String, String>();
                                mapComplete.put("connectToken", user.getConectToken());
                                mapComplete.put("oid", serviceClienteStatus.getOid());
                                mapComplete.put("tripId", currentTrip.getBko_orders_trips_id());
                                mapComplete.put("latitud", "" + currentTrip.getEndLatitud());
                                mapComplete.put("longitud", "" + currentTrip.getEndLongitude());
                                mapComplete.put("orderId", currentTrip.getBko_orders_id());

                                int count = 1;
                                try {
                                    count = BkoUtilities.countFileTxt(serviceClienteStatus.getOid());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                mapComplete.put("linesFileApp", "" + count);
                                mapComplete.put("typeFileTxt", "web");
                                mapComplete.put("timeStart", currentTrip.getBko_orders_trips_startdatetime());
                                mapComplete.put("timecompleted", currentTrip.getBko_orders_trips_completeddatetime());
                                if (currentTrip.isSuccess())
                                    mapComplete.put("success", "true");
                                else
                                    mapComplete.put("success", "false");


                                try {


                                    Map<String, String> mapCompleteTxt = new HashMap<String, String>();
                                    mapCompleteTxt.put("oid", "" + serviceClienteStatus.getOid());

                                    //map.put("success","true");
                                    URL contentTxt = HttpRequest
                                            .get(Constants.GET_SEND_TXT(context), mapCompleteTxt, true)
                                            .connectTimeout(3000).url();



                                                //  mapComplete.remove("typeFileTxt");
                                                //  mapComplete.put("typeFileTxt", "app");
                                                completeResponse = HttpRequest
                                                        .get(Constants.GET_WORKER_COMPLETED_URL(context), mapComplete, true)
                                                        .connectTimeout(3000).body();

                                                completeResponseVO = gson.fromJson(completeResponse, BkoCompleteResponse.class);
                                                return completeResponseVO;





                                } catch (Exception e) {

                                    e.printStackTrace();

                                }

                                //completeTrip();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;


    }


    public static String formateDateFromstring(String inputDate) {

        Date parsed = null;
        String outputDate = "";

        if (inputDate == null)
            return "";


        try {
            SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            SimpleDateFormat df_output = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

            parsed = df_input.parse(inputDate);

            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            // OVERRIDE SOME symbols WHILE RETAINING OTHERS
            symbols.setAmPmStrings(new String[]{"AM", "PM"});
            df_output.setDateFormatSymbols(symbols);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {

        }

        return outputDate;

    }


    public static String JsonDate(String jsonData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        String formattedTime = null;

        try {
            d = sdf.parse(jsonData);

            return output.format(d);

        } catch (ParseException e) {

        }

        return "";
    }


    public static String JsonDate(String jsonData, String inputFormat, String outFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        SimpleDateFormat output = new SimpleDateFormat(outFormat);
        Date d = null;
        String formattedTime = null;

        try {
            d = sdf.parse(jsonData);

            return output.format(d);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return "";
    }


    public static Date getDate(String date, String format) {


        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date d = null;
            String formattedTime = null;
            d = sdf.parse(date);

            return d;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String formatToYesterdayOrToday(String date) throws ParseException {

        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, +1);
            DateFormat timeFormatter = new SimpleDateFormat("EEEE dd MMM", new Locale("es", "ES"));

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return "HOY";
            } else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                return "MAÑANA";
            } else {

                String dayOfTheWeek = timeFormatter.format(dateTime);

                return dayOfTheWeek;
            }
        } catch (Exception e) {
            return "";
        }

    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    public static boolean checkCorrectDate(final Context context) {
        int automaticTime = 0;
        try {
            automaticTime = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME);


            int automaticTimeZone = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE);


            if (automaticTime == 0 || automaticTimeZone == 0) {


                AlertDialog alertDialog = NotificationDialog.showDialog(context.getString(R.string.blako_advertencia), context.getString(R.string.blako_estatus_cambiar_fecha_mensaje), context, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 0);
                            }
                        },
                        context.getString(R.string.blako_estatus_cambiar_fecha), null);

                return false;

            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isMockLocationOn(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }


    public static Boolean isActivityRunning(Class activityClass, Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }


    public static void showLocation(final Context context, final String lat, final String lng) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bko_location_dialog, null);

        ImageView googleBtn = (ImageView) dialogView.findViewById(R.id.googleBtn);
        ImageView wazeBtn = (ImageView) dialogView.findViewById(R.id.wazeBtn);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Abrir Ubicación");


        dialogBuilder.setNegativeButton(R.string.blako_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

                try {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng+"&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

        wazeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

                try {

                    String url = "waze://?ll=" + lat + "," + lng + "&navigate=yes";
                    Intent intentwaze = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intentwaze);
                } catch (Exception e) {
                    Intent intentwaze =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    context.startActivity(intentwaze);
                }

            }
        });
        alertDialog.show();
    }
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + CustomAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    public static Long getTimeStamp(){
        return System.currentTimeMillis()/1000;
    }
}
