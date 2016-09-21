package dropdown.spinner.com.dropdownlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.btn);
        final DropDown<String> dropDown = (DropDown) findViewById(R.id.drop_down);
        dropDown.setItems(getItems());
        dropDown.setItemClickListener(new DropDown.ItemClickListener<String>() {
            @Override
            public void onItemSelected(DropDown dropDown, String selectedItem) {
                Toast.makeText(getApplicationContext(),selectedItem,Toast.LENGTH_SHORT).show();
            }
        });




    }

    private List<String> getItems() {
        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("One");
        list.add("Two");
        list.add("One");
        list.add("Two");
        list.add("One");
        list.add("Two");
        list.add("One");
        list.add("Two");


        return list;
    }
}
