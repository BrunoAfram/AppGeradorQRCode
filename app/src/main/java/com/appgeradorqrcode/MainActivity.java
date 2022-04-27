package com.appgeradorqrcode;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editQRCode;
    private Button btnGenerateQRCode;
    private Button btnShare;
    private ImageView imgQRCode;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initComponents();
        btnGenerateQRCode.setOnClickListener(view -> {
            if (TextUtils.isEmpty(editQRCode.getText().toString())) {
                editQRCode.setError("Campo Inválido");
                editQRCode.requestFocus();
            } else {
                generateQrCode(editQRCode.getText().toString());
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        btnShare.setOnClickListener(view -> {
            shareQRCode();
        });
    }

    private void shareQRCode() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "qrcode.jpg");
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/qrcode.jpg"));
        startActivity(Intent.createChooser(share, "Compartilhar QR Code"));
    }


    private void generateQrCode(String string) {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {

            BitMatrix bitMatrix = qrCodeWriter.encode(string, BarcodeFormat.QR_CODE, 300, 300);

            int width = bitMatrix.getWidth();
            int Height = bitMatrix.getHeight();
            Bitmap _bitmap = Bitmap.createBitmap(width, Height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width ; x++){

                for (int y = 0; y < Height; y++){

                    _bitmap.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);

                }

            }
            bitmap = _bitmap;
            imgQRCode.setImageBitmap(_bitmap);
            btnShare.setVisibility(View.VISIBLE);

        }catch (WriterException e){
            e.printStackTrace();
        }

    }

    private void initComponents() {

        editQRCode = findViewById(R.id.editQRCode);
        btnGenerateQRCode = findViewById(R.id.btnGenerateQRCode);
        btnShare = findViewById(R.id.btnShare);
        imgQRCode = findViewById(R.id.imgQRCode);

    }
}