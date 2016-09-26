package com.spinner.dropdown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spinner.dropdown.model.Bank;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DropDown<Bank> bankDropDown = (DropDown) findViewById(R.id.drop_down);
        final DropDown<String> stringDropDown = (DropDown) findViewById(R.id.string_list);
        stringDropDown.setItems(getItems());
        bankDropDown.setItems(getBanks());
        bankDropDown.setWhatToPrint(new DropDown.Printable<Bank>() {
            @Override
            public String getPrintable(Bank bank) {
                return bank.getName();
            }
        });
        bankDropDown.setItemClickListener(new DropDown.ItemClickListener<Bank>() {
            @Override
            public void onItemSelected(DropDown dropDown, Bank selectedItem) {
                Toast.makeText(getApplicationContext(), "Selected item :" + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int selectedIndex = bankDropDown.getSelectedIndex();
                final Bank selectedItem = bankDropDown.getSelectedItem();
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
