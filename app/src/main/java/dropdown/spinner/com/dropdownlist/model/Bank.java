package dropdown.spinner.com.dropdownlist.model;

/**
 * Created by sarath on 24/2/16.
 */
public class Bank {
    private String mName;
    private String mIfsc;

    public Bank(String bankName, String IFSC) {
        this.mName = bankName;
        this.mIfsc = IFSC;
    }


    @Override
    public String toString() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getIfsc() {
        return mIfsc;
    }

    public void setIfsc(String ifsc) {
        this.mIfsc = ifsc;
    }
}
