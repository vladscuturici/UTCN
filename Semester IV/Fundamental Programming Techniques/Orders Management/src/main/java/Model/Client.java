package Model;
public class Client {
    private int id;
    private String name;
    private String email;
    public Client(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String toString(){
        return this.id + " " + this.name + " " + this.email;
    }
}