package dropdown.spinner.com.dropdownlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import dropdown.spinner.com.dropdownlist.model.Bank;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.btn);
        final DropDown<Bank> dropDown = (DropDown) findViewById(R.id.drop_down);
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
