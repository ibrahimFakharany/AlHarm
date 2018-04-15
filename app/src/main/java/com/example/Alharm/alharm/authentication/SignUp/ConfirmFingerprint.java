package com.example.Alharm.alharm.authentication.SignUp;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
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
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.example.Alharm.alharm.authentication.LogIn.ConfirmCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ConfirmFingerprint extends AppCompatActivity {
    User user;
    String user_type;
    FirebaseAuth firebaseAuth;

    ImageView fingerprintImage;
    static boolean isConfirmed = false;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private FingerprintHandler fingerprintHandler;

    private static final String FINGERPRINT_KEY = "key_name";
    private static final int REQUEST_USE_FINGERPRINT = 300;
    String PAGE_TYPE = "";

    String firebaseKey = "";
    Bundle bundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_fingerprint);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = this.getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {


            PAGE_TYPE = bundle.getString(ConfirmCode.PAGE_TYPE_KEY);
            if (PAGE_TYPE.equals(ConfirmCode.PAGE_TYPE_CONFIRMATION_CODE)) {

                firebaseKey = bundle.getString("firebaseKey");


            }
            user = new User();
            user_type = bundle.getString("user type");
            user = (User) bundle.getSerializable("user Data");


            fingerprintHandler = new FingerprintHandler(this);

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);


            // check support for android fingerprint on device
            checkDeviceFingerprintSupport();
            //generate fingerprint keystore
            generateFingerprintKeyStore();
            //instantiate Cipher class
            Cipher mCipher = instantiateCipher();
            if (mCipher != null) {
                cryptoObject = new FingerprintManager.CryptoObject(mCipher);
            }
            fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
            fingerprintImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fingerprintHandler.completeFingerAuthentication(fingerprintManager, cryptoObject);
                    Toast.makeText(ConfirmFingerprint.this, "يمكنك الأن مسح بصمتك\nبرجاء وضع إصبعك على ماسح البصمة", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private void checkDeviceFingerprintSupport() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, REQUEST_USE_FINGERPRINT);
        } else {
            if (!fingerprintManager.isHardwareDetected()) {
                Toast.makeText(ConfirmFingerprint.this, "مسح البصمة غير متور على هذا الجهاز !", Toast.LENGTH_LONG).show();
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(ConfirmFingerprint.this, "لم يتم وضع بصمة على هذا الجهاز بعد !\nمن فضلك ادخل بصمتك من الإعدادات حتي يمكننا استخراجها هنا للتسجيل", Toast.LENGTH_LONG).show();
            }
            if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(ConfirmFingerprint.this, "لم يتم تفعيل قفل الشاشة بالبصمة .. برجاء التفعيل", Toast.LENGTH_LONG).show();
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

        private static final String TAG = FingerprintHandler.class.getSimpleName();

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

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
// اذا تمت مطابقة البصمة بنجاح
            // يتم تغيير قيمة المتغير  isConfirmed الى true
            isConfirmed = true;
            Toast.makeText(context, "تم تسجيل البصمة بنجاح", Toast.LENGTH_SHORT).show();
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


    // تسجيل بيانات المستخدم
    private void RegisterUser(String useremail, String userpass) {
        // يتم اظهار  ProgressDialog حتي ينتظر المستخدم تسجيل بياناتة
        final ProgressDialog progressDialog1 = new ProgressDialog(ConfirmFingerprint.this);
        progressDialog1.setMessage("يتم تسجيل بيانات المستخدم ..");
        progressDialog1.show();
        // تسجيل البيانات داخل ال firebase عن طريق  createUserWithEmailAndPassword
        firebaseAuth.createUserWithEmailAndPassword(useremail, userpass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // اذا تم التسجيل بنجاح يتم استدعاء فنكشن  storeUserData() لتخزين البيانات داخل قواعد البيانات
                            String uId = task.getResult().getUser().getUid();
                            storeUserData(uId);
                            progressDialog1.dismiss();
                        } else {
                            // اذا لم يتم التسجيل بنجاح يتم اخبار المستخدم
                            progressDialog1.dismiss();
                            Toast.makeText(ConfirmFingerprint.this, "لا يمكن التسجيل الأن !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // نخزين البيانات داخل قواعد البيانات
    private void storeUserData(String key) {
        if (bundle.getString(ConfirmCode.PAGE_TYPE_KEY).equals(ConfirmCode.PAGE_TYPE_CONFIRMATION_CODE)) {
            Task<Void> task = FirebaseDatabase.getInstance().getReference().getRoot().child(user.getUserType()).child(firebaseKey).removeValue();
            if (task != null) {

                Log.e("hema", "successfull deleted");

            }
        }
        user.setState("authorized");
        // الحصول على بيانات المستخدم على شكل  HashMap
        HashMap<String, Object> userMap = user.toMap();

        DatabaseReference Root = FirebaseDatabase.getInstance().getReference().getRoot();
        // Root.child("Users").child(user_type).push().getKey();
        // انشاء key  جديد للمستخدم
        String temp_key = key;
        HashMap<String, Object> childUpdate = new HashMap<String, Object>();
        // وضع بيانات المستخدم داخل ال  childUpdate
        childUpdate.put("/Users/" + user_type + "/" + temp_key, userMap);

        // وضع بيانات المستخدم داخل قواعد البيانات
        Root.updateChildren(childUpdate);

        Gson gson = ((CustomApplication) getApplication()).getGsonObject();
        String userDataString = gson.toJson(user);
        CustomSharedPreference pref = ((CustomApplication) getApplication()).getShared();
        pref.setUserData(userDataString);

        pref.setUserID(temp_key);
        // وضع حالة تسجيل دخول المستخدم ب true للتأكيد ان المستخدم قام بتسجيبل الدخول
        pref.setUserLogInState("true");
        //وضع نوع المستخدم داخل ال  SharedPreference
        pref.setUserType(user_type);
        // الانتقال الى الصفحة الرئيسية
        Intent intent = new Intent(ConfirmFingerprint.this, Main_Page.class);
        startActivity(intent);
        ConfirmFingerprint.this.finish();
    }


    // عند الضغط على زر تسجيل
    public void signUp(View view) {
        // اذا كان المتغير isConfirmed يساوي true والذي يعني ان المستخدم قام بتأكيد بصمتة
        if (isConfirmed)
            // نستدعي فنكشن  RegisterUser والتي تقوم بتسجيل المستخدم داخل ال firebase
            RegisterUser(user.getEmail(), user.getPassword());
        else
            Toast.makeText(this, "لم يتم تسجيل البصمة بعد .. برجاء الضغط على صورة البصمة ثم ضع اصبعك على مكان البصة بالجهاز", Toast.LENGTH_LONG).show();

    }


}