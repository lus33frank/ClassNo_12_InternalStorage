package tw.com.frankchang.houli.classno_12_internalstorage;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    TextView tvShow, tvPath;
    EditText etInput, etInput_filename;
    String strFile_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //呼叫建立findViewById
        findviewer();
        //呼叫建立Spinner的內容
        setSpinnerList();
    }

    private void findviewer(){
        //建立findViewById
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(Spinner_Listener);

        tvShow = (TextView) findViewById(R.id.textView);
        tvPath = (TextView) findViewById(R.id.textView2);
        etInput = (EditText) findViewById(R.id.editText);
    }

    private void setSpinnerList(){
        //建立Spinner的內容
        ArrayAdapter<String> adt = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item, fileList());
        adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //指定給Spinner
        spinner.setAdapter(adt);
    }

    private void showData(){
        //顯示檔案內容
        try {
            //謮取檔案
            FileInputStream fis = openFileInput(strFile_Name);
            byte[] txt_byte = new byte[fis.available()];
            fis.read(txt_byte);
            fis.close();

            //顯示檔案內容
            tvShow.setText(new String(txt_byte));

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    AdapterView.OnItemSelectedListener Spinner_Listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //Spinner 的選擇監聽器

            //取得所選的檔案名稱
            strFile_Name = parent.getItemAtPosition(position).toString();
            //顯示檔案內容
            showData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //沒選擇任何項目時的動作
        }
    };

    public void onClick_of_Save(View v){
        //儲存

        //建立Dialog使用的View
        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        //將所需的Widget findViewById
        etInput_filename = (EditText) dialog_view.findViewById(R.id.editText2);
        //建立Dialog並顯示
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_save_title)
                .setView(dialog_view)
                .setPositiveButton(R.string.dialog_ok, dialog_save_Listener)
                .setNegativeButton(R.string.dialog_cancel, dialog_save_Listener)
                .show();
    }

    DialogInterface.OnClickListener dialog_save_Listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //Dialog的監聽器
            String toast_message;
            boolean save_ok = false;

            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //按下確定
                    try {
                        //存檔
                        FileOutputStream fos = openFileOutput(etInput_filename.getText().toString(), MODE_PRIVATE);
                        fos.write((etInput.getText().toString() + "\n").getBytes());
                        fos.close();
                        save_ok = true;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //呼叫建立Spinner的內容
                    if (save_ok){
                        setSpinnerList();
                        etInput.setText("");
                        toast_message = getResources().getString(R.string.save_ok);
                    }
                    else{
                        toast_message = getResources().getString(R.string.save_error);
                    }

                    //通知使用者存檔結果
                    Toast.makeText(MainActivity.this,toast_message, Toast.LENGTH_SHORT).show();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //按下取消
                    Toast.makeText(MainActivity.this, R.string.save_cancel, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public void onClick_of_Append(View v){
        //附加
        String toast_message;
        boolean save_ok = false;

        try {
            //儲存檔案
            FileOutputStream fos = openFileOutput(strFile_Name, MODE_APPEND);
            fos.write((etInput.getText().toString() + "\n").getBytes());
            fos.close();
            save_ok = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //顯示檔案內容
        if (save_ok){
            etInput.setText("");
            showData();
            toast_message = getResources().getString(R.string.save_ok);
        }
        else{
            toast_message = getResources().getString(R.string.save_error);
        }

        //通知使用者存檔結果
        Toast.makeText(MainActivity.this, toast_message, Toast.LENGTH_SHORT).show();
    }

    public void onClick_of_Delete(View v){
        //刪除
        String strDelete_Msg;

        strDelete_Msg = getResources().getString(R.string.dialog_delete_message) + "：";
        strDelete_Msg += strFile_Name + "？";

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(strDelete_Msg)
                .setPositiveButton(R.string.dialog_ok, dialog_delete_Listener)
                .setNegativeButton(R.string.dialog_cancel, dialog_delete_Listener)
                .show();
    }

    DialogInterface.OnClickListener dialog_delete_Listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    if (deleteFile(strFile_Name)){
                        setSpinnerList();
                        Toast.makeText(MainActivity.this, R.string.delete_ok, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(MainActivity.this, R.string.delete_cancel, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
