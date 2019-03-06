package in.pokoman.www.pokoman;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Pokoman extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
