package casier.billsplitter.AddBill;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import casier.billsplitter.BuildConfig;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.UserDataObserver;
import casier.billsplitter.Utils;

public class AddBillActivity extends Activity implements UserDataObserver {

    @BindView(R.id.bill_name)
    EditText billName;

    @BindView(R.id.bill_amount)
    EditText billAmount;

    @BindView(R.id.users_picker)
    RecyclerView usersPicker;

    @BindView(R.id.bill_add)
    Button btnAddBill;

    private Utils mUtils;
    private UserPickerAdapter adapter;
    private AddBillPresenter presenter;
    private Uri imageUri;
    private TextRecognizer detector;

    private static final String TAG = "AddBillActivityDebug";
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final int PHOTO_REQUEST = 10;
    private boolean billNameExists = false;
    private boolean billAmountExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        ButterKnife.bind(this);

        presenter = new AddBillPresenter(this);
        mUtils = Utils.getInstance();
        mUtils.registerUserObserver(this);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        Intent intent = getIntent();
        if(intent.hasExtra("line")){
            billAmount.setText(intent.getStringExtra("line"));
        }

        adapter = new UserPickerAdapter(this, R.layout.row_user_picker, presenter.getAccountUserList());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        usersPicker.setLayoutManager(layoutManager);
        usersPicker.setHasFixedSize(true);
        usersPicker.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtils.removeUserObserver(this);
    }

    @Override
    public void onUserDataChange(List<User> userList) {
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.bill_add)
    public void submit(View v){
        if((LocalUser.getInstance().getUserId()) != null && !LocalUser.getInstance().getUserId().isEmpty())
        {
            String amount = billAmount.getText().toString();
            String name = billName.getText().toString();
            if(amount.equals("")){
                Toast.makeText(this, "Ajoutez un montant à la facture", Toast.LENGTH_SHORT).show();
                return;
            }

            if(name.equals("")){
                Toast.makeText(this, "Ajoutez un nom à la facture", Toast.LENGTH_SHORT).show();
                return;
            }

            if(adapter.getSelectedUserList().size() == 0){
                Toast.makeText(this, "Choisissez au moins un utilisateur", Toast.LENGTH_SHORT).show();
                return;
            }

            presenter.addBill(billName.getText().toString(), billAmount.getText().toString(), adapter.getSelectedUserList());
            Intent intent = new Intent();
            setResult(1, intent);
            finish();
        }
    }

    @OnClick(R.id.bill_amount_camera)
    public void cameraAmount(){
        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_PERMISSION :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    takePicture();
            }
        }
    }

    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK){
            launchMediaScanIntent();
            try{
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
                                List<String> currency = Utils.getInstance().getCurrency();
                                for(int c = 0; c < currency.size(); c++){
                                    if(!list.contains(line.getValue()))
                                    {
                                        if(line.getValue().length() < 8)
                                        {
                                            list.add(line.getValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (list.size() < 1) {
                        Toast.makeText(this, "Scan Failed: Found nothing to scan", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, BillPickerActivity.class);
                        intent.putStringArrayListExtra("lines", list);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }
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

    @OnTextChanged(R.id.bill_name)
    public void onBillNameChanged(CharSequence name){
        if(name.length() > 0){
            billNameExists = true;
        } else {
            billNameExists = false;
        }
        checkButtonEnabled();
    }

    @OnTextChanged(R.id.bill_amount)
    public void onBillAmountChanged(CharSequence amount){
        if(amount.length() > 0){
            billAmountExists = true;
        } else {
            billAmountExists = false;
        }
        checkButtonEnabled();
    }

    public void checkButtonEnabled(){
        if(billNameExists && billAmountExists && adapter.getSelectedUserList().size() > 0){
            btnAddBill.setEnabled(true);
            btnAddBill.setTextColor(getResources().getColor(R.color.text));
        } else {
            btnAddBill.setEnabled(false);
            btnAddBill.setTextColor(getResources().getColor(R.color.grey_border));
        }
    }
}
