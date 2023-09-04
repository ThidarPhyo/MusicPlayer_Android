package jp.ac.jec.cm0129.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SDListActivity extends AppCompatActivity {

    //private ArrayAdapter<String> adapter;
    private RowModelAdapter adapter;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlist);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
//        adapter.add("ドラえもん音頭");
//        adapter.add("日本電子専門学校");
        adapter = new RowModelAdapter(this);
//        adapter.add(new RowModel("aaa",1111111L));
//        adapter.add(new RowModel("bbb",2222222L));
//        adapter.add(new RowModel("ccc",3333333L));

        File path = Environment.getExternalStorageDirectory();
        final File[] files = path.listFiles();
        if(files != null){
            for(int i = 0; i< files.length;i++){
                Log.i("LIST_FILE","FILE_DATA"+files[i]);
                adapter.add(new RowModel(files[i], ".."));
            }
        }


        TextView txtPath = findViewById(R.id.txtFolderPath);
        txtPath.setText(path.getAbsolutePath());

        ListView list = (ListView) findViewById(R.id.sdlist);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView = (ListView) adapterView;
                RowModel item = (RowModel) listView.getItemAtPosition(i);

                Toast.makeText(SDListActivity.this,item.getFileName(),Toast.LENGTH_SHORT).show();

//                adapter.remove(item);
//                adapter.notifyDataSetChanged();

                Intent intent = getIntent();
                //String file = String.valueOf(item.getFile().getAbsoluteFile());
                intent.putExtra("SELECT_FILE",item.getFile().getAbsolutePath());
                intent.putExtra("SELECT_FILE_NAME",item.getFileName());
//                Log.i("TESTING_DATA","ALL_DATA::::  "+item.getFile().getAbsoluteFile());
                setResult(RESULT_OK,intent);

                if(item.getFile().isDirectory()){
                    upDateAdapter(item.getFile());
                }else {
                    finish();
                }

//                Intent intent = new Intent("Intent.ACTION_GET_CONTENT");
//                Uri uri = Uri.parse("storage/emulated_0/DCIM/");
//                intent.setDataAndType(uri, "*/*");
//                startActivity(Intent.createChooser(intent, "download"));

            }
        });
    }
    public void openFolder(){
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                +  File.separator + item.getFileName() + File.separator), "*/*");
//        Log.i("URI_DATA","URI_DATA_FOR_path "+Environment.getExternalStorageDirectory().getPath());
//        Log.i("URI_DATA","URI_DATA_FOR "+intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                +  File.separator + item.getFileName() + File.separator), "*/*"));
//        startActivityForResult(intent, RESULT_OK);
    }
    private void upDateAdapter(File item){
        TextView txtPath = findViewById(R.id.txtFolderPath);
        txtPath.setText(item.getAbsolutePath());

        adapter.clear();
        final File[] files = item.listFiles();
        if(item.getParent() != null){

            adapter.add(new RowModel(new File(item.getParent()) ,".." ));
        }
        if(files != null){
            for(int i = 0; i< files.length; i++){
                adapter.add(new RowModel(files[i],files[i].getName()));
            }
        }
        adapter.notifyDataSetChanged();
        Log.i("call_name","GET_PATH_NAME"+item.getAbsolutePath());
//        TextView textView = findViewById(R.id.txtPath);
//        txtPath.setText(item.getAbsolutePath());
    }
    class RowModelAdapter extends ArrayAdapter<RowModel> {
        public RowModelAdapter(@NonNull Context contex){
            super(contex,R.layout.row_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            RowModel item = getItem(position);
            Log.i("MusicPlayer","RowModelAdapter getView position:"+position);
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_item,null);

            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.imgIcon);


            if(item !=null){
            Log.i("ITEM","GET_ITEM_NAME"+item);
            TextView txt1 = convertView.findViewById(R.id.txtListFileName);
            if(txt1 != null){


               if(item.getFile().isDirectory()) {
                  // Log.i("FILE_NAME","GET_FILE_NAME"+item.getFile().isDirectory());
                   iv.setImageDrawable(getResources().getDrawable(R.drawable.folder));

                   txt1.setText(item.getFileName()+"/");
                   txt1.setTextColor(Color.parseColor("#FF0000"));
//                   if(item.getFile().isFile()){
//
//
//                   } else {
//
//
//                   }

                   //upDateAdapter(item.getFile());

               } else {
                   //Log.i("FILE_NAME","GET_FILE_NAME"+item.getFile().isDirectory());
                   txt1.setTextColor(Color.parseColor("#00FF00"));
                   iv.setImageDrawable(getResources().getDrawable(R.drawable.music));
                   txt1.setText(item.getFileName());
               }
//                if(item.getFileName().endsWith(".mp3")){
//                    //if use setImageResource() may be crush
//                    iv.setImageDrawable(getResources().getDrawable(R.drawable.music));
//                    txt1.setText(item.getFileName());
//                } else {
//                    //if use setImageResource() may be crush
//                    iv.setImageDrawable(getResources().getDrawable(R.drawable.folder));
//                    txt1.setTextColor(Color.parseColor("#FF0000"));
//                    txt1.setText(item.getFileName()+"/");
//                }

            }
            TextView txt2 = convertView.findViewById(R.id.txtListFileSize);
            if(txt2 != null){
                txt2.setText(String.valueOf(item.getFileSize()));
            }
        }
            return convertView;
        }
    }
}