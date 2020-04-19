package com.unacademy.redis.controller;

import com.unacademy.redis.service.MainService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class MainController {
    @Autowired
    MainService msobj;

    @GetMapping("/getVal")
    public String getVal(@RequestParam String key) {

        // localhost:8080/user/darshan/repos?query1=springboot

        return msobj.getval(key);
        // return query1;
    }

    @GetMapping("/SetVal")
    public String SetVal(@RequestParam String key, @RequestParam String value, @RequestParam String exptime) {

        return msobj.SetValue(key, value, exptime);
    }

    @GetMapping("/ExpiryTime")
    public String ExpiryTime(@RequestParam String key, @RequestParam String exptime) {

        return msobj.SetExpiryTime(key, exptime);
    }

    @GetMapping("/ZADD")
    public String ZADD(@RequestParam String key, @RequestParam  double score, @RequestParam String element) {
        return msobj.ZADD(key, score, element);
    }

    @GetMapping("/ZRANK")
    public String ZRANK(@RequestParam String key, @RequestParam String element) {

        return msobj.ZRANK(key, element);
    }

    @GetMapping("/ZRANGE")
    public ArrayList<String> ZRANGE(@RequestParam String key, @RequestParam int start, @RequestParam int stop,
            @RequestParam int withscore) {

        return msobj.ZRANGE(key, start, stop, withscore);
    }

}