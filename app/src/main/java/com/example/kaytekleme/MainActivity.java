package com.example.kaytekleme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Realm realm;
    EditText kullaniciAdi, sifre, isim;
    Button btn ,guncelleButon;
    RadioGroup cinsiyetGrup;
    String cinsiyetText,isimText,sifreText,kullaniciText;
    Integer positionT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RealmTanimla();
        tanimla();
        ekle();
        goster();
        pozisyoBul();
    }

    public void RealmTanimla() {
        realm = Realm.getDefaultInstance();

    }

    public void tanimla() {
        listView = findViewById(R.id.listView);
        kullaniciAdi = findViewById(R.id.editTextKullanici);
        sifre = findViewById(R.id.editTextSifre);
        isim = findViewById(R.id.editTextIsim);
        cinsiyetGrup = findViewById(R.id.CisniyetRadio);
        btn = findViewById(R.id.kayıtOlButonu);
        guncelleButon =findViewById(R.id.güncelleButonu);


    }

    public void ekle() {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bilgileriAl();

                yaz(cinsiyetText, isimText, kullaniciText, sifreText);
                sifre.setText("");
                kullaniciAdi.setText("");


            }
        });
        guncelleButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<KisiBilgileri> list =realm.where(KisiBilgileri.class).findAll();
              final KisiBilgileri kisi =list.get(positionT);

              bilgileriAl();


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        kisi.setCinsiyet(cinsiyetText);
                        kisi.setKullanici(kullaniciText);
                        kisi.setSifre(sifreText);
                        kisi.setIsim(isimText);
                    }
                });
                goster();




            }
        });


    }

    public void yaz(final String cinsiyet, final String isim, final String kullaniciAdi, final String sifre) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                KisiBilgileri kisiBilgileri = realm.createObject(KisiBilgileri.class);
                kisiBilgileri.setCinsiyet(cinsiyet);
                kisiBilgileri.setIsim(isim);
                kisiBilgileri.setKullanici(kullaniciAdi);
                kisiBilgileri.setSifre(sifre);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Başarılı", Toast.LENGTH_LONG).show();
                goster();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(getApplicationContext(), "Başarısız", Toast.LENGTH_LONG).show();

            }
        });


    }

    public void goster() {

        RealmResults<KisiBilgileri> kisiBilgileris = realm.where(KisiBilgileri.class).findAll();
        if (kisiBilgileris.size() > 0) {

            adapter adapter = new adapter(kisiBilgileris, getApplicationContext());
            listView.setAdapter(adapter);

        }

    }

    public void pozisyoBul() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            RealmResults<KisiBilgileri> list=realm.where(KisiBilgileri.class).findAll();
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ac(position);
                kullaniciAdi.setText(list.get(position).getKullanici());
                sifre.setText(list.get(position).getSifre());
                isim .setText(list.get(position).getIsim());
                if(list.get(position).getCinsiyet().equals("Erkek")){
                    (( RadioButton) cinsiyetGrup.getChildAt(0)).setChecked(true);
                  }else{
                    (( RadioButton) cinsiyetGrup.getChildAt(1)).setChecked(true);
                }
                positionT=position;
            }
        });


    }

    public void sil(final int position) {

        final RealmResults<KisiBilgileri> gelenList = realm.where(KisiBilgileri.class).findAll();
        Log.i("name", "ListeElemanSayısı:" + gelenList.get(position).getKullanici());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                KisiBilgileri kisi = gelenList.get(position);
                kisi.deleteFromRealm();
                goster();
            }
        });

    }

    public void ac(final int position) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert, null);

        Button evetButon = findViewById(R.id.evetButon);
        Button hayırButon = findViewById(R.id.hayırButon);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        evetButon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sil(position);
                dialog.cancel();
            }
        });
        hayırButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

    }
        public void bilgileriAl()
        {
            Integer id =cinsiyetGrup.getCheckedRadioButtonId();
            RadioButton radioButton=findViewById(id);
            cinsiyetText = radioButton.getText().toString();
           isimText = isim.getText().toString();
            kullaniciText = kullaniciAdi.getText().toString();
             sifreText = sifre.getText().toString();

        }

}
