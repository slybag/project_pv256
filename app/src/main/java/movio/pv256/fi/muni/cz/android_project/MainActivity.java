package movio.pv256.fi.muni.cz.android_project;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import movio.pv256.fi.muni.cz.android_project.adapter.FilmAdapter;
import movio.pv256.fi.muni.cz.android_project.model.Film;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (createFakeFilmList().size() == 0) {
            setContentView(R.layout.activity_main_error);
            TextView text = (TextView) findViewById(R.id.errorTextView);
            text.setText("No data");
        } else if (!isNetworkOnline()) {
            setContentView(R.layout.activity_main_error);
            TextView text = (TextView) findViewById(R.id.errorTextView);
            text.setText("No connection");
        } else {
            setContentView(R.layout.activity_main);

            GridView gridView = (GridView) findViewById(R.id.gridView);

            gridView.setAdapter(new FilmAdapter(createFakeFilmList(), this));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), FilmDetailActivity.class);

                    intent.putExtra("position", position);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Film> createFakeFilmList() {
        List<Film> listOfFilm = new ArrayList<>();
        int i = 0;

        while(i < 5) {
            i++;

            Film film = new Film();
            film.setCoverPath("Path" + i);
            film.setReleaseDate(i);
            film.setTitle("Title" + i);

            listOfFilm.add(film);
        }

        return listOfFilm;
    }

    private boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }
}
