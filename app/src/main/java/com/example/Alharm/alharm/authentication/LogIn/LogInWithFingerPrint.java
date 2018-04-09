package com.example.Alharm.alharm.authentication.LogIn;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Alharm.alharm.Main_Page;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.example.Alharm.alharm.authentication.SignUp.ConfirmFingerprint;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LogInWithFingerPrint extends AppCompatActivity {


    ImageView fingerprintImage;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private FingerprintHandler fingerprintHandler;

    private static final String FINGERPRINT_KEY = "key_name";
    private static final int REQUEST_USE_FINGERPRINT = 300;


    protected static CustomSharedPreference mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_with_finger_print);

        mPref = ((CustomApplication)getApplication()).getShared();

        fingerprintHandler = new FingerprintHandler(this);

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);


        // التحقق من ان هذا الجهاز يدعم البصمة
        checkDeviceFingerprintSupport();

        //انشاء  keystore للبصمة
        generateFingerprintKeyStore();

        Cipher mCipher = instantiateCipher();
        if (mCipher != null) {
            cryptoObject = new FingerprintManager.CryptoObject(mCipher);
        }
        fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);

       // عند الضغط على صورة البصمة
        fingerprintImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
                Toast.makeText(LogInWithFingerPrint.this, "يمكنك الأن مسح بصمتك\nبرجاء وضع إصبعك على ماسح البصمة", Toast.LENGTH_LONG).show();

            }
        });

    }
    private void checkDeviceFingerprintSupport() {
       // التحقق من ان الجهاز يدعم البصمة وان المستخدم قد اعطي الصلاحيات لاستخدامها
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, REQUEST_USE_FINGERPRINT);
        } else {
            // اذا كان الجهاز لا يدعم البصمة
            if (!fingerprintManager.isHardwareDetected()) {
                Toast.makeText(LogInWithFingerPrint.this, "مسح البصمة غير متور على هذا الجهاز !", Toast.LENGTH_LONG).show();
            }
            // اذا لم يكن المستخدم قد قام بتسجيل بصمتة على الجهاز
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(LogInWithFingerPrint.this, "لم يتم وضع بصمة على هذا الجهاز بعد !\nمن فضلك ادخل بصمتك من الإعدادات حتي يمكننا استخراجها هنا للتسجيل", Toast.LENGTH_LONG).show();
            }
           // اذا لم يقم المستخدم بتفعيل البصمة
            if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(LogInWithFingerPrint.this, "لم يتم تفعيل قفل الشاشة بالبصمة .. برجاء التفعيل", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }


    private void generateFingerprintKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator.init(new KeyGenParameterSpec.Builder(FINGERPRINT_KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        keyGenerator.generateKey();
    }


    private Cipher instantiateCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(FINGERPRINT_KEY, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableKeyException |
                CertificateException | IOException | KeyStoreException | InvalidKeyException e) {
            throw new RuntimeException("Failed to instantiate Cipher class");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_USE_FINGERPRINT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // check support for android fingerprint on device
                checkDeviceFingerprintSupport();
                //generate fingerprint keystore
                generateFingerprintKeyStore();
                //instantiate Cipher class
                Cipher mCipher = instantiateCipher();
                if (mCipher != null) {
                    cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                }
            } else {
                Toast.makeText(this, "تم رفض السماح بإستخدام البصمة", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "طلب غير معروف", Toast.LENGTH_LONG).show();
        }
    }

    public static class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private static final String TAG = ConfirmFingerprint.FingerprintHandler.class.getSimpleName();

        private Context context;

        public FingerprintHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(context, "لم يتم تسجيل البصمة !", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }


        // اذا تمت مطابقة البصمة التي ادخلها المستخدم بالبصمة التي على الهاتف الذي قام بالتسجيل بها
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
           // اذا لم تكن بيانات المستخدم من ال sharedPrefrance فارغة .. يعني ان المستخدم لم يقم بالتسجيل
            if (mPref.getUserData()!=null) {
               // تأكيد ان المستخد قام بتسجيل الدخول
                mPref.setUserLogInState("true");
                Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
               //الانتقال الى الصفحة الرئيسية
                Intent userIntent = new Intent(context, Main_Page.class);
                context.startActivity(userIntent);
            }
            else
             Toast.makeText(context, "لم تقم بالتسجيل .. برجاء الذهاب لصفحة التسجيل لإنشاء حساب !", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }

        public void completeFingerAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try {
                fingerprintManager.authenticate(cryptoObject, new CancellationSignal(), 0, this, null);
            } catch (SecurityException ex) {
                Log.d(TAG, "An error occurred:\n" + ex.getMessage());
            } catch (Exception ex) {
                Log.d(TAG, "An error occurred\n" + ex.getMessage());
            }
        }
    }


}
