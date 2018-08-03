package casier.billsplitter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;
import casier.billsplitter.Model.UserInfo;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.fabMenu) FloatingActionMenu fabMenu;
    @BindView(R.id.fabCamera) com.github.clans.fab.FloatingActionButton fabCamera;
    @BindView(R.id.fabText) com.github.clans.fab.FloatingActionButton fabText;
    @BindView(R.id.scanResult) TextView scanResults;
    @BindView(R.id.scanList) ListView scanList;
    @BindView(R.id.coordinator) CoordinatorLayout coordinator;

    private EditText billAmount;
    private EditText billName;

    private static final String TAG = "MainActivityDebug";
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final int PHOTO_REQUEST = 10;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    private Uri imageUri;
    private TextRecognizer detector;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    private BillArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(false);
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });

        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBill(null);
                fabMenu.close(false);
            }
        });

        scanList.setOnItemClickListener(this);

        User user = new User();
        user.setUserEmail(UserInfo.getInstance().getUserEmail());
        user.setUserName(UserInfo.getInstance().getUserName());
        user.setUserPhotoUrl(UserInfo.getInstance().getUserPhotoUrl().toString());

        mDatabase= FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");

        myRef.child(UserInfo.getInstance().getUserId()).setValue(user);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /* TODO Remove this
        it's just a how to for SELECT in Firebase
     */
    private void displayInfo(){
        mDatabase.getReference("users").orderByChild(UserInfo.getInstance().getUserId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getValue();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    final ArrayList<String> list = new ArrayList<>();
                    for (int index = 0; index < textBlocks.size(); index++) {
                        TextBlock tBlock = textBlocks.valueAt(index);
                        for (Text line : tBlock.getComponents()) {
                            // Check if the line contains at least 2 numbers
                            String pattern = "(.*[0-9]{2,}.*)";
                            if(line.getValue().matches(pattern)){
                                for(int c = 0; c < currency.size(); c++){
                                    if (line.getValue().contains(currency.get(c))){
                                        list.add(line.getValue());
                                    }
                                }
                            }
                        }
                    }
                    if (list.size() < 1) {
                        Toast.makeText(this, "Scan Failed: Found nothing to scan", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new BillArrayAdapter(this, R.layout.row_layout, list);
                        scanList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String lineValue = adapterView.getItemAtPosition(i).toString();
        addBill(lineValue);
    }

    private void addBill(String value){
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Nouvelle facture")
                .customView(R.layout.dialog_add_bill, wrapInScrollView)
                .positiveText("Ajouter")
                .negativeText("Annuler")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Date date = Calendar.getInstance().getTime();
                        billAmount = dialog.getCustomView().findViewById(R.id.bill_amount);
                        billName = dialog.getCustomView().findViewById(R.id.bill_name);
                        Bill bill = new Bill(date.toString(), billName.getText().toString(), UserInfo.getInstance().getUserId(), convertToFloatedString(billAmount.getText().toString()));

                        DatabaseReference myRef = mDatabase.getReference("bills");
                        myRef.child(date.toString()).setValue(bill);
                    }
                })
                .show();

        if(value != null) {
            billAmount = dialog.getCustomView().findViewById(R.id.bill_amount);
            billAmount.setText(convertToFloatedString(value)); // TODO traiter le texte pour afficher uniquement la valeur sans la currency
        }
    }

    private String convertToFloatedString(String value){
        for(int i = 0; i < currency.size(); i++){
            value = value.replace(currency.get(i), "");
        }

        if(value.contains(".")) {
            if(value.length() > value.indexOf(".") + 3) value = value.substring(0, value.indexOf(".")+3);
        }
        if(value.contains(",")) value = value.substring(0, value.indexOf(",") +3);

        value = value.replaceAll("[^\\d.]", "");

        value.replace(",", ".");
        return value;
    }
}
