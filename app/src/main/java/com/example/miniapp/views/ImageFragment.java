package com.example.miniapp.views;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.miniapp.R;

public class ImageFragment extends Fragment {
    Uri imageUri;
    ImageView imageView;


    public ImageFragment() {

    }

    public static ImageFragment newInstance(String stringUri) {
        ImageFragment fragment = new ImageFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = rootView.findViewById(R.id.fragment_image_view);
        imageView.setImageURI(imageUri);

        return rootView;
    }
}