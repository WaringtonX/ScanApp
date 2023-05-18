package Models;

import java.util.Date;

public class Barcode {
    private int barId;
    private String barCode1;
    private String barName;
    private int barItemcount;
    private String barCreatedate;

    public Barcode() {

    }

    public int getBarId() {
        return barId;
    }

    public void setBarId(int barId) {
        this.barId = barId;
    }

    public String getBarCode1() {
        return barCode1;
    }

    public void setBarCode1(String barCode1) {
        this.barCode1 = barCode1;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public int getBarItemcount() {
        return barItemcount;
    }

    public void setBarItemcount(int barItemcount) {
        this.barItemcount = barItemcount;
    }

    public String getBarCreatedate() {
        return barCreatedate;
    }

    public void setBarCreatedate(String barCreatedate) {
        this.barCreatedate = barCreatedate;
    }

}
