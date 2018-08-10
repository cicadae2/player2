package bean;

public class UserBean {
    public String account;
    public String password;
    public String name;
    public int number;
    public boolean sex;
    public String head;
    public String gethead(){return head;}
    public void sethead(String head) {
        this.head = head;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

}

