package com.example.miniapp.views;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miniapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BlankFragment extends Fragment {
    Uri imageUri;
    ImageView imageView;
    Drawable drawable;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(String stringUri) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString("stringUri", stringUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            imageUri = Uri.parse(getArguments().getString("stringUri"));
            Log.v("MY TAG", "shit to load: " + imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        imageView = rootView.findViewById(R.id.fragment_image_view);
        imageView.setImageURI(imageUri);

        return rootView;
    }
}