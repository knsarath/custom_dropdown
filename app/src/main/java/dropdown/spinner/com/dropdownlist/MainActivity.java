package dropdown.spinner.com.dropdownlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dropdown.spinner.com.dropdownlist.model.Bank;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DropDown<Bank> dropDown = (DropDown) findViewById(R.id.drop_down);
        final DropDown<String> stringDropDown = (DropDown) findViewById(R.id.string_list);
        stringDropDown.setItems(getItems());
        dropDown.setItems(getBanks());
        dropDown.setWhatToPrint(new DropDown.Printable<Bank>() {
            @Override
            public String getPrintable(Bank bank) {
                return bank.getName();
            }
        });
        dropDown.setItemClickListener(new DropDown.ItemClickListener<Bank>() {
            @Override
            public void onItemSelected(DropDown dropDown, Bank selectedItem) {

            }
        });

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int selectedIndex = dropDown.getSelectedIndex();
                final Bank selectedItem = dropDown.getSelectedItem();
                Toast.makeText(getApplicationContext(), "Selected item :" + selectedItem + " ,  index :" + selectedIndex, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private List<String> getItems() {
        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");


        return list;
    }

    private ArrayList<Bank> getBanks() {
        ArrayList<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Canara Bank", "CNRB00006"));
        banks.add(new Bank("Axis Bank", "AXIS00006"));
        banks.add(new Bank("Federal Bank", "FDRL00006"));
        banks.add(new Bank("State Bank", "SBI00006"));
        banks.add(new Bank("South Indian Bank", "SIB00006"));
        banks.add(new Bank("Canara Bank", "CNRB00006"));
        banks.add(new Bank("Axis Bank", "AXIS00006"));
        banks.add(new Bank("Federal Bank", "FDRL00006"));
        banks.add(new Bank("State Bank", "SBI00006"));
        banks.add(new Bank("South Indian Bank", "SIB00006"));
        return banks;
    }
}
