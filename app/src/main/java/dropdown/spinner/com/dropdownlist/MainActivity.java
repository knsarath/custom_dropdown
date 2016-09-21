package dropdown.spinner.com.dropdownlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDown.setText("A");
            }
        });

    }

    private List<String> getItems() {
        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");


        return list;
    }
}
