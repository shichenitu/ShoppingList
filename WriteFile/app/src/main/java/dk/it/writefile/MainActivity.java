package dk.it.writefile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    // Demo of file read and write

    private TextView show;
    private ItemsDB itemsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = findViewById(R.id.content);
        itemsDB = ItemsDB.get(this);
        show.append(writeFile());
    }

    private String writeFile() {
        // Write a file
        String filename = "itemsFile";
        String fileContents = itemsDB.listItems();
        try (FileOutputStream fos = this.openFileOutput(filename, this.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (java.io.IOException e) {
        }

        // Read a file
        String res = "";
        try {
            FileInputStream fis = this.openFileInput("itemsFile");
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    res = res + line + "\n";
                    line = reader.readLine();
                }
                Log.i("Fileread", res);
            } catch (IOException e) {
            }
        } catch (java.io.FileNotFoundException e) {
        }
        return res;
    }
}