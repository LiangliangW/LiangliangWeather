package cn.edu.pku.wuliangliang.bean;

/**
 * Created by WLL on 2017/11/1.
 */

public class City {

    public City(String province, String city, String number, String firstPy, String allPy, String allFirstPy) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPy = firstPy;
        this.allPy = allPy;
        this.allFirstPy = allFirstPy;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getFirstPy() {
        return firstPy;
    }

    public String getAllPy() {
        return allPy;
    }

    public String getAllFirstPy() {
        return allFirstPy;
    }

    private String province;
    private String city;
    private String number;
    private String firstPy;
    private String allPy;
    private String allFirstPy;

}
