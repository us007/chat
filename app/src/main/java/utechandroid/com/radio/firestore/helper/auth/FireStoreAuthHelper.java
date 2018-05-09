package utechandroid.com.radio.firestore.helper.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;

import utechandroid.com.radio.ui.signup.SignUpInterface;

/**
 * Created by Utsav Shah on 07-Oct-17.
 */

public class FireStoreAuthHelper {

    private Activity mactivity;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    public FireStoreAuthHelper(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FireStoreAuthHelper(Activity activity) {
        this.mactivity = activity;
        SetProgressDialog();
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void SetProgressDialog() {
        mProgressDialog = new ProgressDialog(mactivity);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
    }

    private void ShowProgressDialog() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void DismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void SignUpWithEmail(String email, String password) {
        ShowProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mactivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DismissProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(mactivity, "Successfully Registered...",
                                    Toast.LENGTH_SHORT).show();
                            ((SignUpInterface) mactivity).LayoutUpdateChange();
                            //     updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //    Log.e("createUser:failure", "" + task.getException());
                            Toast.makeText(mactivity, "" + task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void SignInWithEmail(String email, String password) {
        ShowProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mactivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(mactivity, "Successfully Login...",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(mactivity, HomeActivity.class);
                            mactivity.startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mactivity, "" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void ForgotPasswordWithEmail(String email, final AlertDialog alertDialog) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Toast.makeText(mactivity, "Check your email for Reset Password...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void SetNamePhoto(String name, Uri uri) {
        ShowProgressDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates;
      /*  if (uri != null && !uri.equals(Uri.EMPTY)){
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
        }else {*/
        profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build();
        // }

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        DismissProgressDialog();
                        if (task.isSuccessful()) {
                            FirebaseUser user1 = mAuth.getCurrentUser();
                            String userUrl = null;
                            if (user1.getPhotoUrl() != null) {
                                userUrl = user1.getPhotoUrl().toString();
                            } else {
                                userUrl = "";
                            }
                            User userdetail = new User(user1.getUid(), user1.getDisplayName(),
                                    user1.getEmail(), userUrl, new Date());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(user1.getUid())
                                    .set(userdetail)
                                    .addOnSuccessListener(aVoid -> {
                                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Error adding document", e + "");
                                        }
                                    });
                            Toast.makeText(mactivity, "Your Profile Updated...",
                                    Toast.LENGTH_SHORT).show();
                            mactivity.startActivity(new Intent(mactivity, HomeActivity.class));
                        }
                    });
        }
    }

    public String GetUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.getUid();

    }

    public String GetUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.getDisplayName();
    }

//    public void SignOut() {
//        mAuth.signOut();
//        Intent i = new Intent(mactivity, SignActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        mactivity.startActivity(i);
//    }

    public void CheckLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(mactivity, HomeActivity.class);
            mactivity.startActivity(i);
        }
    }
}
