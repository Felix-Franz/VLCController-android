package felixsystems.vlccontroller.client;

/**
 * Created by xifizurk on 26.10.16.
 */
public class Client  {
    String name;
    String ip;
    int port;
    String pw;
    Boolean status;

    // constructors
    public Client() {
    }

    public Client(String name, String ip, int port, String pw, Boolean status) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.pw = pw;
        this.status = status;
    }

    // getters
    public String getIp() {
        return this.ip;
    }
    public String getName() {
        return this.name;
    }

    public Boolean getStatus() { return this.status; }

    // setters
    public void setStatus( Boolean status) { this.status = status; }
}
