package tw.edu.ntust.stockoraclet.oracletpackage;

/**
 * Created by henrychong on 2016/5/27.
 */
public class SubscriptionInfo {
    private int notification;
    private String predict_people;
    private String u_mail;
    private String p_name;
    private String accuracy;

    public SubscriptionInfo() {
        super();
    }

    public SubscriptionInfo(int notification, String p_name, String predict_people, String u_mail,String accuracy) {
        this.notification = notification;
        this.p_name = p_name;
        this.predict_people = predict_people;
        this.u_mail = u_mail;
        this.accuracy = accuracy;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public int getNameLength() {
        return predict_people.length();
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getPredict_people() {
        return predict_people;
    }

    public void setPredict_people(String predict_people) {
        this.predict_people = predict_people;
    }

    public String getU_mail() {
        return u_mail;
    }

    public void setU_mail(String u_mail) {
        this.u_mail = u_mail;
    }
}
