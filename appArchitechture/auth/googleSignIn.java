package com.koshurTech.tadaa.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koshurTech.tadaa.MainActivity;
import com.koshurTech.tadaa.R;
import com.koshurTech.tadaa.loading;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class googleSignIn extends AppCompatActivity {


    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    SignInButton signInButton;

    loading loading;


    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;


    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbarSi);
        signInButton = (SignInButton) findViewById(R.id.googleSignInButton);


        setSupportActionBar(toolbarTop);
        try
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        catch (NullPointerException e){}




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(googleSignIn.this,gso);
        mAuth = FirebaseAuth.getInstance();


        loading = new loading(googleSignIn.this);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(googleSignIn.this);
                builder.setTitle("Information");
                builder.setMessage("Ta Daa App takes your personal public information from Google " +
                        "API which does not include your passwords or any other sensitive information. However " +
                        "if your google account gets compromised in future, Ta Daa / KoshurTech will not be responsible.");
                builder.setIcon(R.drawable.ic_baseline_info_24);
                builder.setPositiveButton(Html.fromHtml("<font color='#EAB543'>Accept</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loading.startLoadingAnimation();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                signIn();
                            }
                        },500);
                    }
                }).setNegativeButton(Html.fromHtml("<font color='#EAB543'>Decline</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });



                builder.show();


            }
        });

    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(googleSignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            firebaseFirestore = FirebaseFirestore.getInstance();
                            CollectionReference cr = firebaseFirestore.collection("users");

                            CollectionReference cr2 = cr.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userInformation");

                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = new Date();

                            documentReference = cr2.document(formatter.format(date));

                            Map<String, String> payload = new HashMap<String, String>();
                            payload.put("userName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            payload.put("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());


                            documentReference.set(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loading.dismissDialog();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    }
                                }
                            });



                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }












}