package com.bhola.chutlundsmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    private String TAG = "TAGA";
    TextView toggleSignUp;
    TextView toggleLogin;
    LinearLayout loginLayout, signUplayout;
    String loginEmail_text = "";
    String loginPassword_text = "";
    String email_text = "";
    String fullname_text = "";
    String password_text = "";
    String confirmPassword_text = "";
    ProgressDialog progressDialog;

    //Google
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    LinearLayout googleBtn;


    //Facebook
    CallbackManager callbackManager;


    //Credentials
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        toggleSignUp = findViewById(R.id.register);
        toggleLogin = findViewById(R.id.toggleLogin);
        loginLayout = findViewById(R.id.signInlayout);
        signUplayout = findViewById(R.id.registerlayout);
        toggleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                signUplayout.setVisibility(View.VISIBLE);
            }
        });
        toggleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.VISIBLE);
                signUplayout.setVisibility(View.GONE);
            }
        });

        signUpWithCredentials();
        loginWithCredentials();
        googleSignInStuffs();
        facebookSignInStuffs();

    }

    private void loginWithCredentials() {
        TextInputLayout loginEmail = findViewById(R.id.loginEmail);
        TextInputLayout loginPassword = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginBtn);


        loginEmail.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                loginEmail.setError("");

                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    loginEmail_text = s.toString();

            }
        });
        loginPassword.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                loginPassword.setError("");
                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    loginPassword_text = s.toString();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail.setError("");
                loginPassword.setError("");

                if (loginEmail_text.length() == 0) {
                    loginEmail.setError("Enter Email");
                    return;
                }
                if (loginPassword_text.length() == 0) {
                    loginPassword.setError("Enter Password");
                    return;
                }
                if (!isValid(loginEmail_text)) {
                    loginEmail.setError("Email not valid");
                    return;
                }
                progressDialog.show();


                firebaseAuth.signInWithEmailAndPassword(loginEmail_text, loginPassword_text).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.cancel();
                        Toast.makeText(login.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        LoginInComplete();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        if (e.getMessage().toString().trim().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            Toast.makeText(login.this, "User not registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


        TextView forgotpassword = findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginEmail_text.length() == 0) {
                    loginEmail.setError("Enter registered email");
                    return;
                }
                progressDialog.show();

                firebaseAuth.sendPasswordResetEmail(loginEmail_text).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.cancel();
                        Toast.makeText(login.this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        if (e.getMessage().toString().trim().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            Toast.makeText(login.this, "User not registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }                    }
                });

            }
        });

    }

    private void signUpWithCredentials() {
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout fullname = findViewById(R.id.fullname);
        TextInputLayout password = findViewById(R.id.password);
        TextInputLayout confirmPassword = findViewById(R.id.confirmPassword);
        Button signUpBtn = findViewById(R.id.signUpBtn);


        email.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                email.setError("");

                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    email_text = s.toString();

            }
        });
        fullname.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                fullname.setError("");

                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    fullname_text = s.toString();

            }
        });
        password.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                password.setError("");

                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    password_text = s.toString();

            }
        });
        confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                confirmPassword.setError("");

                //get the String from CharSequence with s.toString() and process it to validation
                if (s.toString() != null)
                    confirmPassword_text = s.toString();

            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullname_text.length() == 0) {
                    fullname.setError("Enter Full Name");
                    return;
                }
                if (email_text.length() == 0) {
                    email.setError("Enter Email");
                    return;
                }
                if (password_text.length() == 0) {
                    password.setError("Enter Password");
                    return;
                }
                if (confirmPassword_text.length() == 0) {
                    confirmPassword.setError("Enter Confirm Password");
                    return;
                }
                if (!password_text.equals(confirmPassword_text)) {
                    confirmPassword.setError("Password Not matched");
                    return;
                }

                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email_text, password_text).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        signUpComplete();
                        Toast.makeText(login.this, "login now", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        ArrayList<String> keyword = new ArrayList<>();
                        firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getUid()).set(new UserModel(fullname_text, email_text, SplashScreen.countryLocation, false, false, keyword));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    private void signUpComplete() {
        toggleLogin.performClick();
    }

    private void LoginInComplete() {
    }


    private void facebookSignInStuffs() {
        LinearLayout loginFacebook = findViewById(R.id.loginFacebook);
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(login.this, Arrays.asList("public_profile"));
            }
        });

        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if(accessToken!=null && !accessToken.isExpired()){
//            navigateToSecondActivity();
//        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "onSuccess: " + loginResult);
                        navigateToSecondActivity();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "onSuccess: " + exception.getMessage());
                    }
                });


        String EMAIL = "email";

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "loginButton: " + exception.getMessage());
            }
        });
    }

    private void googleSignInStuffs() {
        googleBtn = findViewById(R.id.loginGoolge);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToSecondActivity();
        }


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            //Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {

                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        } else {
            //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
    }
}

class UserModel {

    String fullname, email, country;
    boolean verified, membership;
    ArrayList<String> keywords;

    public UserModel() {
    }

    public UserModel(String fullname, String email, String country, boolean verified, boolean membership, ArrayList<String> keywords) {
        this.fullname = fullname;
        this.email = email;
        this.country = country;
        this.verified = verified;
        this.membership = membership;
        this.keywords = keywords;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isMembership() {
        return membership;
    }

    public void setMembership(boolean membership) {
        this.membership = membership;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
