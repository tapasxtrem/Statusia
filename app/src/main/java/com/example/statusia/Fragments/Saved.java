package com.example.statusia.Fragments;

import static com.example.statusia.Activitys.MainActivity.statusDir;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.statusia.Adapters.SavedViewAdapter;
import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Saved extends Fragment {
    RecyclerView SavedRecycleView;
    MaterialButton howToUseBtn;
    public static Activity activity;
    LinearLayout noSavedStatus;

    ArrayList<String> selectedList;
    boolean isSelectModeEnabled;
    PathMap pathMap;
    ArrayList<String> FileUriList;
    ProgressDialog progress;
//    TextView savedFragHeading;
//    MaterialButton deleteBtn;
//    ImageButton sCheckAllBtn;

    public Saved() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        SavedRecycleView = view.findViewById(R.id.savedRecycleView);
        howToUseBtn = view.findViewById(R.id.howToUseBtn);
        noSavedStatus = view.findViewById(R.id.noSavedStatus);
//        savedFragHeading = view.findViewById(R.id.savedFragHeading);
//        deleteBtn = view.findViewById(R.id.deleteBtn);
//        sCheckAllBtn = view.findViewById(R.id.sCheckAllBtn);

        activity = getActivity();

        pathMap = new PathMap(getContext());
        FileUriList = new ArrayList<>();
        selectedList = new ArrayList<>();
        isSelectModeEnabled = false;

        progress = new ProgressDialog(getContext());
        progress.setMessage("Downloading images ...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);

        loadSavedStatusFiles();

        howToUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogHowToUse();
            }
        });

//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "onClick: "+selectedList);
//                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
//                        "Deleting...",
//                        Snackbar.LENGTH_INDEFINITE);
//                snackbar.show();
////                deleteFiles();
//                snackbar.dismiss();
//            }
//        });
//
//        sCheckAllBtn.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View v) {
//                if(selectedList.size()==FileUriList.size()){
//                    isSelectModeEnabled = false;
//                    selectedList = new ArrayList<>();
//                    savedFragHeading.setText("Images");
//                    sCheckAllBtn.setVisibility(View.GONE);
//                    deleteBtn.setVisibility(View.GONE);
//                }else {
//                    isSelectModeEnabled = true;
//                    selectedList = new ArrayList<>();
//                    selectedList.addAll(FileUriList);
//                    savedFragHeading.setText(selectedList.size() + " Selected");
//                }
//                loadRecycleView(FileUriList);
//            }
//        });

        return view;
    }

//    public void deleteFiles(){
//        ArrayList<String> failedUriList = new ArrayList<>();
//        for(String uri:selectedList) {
//            String failedUri;
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                failedUri = saveImageBelowR(Uri.parse(uri));
//            } else {
//                failedUri = saveImageR(Uri.parse(uri));
//            }
//            if(!failedUri.equals("NULL")) failedUriList.add(failedUri);
//        }
//
//        if(failedUriList.size()>0) {
//            Snackbar.make(requireActivity().findViewById(android.R.id.content),
//                    "Failed to download "+failedUriList.size()+" images",
//                    Snackbar.LENGTH_SHORT).show();
//
//
//            for(String uri: selectedList){
//                if(!failedUriList.contains(uri)){
//                    selectedList.remove(uri);
//                }
//                sleep(100);
//            }
//
//            loadRecycleView(ImageUriList);
//        }
//        else{
//            Snackbar.make(requireActivity().findViewById(android.R.id.content),
//                    "Files Saved successfully at: Downloads/.Statusia/Images",
//                    Snackbar.LENGTH_SHORT).show();
//
//            selectedList = new ArrayList<>();
//            isSelectModeEnabled = false;
//            checkAllBtn.setVisibility(View.GONE);
//            multiSaveBtn.setVisibility(View.GONE);
//            loadRecycleView(ImageUriList);
//        }
//    }

    public void openDialogHowToUse(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.how_to_use_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button gotIt = dialog.findViewById(R.id.got_it);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadSavedStatusFiles() {
        File imageFile = new File(statusDir.getPath());
        File []savedFiles = imageFile.listFiles();

        ArrayList<String> fileUris = new ArrayList<>();

        ArrayList<File> fileList = new ArrayList<>();
        if(savedFiles!=null) {
            fileList.addAll(Arrays.asList(savedFiles));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileList.sort(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o1.lastModified(), o2.lastModified());
                }
            });
        }

        for (int i =fileList.size()-1;i>=0;i--){
            File f = fileList.get(i);
            fileUris.add(f.getPath());
        }

        if(fileUris.size()<=0){
            noSavedStatus.setVisibility(View.VISIBLE);
        }
        else{
            noSavedStatus.setVisibility(View.GONE);
        }

        loadRecycleView(fileUris);
    }

    public void loadRecycleView(ArrayList<String> SavedUriList ){
        int spanCount;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            spanCount = 3;
        } else {
            // code for landscape mode
            spanCount = 5;
        }
        SavedViewAdapter adapter = new SavedViewAdapter(getContext(),SavedUriList);
        SavedRecycleView.setAdapter(adapter);
        SavedRecycleView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        loadSavedStatusFiles();
    }
}