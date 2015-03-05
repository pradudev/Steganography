package stegnography.example.com.steganography;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class ImageSteganography extends ActionBarActivity {


    private static final int CAPTURE_IMAGE = 1;
    public static final int SELECT_IMAGE = 2;
    AlertDialog.Builder alertBuilder;
    AlertDialog alert;
    ImageView addImage;
    Random random;
    Bitmap selectedImage;
    String imagePath;
    Button encrypt;
    Button decrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_steganography);
        encrypt = (Button) findViewById(R.id.encrypt);
        encrypt.setEnabled(false);
        decrypt = (Button) findViewById(R.id.decrypt);
        decrypt.setEnabled(false);
        random = new Random();
        addImage = (ImageView) findViewById(R.id.working_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoChooseOption();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_steganography, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            encrypt.setEnabled(true);
            decrypt.setEnabled(true);
            setUpImage(requestCode, data);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpImage(int requestCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == CAPTURE_IMAGE) {
            String path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/default.png";
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile
                        .getAbsolutePath());
                selectedImage = bitmap;
                imagePath = path;
                addImage.setPadding(0, 0, 0, 0);
                addImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                addImage.setImageBitmap(bitmap);

            }
        } else if (requestCode == SELECT_IMAGE) {

            Bitmap bitmap = getBitmapFromCameraData(data, this);
            // bitmap = convertImageSquare(bitmap);
            selectedImage = bitmap;
            addImage.setImageBitmap(bitmap);

        }
    }

    public Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imagePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return bitmap;
    }


    public void showPhotoChooseOption() {

        alertBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(
                R.layout.dialog_select_image, null);
        alertBuilder.setView(v);
        Button camera = (Button) v.findViewById(R.id.camera);

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                alert.dismiss();
                startCam();
            }
        });

        Button gallery = (Button) v.findViewById(R.id.gallery);

        gallery.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                alert.dismiss();
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {
                    startActivityForResult(i, SELECT_IMAGE);
                } catch (Exception e) {
                    e.printStackTrace();


                }

            }
        });

        alert = alertBuilder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    Uri outputFileUri;

    public void startCam() {

        String file = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/stegno_cam.png";
        imagePath = file;
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputFileUri = Uri.fromFile(newfile);
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
