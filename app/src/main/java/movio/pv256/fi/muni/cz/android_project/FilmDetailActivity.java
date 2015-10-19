package movio.pv256.fi.muni.cz.android_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Michal on 18. 10. 2015.
 */
public class FilmDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int position = getIntent().getExtras().getInt("position");
    }
}
