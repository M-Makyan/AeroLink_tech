package com.example.aerolink;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button btnLogout, saveButton, btnViewMyPosts;
    private TextView userNameTextView, emailTextView;
    private ImageView profileImageView;
    private Switch notificationsSwitch;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        profileImageView = view.findViewById(R.id.profileImageView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        emailTextView    = view.findViewById(R.id.emailTextView);
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        saveButton      = view.findViewById(R.id.saveButton);
        btnLogout       = view.findViewById(R.id.btnLogout);
        btnViewMyPosts  = view.findViewById(R.id.btnViewMyPosts);  // Correct ID

        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        profileImageView.setOnClickListener(v -> showProfileImageOptionsDialog());

        loadPreferences();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userNameTextView.setText(currentUser.getDisplayName());
            emailTextView.setText(currentUser.getEmail());
            loadProfileImage(currentUser);
        }

        saveButton.setOnClickListener(v -> savePreferences());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        btnViewMyPosts.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyPostsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void showProfileImageOptionsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Profile Picture Options")
                .setItems(new CharSequence[]{"Change Picture", "Delete Picture"}, (dialog, which) -> {
                    if (which == 0) showImagePickerDialog();
                    else deleteProfilePicture();
                })
                .show();
    }

    private void deleteProfilePicture() {
        profileImageView.setImageResource(R.drawable.ohms_law);
        Toast.makeText(getContext(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        } else openCamera();
                    } else openGallery();
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST);
    }

    private void loadPreferences() {
        boolean isDark = sharedPreferences.getBoolean("dark_mode", false);
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            try {
                Uri selectedUri = data.getData();
                if (requestCode == PICK_IMAGE_REQUEST && selectedUri != null) {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedUri);
                    profileImageView.setImageBitmap(bmp);
                } else if (requestCode == TAKE_PHOTO_REQUEST) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    profileImageView.setImageBitmap(photo);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications_enabled", notificationsSwitch.isChecked());
        editor.putString("user_name", userNameTextView.getText().toString());
        editor.putString("user_email", emailTextView.getText().toString());
        editor.apply();
        Toast.makeText(getContext(), "Settings saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void loadProfileImage(FirebaseUser user) {
        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.ohms_law);
        }
    }
}
