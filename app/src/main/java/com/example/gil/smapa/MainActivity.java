package com.example.gil.smapa;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnRunApp, btnSaveMeasure, finishTravel;
    ListView tv;
    TextView helperKey, txtMeasure, totalClients;
    EditText dataClient;
    final ArrayList<String> allCients = new ArrayList<>();
    private int totalClientsToCheck = 0;
    final ArrayList<String> al = new ArrayList<String>();
    final ArrayAdapter[] adapter = new ArrayAdapter[1];
    final ArrayList<String>[] dcoBackupAllCients = new ArrayList[1];
    final ArrayMap<Integer, String>  users = new ArrayMap<>();
    List<String> dcobackup = new ArrayList<>();

    String resumeAllClienbts = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*this button is to start the read file process - este boton es para iniciar el proceso de lecture*/
        btnRunApp = (Button)findViewById(R.id.btnRunApp);
        /*this button is for take a measure and  update measure modified previously in the "dataClient" Texvtview -
        * este boton es para tomar la medida y actualzar la medida previamente modificada en el ListView dataClient*/
        btnSaveMeasure = (Button)findViewById(R.id.btnSaveMeasure);
        /*This button is for save every measure took ik and save it in an another file
        * este boton es para guardar todas las medidas tomadas y guardarlas en otro archivo*/
        finishTravel = (Button)findViewById(R.id.finishTravel);
        /*Listview to watch all people in the DCO File
        * listview para mirar todas las personas en el archivo DCO*/
        tv = (ListView)findViewById(R.id.myListView);
        /*EditText to update data people
        * edittext para actualizar personas*/
        dataClient = (EditText) findViewById(R.id.dataClient);
        /*keep a reference of current id of item select on listview
        * mantener una referencia del item actual seleccionado en el listview*/
        helperKey = (TextView)findViewById(R.id.helperKey);
        /*textview to display how many people are not been took it a measure
        * textview para mostrar cuantas personas aun no se han tomado medidas*/
        totalClients = (TextView)findViewById(R.id.totalClients);

        /*hide the helperKey*/
        helperKey.setVisibility(View.INVISIBLE);
        /*Event to save the DCO FILE after to take all measures
        * evento para guardar el archivo dco después de guardar toas las medidas*/
        finishTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Caal method saveDcoFile -- llamar al metodo saveDcoFile()*/
                saveDcoFile();
            }
        });
        /*Event on btnSaveMeasure to save a new client measure -- evento sobre btnSaveMeasure para guardar una nueva medida del cliente*/
        btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMeasure();
            }
        });
        /*Event to load data from DCO file -- evento para cargar datos del arhivo DCO*/
        btnRunApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDcoFile();
            }
        });
        /*Event on ListView to display information about an specific client in the editText
        * evento sobre el listrview para mostrar information acerca de un cliente especifico en el editText*/
        tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Call to changeItemToEdit and then update the current item and display user information*/
                changeItemToEdit(i);
            }
        });
    }

    public void changeItemToEdit(Integer currentId){
        /*update text of "dataClcient(EditText)" -- get the client from the ArrayList named allClients and display it
        * actulizar texto de "dataClient(ex un EditText)"-- obtener client del arrayList llamado allClientes y mostrarlo*/
        dataClient.setText(allCients.get(currentId).toString());
        /*update helperKey to keep the correct item from listview previously selected
        * actualizar helperKey para mantener el correcto item del listView previamente seleccionado*/
        helperKey.setText(Integer.toString(currentId));
        Intent in = new Intent(getApplicationContext(), Main2Activity.class);
        in.putExtra("client", allCients.get(currentId));
        startActivityForResult(in, 1);
    }
    public void LoadDcoFile(){
        /********LOAD FILE FROM SDCARD*************/

        /****************************************/
        /*Create a new Thread to avoid overload the main thread and avoid closed the app
        * crear un nuevo hilo para evitar sobrecargar el hilo principal y que se cierre la applicacion*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //get the path of SDCard and read a file named "DCO.DCO"
                    File sdcard = Environment.getExternalStorageDirectory();

                    File file = new File(sdcard, "DCO.DCO");
                    //create a new bufferReader to get data from the file previously readed -- crear un nuewo bufferReadder para conseguiar datos del arvhibo previamente leiido
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    /*counter to read data from the file and knows when we need to add a new item to the ArrayList allClients and dcoBackUp
                     * contador para leer information del archivo y saber cuando necesitamos agregar un nuevo item al arrayList allClients y dcoBackup */
                    int count = 0;
                    int extracounter = 0;
                    String helperText = "";
                    while ((line = br.readLine()) != null) {
                        /*increase the counter after to read a new line
                        * incremenat el contador despues de leer una linea nueva*/
                        count++;
                        /*append the current line to this variable and only when is necessary create a new element
                        * agregar la linea actual a la variable y solo cuanoo es necesario crear un nuevo elemento*/
                        helperText = helperText + line;
                        /*this block is going to be executed when is necessary create a new elemento on the ArrayList the pattern found it was after 3 lines
                        * este bloque vas a ser ejecutado cuando es necesario crear un nuevo elemento en el arrayList(allClient y dcoBackuo), el patron
                        * encontrado fue despues de tres líneas*/
                        if(count >= 3){
                            users.put(extracounter, helperText);
                            extracounter++;
                            allCients.add(helperText);
                            dcobackup.add(helperText);
                            count = 0;
                            helperText = "";
                        }
                    }
                    /**Close the bufferREader
                     * cerrarr el bufferReader*/
                    br.close();

                    /*fill the ListView with the new data created, in this case the data on allClients
                    * llenar el listview con los nuevos datos creados, en este caso son los datos en allClients*/
                    adapter[0] = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, allCients);
                    dcoBackupAllCients[0] = allCients;
                    /*get the size of the arrayList allClients to displaye the number of clients
                    * obtener el tamaño de el arrayList allClients para mostrar el numero de clientes*/
                    totalClientsToCheck = allCients.size();
                    /*Create a new thread to interact with the graphic Interface
                    * crear un hilo nuevo para poder interactuar con la interfaz gráfica*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*update the text in totalCloientsToCheck(TExtView)
                            * actulizar el texto en totalCloientsToCheck(TExtView)*/
                            totalClients.setText(Integer.toString(totalClientsToCheck));
                        }
                    });
                    /*Create a new thread to interact with the graphic Interface and refill the listview
                    * crear un hilo nuevo para poder interactuar con la interfaz gráfica y rellenar el listview*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setAdapter(adapter[0]);
                        }
                    });


                }
                catch (IOException e) {
                    /*In case an error, display an Toast with a message
                    * en caso de un error, mostrar un Toast con un mensaje*/
                    Toast.makeText(MainActivity.this, "Ha surgido un error!", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();//Start the thread -- iniciar el hilo

        /*hide the button that started the process to read data from the DCO flle
        * oculat el boton que inicio el proceso de lectura de datos del archivo DCO*/
        btnRunApp.setVisibility(View.GONE);
    }
    /*Method to save a new measure
    * metodo para guardar una nueva lectura*/
    public void saveMeasure(){
        try{
            /*get the current item selected on the listview
            * obtener el item actual en el listview*/
            int currentId = Integer.parseInt(helperKey.getText().toString());
            /*in this part we update the data in 'dcoBackupAllCients[0]' with the new measure took it
            * en esta parte nosotros actulizamos los datos en 'dcoBackupAllCients[0]' con la nueva medida tomada*/
            dcoBackupAllCients[0].set(currentId, dataClient.getText().toString());
            /*in this part we update the data in 'dcobackuo' with the new measure took it
            * en esta parte nosotros actulizamos los datos en 'dcobackup' con la nueva medida tomada*/
            dcobackup.set(currentId, dataClient.getText().toString());
            /*remove the current item updated and then delete it on the ListView
            * remover el item actual actulizado y luego borrarlo del listvIew*/
            allCients.remove(currentId);
            adapter[0].notifyDataSetChanged();
            /*decrease the number of measure to take
            * disminuir el numero de medidas a tomar*/
            dataClient.setText("");
            totalClientsToCheck--;
            totalClients.setText(Integer.toString(totalClientsToCheck));
            /*Display a new toast with a message
            * mostrat un nuevo toast con un mensaje */
            Toast.makeText(MainActivity.this, "Lectura guardada correctamente", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Ha surgido un error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    /*Method to save the measures took it and create a new DCo file
    * metodo para salvar las medidas tomadas y crear un archivo DCo nuevo*/
    public void saveDcoFile(){
        /*Create a new thread
        * crear un hilo nuevo*/
        new Thread(new Runnable() {

            @Override
            public void run() {
                /*get the size of clients with data updated
                * obtener el numero de clientes con datos actualizados*/
                int limit = dcobackup.size();
                /*do a string to then save it on the DCO file
                * hacer una cadena para después guardarla en el archivo DCO*/
                for (int i = 0; i<limit; i++){
                    resumeAllClienbts = resumeAllClienbts + dcobackup.get(i) + "\n";
                }
                /*make a try to handle an possible error -- hacer un try para manejar un posible error*/
                try {
                    /*As the beginnig, we get the path of the SdCard end we creatre a new DCo File inside the Recorridos folder*/
                    File newFolder = new File(Environment.getExternalStorageDirectory(), "Recorridos");
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }
                    /*the name of the file will be "FINAL_DCO.DCO"
                    el nombre del achivo será "FINAL_DCO.DCO"*/
                    final File file = new File(newFolder, "FINAL_DCO" + ".DCO");
                    /*create the file
                    * crear el archivo*/
                    file.createNewFile();

                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    /*append thee data with the clients updated
                    * agregar los datos con los clientes actualizados*/
                    myOutWriter.append(resumeAllClienbts);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                    /*New threads to interact with the graphic interace main and display messages with Toasts
                    * hilos nuevos para interactuar con la interaz grafica principa y mostrar mensahes con Toast*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Se ha guardado el archivo correctamente con el nombre de: " + file.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Ha surgido un error mientras se guardaba el recorrido", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();/*Start the execution -- iniciar la ejecución*/

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            //Toast.makeText(this, data.getStringExtra("lecture"), Toast.LENGTH_SHORT).show();
            String newData = data.getStringExtra("lecture");
            try{
            /*get the current item selected on the listview
            * obtener el item actual en el listview*/
                int currentId = Integer.parseInt(helperKey.getText().toString());
            /*in this part we update the data in 'dcoBackupAllCients[0]' with the new measure took it
            * en esta parte nosotros actulizamos los datos en 'dcoBackupAllCients[0]' con la nueva medida tomada*/
                dcoBackupAllCients[0].set(currentId, newData);
            /*in this part we update the data in 'dcobackuo' with the new measure took it
            * en esta parte nosotros actulizamos los datos en 'dcobackup' con la nueva medida tomada*/
                dcobackup.set(currentId, newData);
            /*remove the current item updated and then delete it on the ListView
            * remover el item actual actulizado y luego borrarlo del listvIew*/
                //allCients.remove(currentId);
                if(tv.getChildAt(currentId).isEnabled())
                {
                    tv.getChildAt(currentId).setEnabled(false);
                }
                //adapter[0].notifyDataSetChanged();
            /*decrease the number of measure to take
            * disminuir el numero de medidas a tomar*/
                dataClient.setText("");
                totalClientsToCheck--;
                totalClients.setText(Integer.toString(totalClientsToCheck));
            /*Display a new toast with a message
            * mostrat un nuevo toast con un mensaje */
                Toast.makeText(MainActivity.this, "Lectura guardada correctamente", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "Ha surgido un error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
