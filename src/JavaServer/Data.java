package JavaServer;

import java.util.HashMap;
import java.util.Map;

public class Data {

    private Map<String, String> users = new HashMap();

    public Data() {
        users.put("test1", "test");
        users.put("test2", "test");
        users.put("test3", "test");
        users.put("test4", "test");
        users.put("test5", "test");
        users.put("test6", "test");
    }

    public void insert(String name, String password) {
        users.put(name, password);
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public String getPW(String key) {
        return users.get(key);
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }
}
