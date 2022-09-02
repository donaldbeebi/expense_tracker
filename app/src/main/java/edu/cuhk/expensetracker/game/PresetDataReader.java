package edu.cuhk.expensetracker.game;

//god made me use singleton, don't blame me

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import edu.cuhk.expensetracker.R;

public class PresetDataReader {
    private static String PRESET_FILE_PATH = "/data/data/edu.cuhk.expensetracker/files/preset";
    private static String RECORD_FILE_PATH = "/data/data/edu.cuhk.expensetracker/files/record";

    private HashMap<String, ArrayList<String>> m_Data;
    private boolean m_FirstTime = false;
    private static final PresetDataReader m_Instance = new PresetDataReader();

    public static PresetDataReader getInstance() { return m_Instance; }

    private PresetDataReader() {}

    public void loadData(Context context, Resources res) {
        //Setting up m_Data
        m_Data = new HashMap<>();
        m_Data.put("preset_list", new ArrayList<>());
        m_Data.put("preset_list_amount", new ArrayList<>());
        m_Data.put("preset_list_category", new ArrayList<>());
        m_Data.put("record_item", new ArrayList<>());
        m_Data.put("record_amount", new ArrayList<>());
        m_Data.put("record_date", new ArrayList<>());
        m_Data.put("record_category", new ArrayList<>());

        //Loading data
        File file = new File(PRESET_FILE_PATH);
        InputStream isd = null;
        InputStream isdr = null;

        if (file.exists()) {
            try {
                m_FirstTime = false;
                isd = new FileInputStream(PRESET_FILE_PATH);
                isdr= new FileInputStream(RECORD_FILE_PATH);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            m_FirstTime = true;
            isd = res.openRawResource(R.raw.preset);
            isdr = res.openRawResource(R.raw.record);
        }

        //Reading from input stream for creating the preset list in the Logging Activity
        BufferedReader br = new BufferedReader(new InputStreamReader(isd, Charset.forName("UTF-8")));
        BufferedReader brr = new BufferedReader(new InputStreamReader(isdr, Charset.forName("UTF-8")));
        //for creating file to save info for first time open the app
        FileOutputStream fos = null;
        FileOutputStream fosd = null;
        FileOutputStream fosr = null;
        FileOutputStream fosrd = null;
        String linewrite = "";
        String linewriter = "";

        //creating files for preset options
        try {
            if (m_FirstTime) {
                fos = context.openFileOutput("preset", Context.MODE_PRIVATE);
                fosd = context.openFileOutput("preset_dup", Context.MODE_PRIVATE);
            }
            while (((linewrite = br.readLine()) != null)) {
                String output = linewrite + "\n";
                String[] store = linewrite.split(",");
                if (m_FirstTime) {
                    fos.write(output.getBytes());
                    fosd.write(output.getBytes());
                }

                m_Data.get("preset_list").add(store[0]);
                m_Data.get("preset_list_amount").add(store[1]);
                m_Data.get("preset_list_category").add(store[2]);
            }
            if (m_FirstTime) {
                fos.close();
                fosd.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        //creating local saving for logging record
        try {
            if (m_FirstTime) {
                fosr = context.openFileOutput("record", Context.MODE_PRIVATE);
                fosrd = context.openFileOutput("record_dup", Context.MODE_PRIVATE);
            }
            while (((linewriter = brr.readLine()) != null)) {
                String output = linewriter + "\n";
                String[] store = linewriter.split(",");
                if (m_FirstTime) {
                    fosr.write(output.getBytes());
                    fosrd.write(output.getBytes());
                }
                m_Data.get("record_item").add(store[0]);
                m_Data.get("record_amount").add(store[1]);
                m_Data.get("record_date").add(store[2]);
                m_Data.get("record_category").add(store[3]);
            }
            br.close();
            brr.close();
            isd.close();
            isdr.close();
            if (m_FirstTime) {
                fosr.close();
                fosrd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<String>> getData() {
        return m_Data;
    }
}
