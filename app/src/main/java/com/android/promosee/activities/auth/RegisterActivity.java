package com.android.promosee.activities.auth;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.promosee.R;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Session;
import com.android.promosee.core.Validators;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name_edittext) EditText nameEditText;
    @BindView(R.id.email_edittext) EditText emailEditText;
    @BindView(R.id.password_edittext) EditText passwordEditText;
    @BindView(R.id.confirm_password_edittext) EditText confirmPasswordEditText;
    @BindView(R.id.city_edittext) EditText cityEditText;
    @BindView(R.id.photo) ImageView photo;

    private final static int PERMISSION_CAMERA = 1, CAMERA = 2, GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_button)
    public void registerButtonOnClick() {
        if (!Validators.validateLength(nameEditText, 3))
            Alert.dialog(this, "Name is required");
        else if (!Validators.validateEmail(emailEditText))
            Alert.dialog(this, "Email not valid ");
        else if (!Validators.validateLength(passwordEditText, 4))
            Alert.dialog(this, "Password minimum 4 character");
        else if (!Validators.validateLength(confirmPasswordEditText, 4))
            Alert.dialog(this, "Confirm password is required");
        else if (!Validators.validateLength(cityEditText, 1))
            Alert.dialog(this, "City is required");
        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()))
            Alert.dialog(this, "Password is mismatch");
        else {
            JSONObject params = API.getBaseJSONParams(this);
            try {
                params.put("name", nameEditText.getText().toString());
                params.put("email", emailEditText.getText().toString());
                params.put("phone", "");
                params.put("password", passwordEditText.getText().toString());
                params.put("city", cityEditText.getText().toString());
                params.put("refferal_code", "");
                params.put("fb_token", "");
                params.put("gcm_token", new Preferences(this).getString("fcmToken"));
            } catch (JSONException e) {
                return;
            }

            Session.register(this, params, null);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            selectImage();
        else
            Alert.dialog(this, getResources().getString(R.string.ignore_camera_permission));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        photo.setImageBitmap(thumbnail);
    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        photo.setImageBitmap(bm);
    }

    @OnClick(R.id.add_icon)
    public void addIconOnClick() {
        checkPermissionCamera();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions =  {android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA);
        }
        else
            selectImage();
    }
}
