package com.seebaldtart.projectnewsapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
public class InputActivity extends AppCompatActivity {
    private String queryString;
    private SearchView searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        searchBar = (SearchView) findViewById(R.id.search_bar);
        final Button search_button = (Button) findViewById(R.id.search_button);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query;
                QueryUtils.performSearch(getApplicationContext(), query, searchBar);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryString = newText;
                return false;
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryUtils.performSearch(getApplicationContext(), queryString, searchBar);
            }
        });
    }
//    private void performSearch(String query, SearchView view) {
//        if (!view.getQuery().equals("")) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("tag", query);
//            intent.putExtras(bundle);
//            Log.i("TEST", "User Input: " + query);
//            startActivity(intent);
//        }
//    }
}
