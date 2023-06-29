package com.example.statusia.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.example.statusia.Activitys.MainActivity.WhatsAppPackage;
import static com.example.statusia.Activitys.MainActivity.statusDir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.statusia.Activitys.PermissionManager;
import com.example.statusia.Adapters.StatusViewAdapter;
import com.example.statusia.R;
import com.example.statusia.Tools.PathMap;
import com.example.statusia.Tools.WhatsappSpinner;
import com.example.statusia.classes.WhatsappSpinnerItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Statuses extends Fragment {

    private static final int REQUEST_CODE2 = 202;
    RecyclerView ImagesRecycleView;
    public static Activity activity;
    int p =0;
    String TAG = "MainActivity/Images : ";
    MaterialButton openWhatsapp;
    MaterialButton multiButton;
    MaterialButton multiSaveBtn;
    ImageButton checkAllBtn;
    LinearLayout noImageStatus;
    Spinner spinner;
    WhatsappSpinner ws;
    Dialog dialog;

    ArrayList<String> selectedList;
    boolean isSelectModeEnabled;
    PathMap pathMap;
    ArrayList<String> StatusUriList;
    ProgressDialog progress;
    TextView imageFragHeading;

    public Statuses() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statuses, container, false);
        pathMap = new PathMap(getContext());
        activity = getActivity();
        StatusUriList = new ArrayList<>();
        selectedList = new ArrayList<>();
        isSelectModeEnabled = false;
        spinner = view.findViewById(R.id.whatsappTypeSpinner);
        ImagesRecycleView = view.findViewById(R.id.imagesRecycleView);
        noImageStatus = view.findViewById(R.id.noImageStatus);
        openWhatsapp = view.findViewById(R.id.openWhatsapp);
        multiButton = view.findViewById(R.id.openWhatsapp2);
        multiSaveBtn = view.findViewById(R.id.multiSaveBtn);
        checkAllBtn = view.findViewById(R.id.checkAllBtn);
        imageFragHeading = view.findViewById(R.id.imageFragHeading);

        progress = new ProgressDialog(getContext());
        progress.setMessage("Downloading images ...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);

        setOpenWhatsappBtn();

        ws = new WhatsappSpinner(requireContext(),spinner);
        ws.setSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ws.setSpinnerPosition(position);
                WhatsappSpinnerItem item = (WhatsappSpinnerItem) parent.getItemAtPosition(position);
                String name = item.getItemName();
                if(name.equals("WhatsApp")) WhatsAppPackage = "com.whatsapp";
                else if(name.equals("WhatsApp Business")) WhatsAppPackage = "com.whatsapp.w4b";
                isSelectModeEnabled = false;
                selectedList = new ArrayList<>();
                imageFragHeading.setText("Images");
                checkAllBtn.setVisibility(View.GONE);
                multiSaveBtn.setVisibility(View.GONE);
                loadStatusFile(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        multiSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: "+selectedList);
                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "Downloading...",
                        Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                saveImageFiles();
                snackbar.dismiss();
            }
        });

        checkAllBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(selectedList.size()== StatusUriList.size()){
                    isSelectModeEnabled = false;
                    selectedList = new ArrayList<>();
                    imageFragHeading.setText("Images");
                    checkAllBtn.setVisibility(View.GONE);
                    multiSaveBtn.setVisibility(View.GONE);
                }else {
                    isSelectModeEnabled = true;
                    selectedList = new ArrayList<>();
                    selectedList.addAll(StatusUriList);
                    imageFragHeading.setText(selectedList.size() + " Selected");
                }
                loadRecycleView(StatusUriList);
            }
        });

        dialog = new Dialog(getContext());

        return view;
    }

    public void saveImageFiles(){
        ArrayList<String> failedUriList = new ArrayList<>();
        for(String uri:selectedList) {
            String failedUri;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                failedUri = saveImageBelowR(Uri.parse(uri));
            } else {
                failedUri = saveImageR(Uri.parse(uri));
            }
            if(!failedUri.equals("NULL")) failedUriList.add(failedUri);
        }

        if(failedUriList.size()>0) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    "Failed to download "+failedUriList.size()+" images",
                    Snackbar.LENGTH_SHORT).show();


            for(String uri: selectedList){
                if(!failedUriList.contains(uri)){
                    selectedList.remove(uri);
                }
                sleep(100);
            }

            loadRecycleView(StatusUriList);
        }
        else{
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    "Files Saved successfully at: Downloads/.Statusia/Images",
                    Snackbar.LENGTH_SHORT).show();

            selectedList = new ArrayList<>();
            isSelectModeEnabled = false;
            checkAllBtn.setVisibility(View.GONE);
            multiSaveBtn.setVisibility(View.GONE);
            loadRecycleView(StatusUriList);
        }
    }

    public void sleep(int millis){
        try {
            Thread.sleep(millis);
        }catch (Exception e){
            Log.e(TAG, "sleep: "+e);
        }
    }

    public void loadStatusFile(int sPos){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
            loadImageStatusFilesR(sPos);
        }
        else{
            loadImageStatusFilesBelowR(sPos);
        }
    }

    public void setOpenWhatsappBtn(){
        openWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsapp();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setMultiButton(String type){
        if(type.equals("OPEN")){
            multiButton.setText("Open WhatsApp");
            multiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWhatsapp();
                }
            });
        }
        else{
            multiButton.setText("Allow Permission");
            multiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDirectory(ws.getSpinnerPosition());
                }
            });
        }
    }

    private void openWhatsapp(){
        Intent intent = requireActivity().getPackageManager().getLaunchIntentForPackage(WhatsAppPackage);
        startActivity(intent);
    }

    public void loadRecycleView(ArrayList<String> ImageUriList ){
        int spanCount;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            spanCount = 3;
        } else {
            // code for landscape mode
            spanCount = 5;
        }
        StatusViewAdapter adapter = new StatusViewAdapter(getContext(),ImageUriList,
                checkAllBtn,multiSaveBtn,imageFragHeading,selectedList,isSelectModeEnabled);
        ImagesRecycleView.setAdapter(adapter);
        ImagesRecycleView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));
    }

    public void loadImageStatusFilesBelowR(int sPos){
        StatusUriList = new ArrayList<>();
        if ((ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            startActivity(new Intent(getContext(),PermissionManager.class));
        }

        String imageStatusPath;
        String imageStatusPath2;

        if(sPos==0) {
            imageStatusPath = Environment.getExternalStorageDirectory().getPath()
                    + "/WhatsApp/Media/.Statuses";
            imageStatusPath2 = Environment.getExternalStorageDirectory()
                    + File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        }
        else{
            imageStatusPath = Environment.getExternalStorageDirectory().getPath()
                    + "/WhatsApp Business/Media/.Statuses";
            imageStatusPath2 = Environment.getExternalStorageDirectory()
                    + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses";
        }
        File imageFile = new File(imageStatusPath);
        File imageFile2 = new File(imageStatusPath2);

        File []imageFiles = imageFile.listFiles();
        File []imageFiles2 = imageFile2.listFiles();

//        ArrayList<String> imageUriList;
        if(imageFiles!=null) {
            StatusUriList = getImageUris(imageFiles);
        }
        else if(imageFiles2!=null){
            StatusUriList = getImageUris(imageFiles2);
            Log.d(TAG, "loadImageStatusFilesBelowR: "+ Arrays.toString(imageFiles2));
        }
        else StatusUriList = new ArrayList<>();

        if(StatusUriList.size()<=0){
            noImageStatus.setVisibility(View.VISIBLE);
            openWhatsapp.setVisibility(View.GONE);
        }
        else{
            noImageStatus.setVisibility(View.GONE);
            openWhatsapp.setVisibility(View.VISIBLE);
        }

        loadRecycleView(StatusUriList);
    }

    public ArrayList<String> getImageUris(File []imageFiles){
        ArrayList<String> list = new ArrayList<>();
        for (File f : imageFiles) {
            String fPath = f.getPath();
//            Log.e(TAG, "getImageUris: "+fPath);
//            if(fPath.endsWith(".jpg") || fPath.endsWith(".jpeg") || fPath.endsWith(".png")) {
                list.add(fPath);
//            }
        }
        return list;
    }

    public void loadImageStatusFilesR(int sPos){
        StatusUriList = new ArrayList<>();
        SharedPreferences preferences = requireContext().getSharedPreferences("FILE_URI",MODE_PRIVATE);
        String UriPath;
        if(sPos == 0) UriPath = preferences.getString("WHATSAPP_URI","NULL");
        else UriPath = preferences.getString("WHATSAPP_BUSINESS_URI","NULL");

        if(UriPath.equals("NULL")){
            openDialogForPermission(sPos);
            setMultiButton("PERMISSION");
        }
        else {
            setMultiButton("OPEN");
            Uri URI = Uri.parse(UriPath);

            DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(), URI);

            DocumentFile[] sFiles = new DocumentFile[0];
            if (documentFile != null) sFiles = documentFile.listFiles();

            for (DocumentFile doc : sFiles) {
                String docString = doc.getUri().toString();
                if(!docString.endsWith("nomedia")) {
                    StatusUriList.add(docString);
                }
            }
        }

        if (StatusUriList.size() <= 0) {
            noImageStatus.setVisibility(View.VISIBLE);
            openWhatsapp.setVisibility(View.GONE);
        } else {
            noImageStatus.setVisibility(View.GONE);
            openWhatsapp.setVisibility(View.VISIBLE);
        }

        loadRecycleView(StatusUriList);
    }

    @Override
    public void onResume() {
        super.onResume();
        ws.resetPreviousSpinnerPos();
        Log.e(TAG, "openDialogForPermission"+1);
    }

    public void openDialogForPermission(int sPos){
        dialog.setContentView(R.layout.permission_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView wsMedia = dialog.findViewById(R.id.wsMedia);
        String text;
        if(sPos == 0) text = "Allow access to <b>'WhatsApp Media'</b> Folder";
        else text = "Allow access to <b>'WhatsApp Business Media'</b> Folder";

        wsMedia.setText(Html.fromHtml(text));

        MaterialCardView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        MaterialCardView grantPermission = dialog.findViewById(R.id.grantPermission);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSnackBar("To Get All Status, Allow Access to 'Whatsapp Media' Folder",Snackbar.LENGTH_SHORT);
            }
        });

        grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDirectory(ws.getSpinnerPosition());
            }
        });

        if(!dialog.isShowing()) dialog.show();
    }

    public void openDirectory(int sPos) {
        String path;
        if(sPos == 0) {
            path = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        }else{
            path = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses";
        }
        File file = new File(path);
        String startDir = null, secondDir, finalDirPath;

        if (file.exists()) {
            if(sPos == 0) startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
            else startDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
        }

        StorageManager sm = (StorageManager) requireContext().getSystemService(Context.STORAGE_SERVICE);

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        }

        assert intent != null;
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();

        Log.d("TAG", "INITIAL_URI scheme: " + scheme);

        scheme = scheme.replace("/root/", "/document/");

        finalDirPath = scheme + "%3A" + startDir;

        uri = Uri.parse(finalDirPath);

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);

        Log.d("TAG", "uri: " + uri.toString());

        startActivityForResult(intent, REQUEST_CODE2);
    }

    public void showSnackBar(String text,int length){
        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), text, length);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE2 && resultCode==RESULT_OK){
            if (data != null) {
                Uri uri = data.getData();
                if (uri.getPath().endsWith(".Statuses")) {
                    Log.d("TAG", "onActivityResult: " + uri.getPath());

                    requireContext().getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    SharedPreferences preferences = requireContext().getSharedPreferences("FILE_URI",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    if (ws.getSpinnerPosition()==0) editor.putString("WHATSAPP_URI", String.valueOf(uri));
                    else editor.putString("WHATSAPP_BUSINESS_URI", String.valueOf(uri));
                    editor.apply();
                    loadImageStatusFilesR(ws.getSpinnerPosition());
                } else {
                    // dialog when user gave wrong path
                    showSnackBar("You Give wrong access, Try again",Snackbar.LENGTH_SHORT);
                }
            }

        }
        else{
            showSnackBar("To Get All Status, Allow Access to 'Whatsapp Media' Folder",Snackbar.LENGTH_SHORT);
        }
    }

    int conname =0;
    private String saveImageBelowR(Uri uri) {
        String failedUri = "NULL";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + (++conname) + ".jpg";

        try {
            File destFile = new File(statusDir.getPath(),fileName);
            File sourceFile = new File(uri.getPath());

            FileUtils.copyFile(sourceFile,destFile);
            if(destFile.setLastModified(System.currentTimeMillis())) Log.d(TAG, "saveImage: Successfully");;

            pathMap.savePathMap(new File(uri.getPath()).getName(),fileName);

            Log.e("Download URI",Uri.fromFile(destFile)+"");
            Log.e("Input URI",uri+"");
        }
        catch (Exception e){
            Log.e("Image download error",e.toString());
            failedUri = uri.toString();
        }

        return failedUri;
    }

    private String saveImageR(Uri uri) {
        String failedUri= "NULL";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        String fileName = "IMG_" + currentDateTime + (++conname) + ".jpg";

        try {
            File destFile = new File(statusDir.getPath(),fileName);
            InputStream in = requireContext().getContentResolver().openInputStream(uri);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(in,destFile);
            if(destFile.setLastModified(System.currentTimeMillis())) Log.d(TAG, "saveImage: Successfully");;
            in.close();

            pathMap.savePathMap(new File(uri.getPath()).getName(),fileName);

            Log.e("Download URI",Uri.fromFile(destFile)+"");
            Log.e("Input URI",uri+"");
        }
        catch (Exception e){
            Log.e("Image download error",e.toString());
            failedUri = uri.toString();
        }
        return failedUri;
    }
}