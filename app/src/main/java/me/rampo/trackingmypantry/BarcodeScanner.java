package me.rampo.trackingmypantry;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BarcodeScanner extends Fragment {
    Context context;
    Bundle b;
    TextView text;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.barcode, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getContext();
        b = getArguments();

        text = view.findViewById(R.id.camera_text);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        SurfaceView surfaceView = view.findViewById(R.id.camera);
        CameraSource cameraSource = new CameraSource.Builder(context, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcode = detections.getDetectedItems();

                if (barcode.size() != 0) {
                    String brcode = barcode.valueAt(0).displayValue;
                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1000);
                            text.setText(brcode);
                        }
                    });
                }
            }
        });
        view.findViewById(R.id.camera_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.putString("qrCode", text.getText().toString());
                NavHostFragment.findNavController(BarcodeScanner.this).navigate(R.id.action_Qrcode_Home, b);

            }
        });
        view.findViewById(R.id.camera_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(BarcodeScanner.this).navigate(R.id.action_Qrcode_Home, b);

            }
        });

    }
}


