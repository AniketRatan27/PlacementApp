package com.example.placementapp;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.S;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


public class resourcefgm extends Fragment {

    private ImageView aptitudeDownload, faqDownload, resumeDownload, interviewDownload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_resourcefgm, container, false);

        // intialization
        aptitudeDownload =view.findViewById(R.id.aptitude);
        faqDownload = view.findViewById(R.id.faq);
        resumeDownload = view.findViewById(R.id.resume);
        interviewDownload = view.findViewById(R.id.interview);

        // storing url

        String aptitude="https://firebasestorage.googleapis.com/v0/b/signup1-e0bf2.appspot.com/o/RS%20AGGARWAL%20QUANTITATIVE%20APTITUDE%20NEW%20EM.pdf?alt=media&token=c5d7c0bb-ff80-4f8f-aed1-cef379f7a165";
        String resume="https://firebasestorage.googleapis.com/v0/b/signup1-e0bf2.appspot.com/o/Basic_Resume.pdf?alt=media&token=dade4ff5-3dcd-44a8-ac30-b431b9dcd5f0";
        String faq="https://firebasestorage.googleapis.com/v0/b/signup1-e0bf2.appspot.com/o/FAQS%20EDITED.pdf?alt=media&token=acac8f0a-a278-4a82-84b1-666a5ab06e99";
        String interview="https://firebasestorage.googleapis.com/v0/b/signup1-e0bf2.appspot.com/o/How%20to%20answer%2064%20Toughest%20Interview%20Questions.pdf?alt=media&token=5b63794e-3daf-4ded-9f16-045b603ea40f";

        aptitudeDownload.setOnClickListener(v -> downloadPDF("Aptitude",aptitude));
        faqDownload.setOnClickListener(v -> downloadPDF("FAQ",resume));
        resumeDownload.setOnClickListener(v -> downloadPDF("Resume",faq));
        interviewDownload.setOnClickListener(v -> downloadPDF("Interview",interview));



        return view;
    }

    private void downloadPDF(String filename,String fileurl) {
        Context context = getContext();
        if (context != null) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(fileurl);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(filename);
            request.setDescription("Downloading " + filename);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(context, "Downloading " + filename, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

        }
