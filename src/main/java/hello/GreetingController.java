package hello;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import models.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    ArrayList<User> users = new ArrayList<>();

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting") // assumes method is GET, alternatively RequestMethod.GET
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping(value="/user/create", method= RequestMethod.POST)
    public User createUser(@RequestBody String userJsonString) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonUser = (JSONObject) jsonParser.parse(userJsonString);

        User user = new User((String) jsonUser.get("name"), (Long) jsonUser.get("age"), (String) jsonUser.get("email"));
        users.add(user);
        return user;
    }

    @RequestMapping("/user/list")
    public ArrayList<User> getUsersList (){
        return users;
    }

    @RequestMapping(value="/user/edit", method = RequestMethod.POST)
    public User editUser(@RequestBody String userJsonString) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonUser = (JSONObject) jsonParser.parse(userJsonString);
        User ret = null;

        for (User user : users) {
            if (user.getName().equals(jsonUser.get("name"))){
                user.setAge((Long) jsonUser.get("age"));
                user.setEmailAddress((String) jsonUser.get("email"));
                ret = user;
                break;
            }
        }
        return ret;
    }

    @RequestMapping("/user/delete")
    public User deleteByName(@RequestParam (value="name") String name){
        User ret = null;

        for (User user : users) {
            if (user.getName().equals(name)){
                ret = user;
                users.remove(user);
                break;
            }
        }
        return ret;
    }
}
