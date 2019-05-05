package com.example.actividadrealm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.actividadrealm.Model.Persona;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    EditText nombre,edad1,edad2,id;
    CheckBox masculino,femenino;
    Button añadir,mostrar,modificar,eliminar;
     TextView mostrarResultado;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        id=(EditText) findViewById(R.id.id);
        nombre=(EditText) findViewById(R.id.nombre);
        edad1=(EditText) findViewById(R.id.edad1);
        edad2=(EditText) findViewById(R.id.edad2);
        masculino=(CheckBox) findViewById(R.id.genereMasculi);
        femenino=(CheckBox) findViewById(R.id.genereFemeni);
        añadir=(Button) findViewById(R.id.añadir);
        mostrar=(Button) findViewById(R.id.mostrar);
        modificar=(Button) findViewById(R.id.modificar) ;
        eliminar=(Button) findViewById(R.id.eliminar);
        mostrarResultado=(TextView) findViewById(R.id.mostrarResultados);

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_to_datanbase(nombre.getText().toString().trim(), Integer.parseInt(edad1.getText().toString().trim()));
                calcularId();

            }


        });

        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_database();

            }


        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= nombre.getText().toString();
                String age = edad1.getText().toString();
                updateFromDatabase(name,age);

            }
        });



        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= nombre.getText().toString();
                delete_from_database(name);

            }

        });

    }



    private void save_to_datanbase(final String name, final int age) {
        final int index= calcularId();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Persona persona = realm.createObject(Persona.class,index);
                persona.setNombre(name);
                persona.setEdad(age);


            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v("Succes", "--------->Ok<----------");

            }
        }, new Realm.Transaction.OnError(){
            @Override
            public void onError(Throwable error){
                Log.e("Failed", error.getMessage());

            }
        });

    }
    private void updateFromDatabase(final String name, final String age) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                int identificador = Integer.parseInt(String.valueOf(Integer.parseInt(id.getText().toString())));
                Persona persona = realm.where(Persona.class)
                        .equalTo("identificador", identificador)
                        .findFirst();
                if (persona != null) {
                    persona.setNombre(name);
                    persona.setEdad(Integer.parseInt(age));

                    realm.insertOrUpdate(persona);
                    refresh_database();
                }

            }

        });
    }
    private void refresh_database() {
        RealmResults<Persona> results = realm.where(Persona.class)
                .findAllAsync();
        results.load();

        String personas="";
        for(Persona persona:results){
            personas+="Nombre: "+persona.getNombre()+" Edad:"+persona.getEdad()+"\n";
        }

        mostrarResultado.setText(personas);

    }

    private void delete_from_database(String name) {
        final RealmResults<Persona> personas = realm.where(Persona.class).equalTo("nombre", name).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                personas.deleteFromRealm(0);
            }
        });
    }

    private final static int calcularId(){

        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Persona.class).max("identificador");

        int nextId;
        if (currentIdNum == null){
            nextId = 0;
        }else {
            nextId = currentIdNum.intValue()+1;
        }
        return nextId;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
