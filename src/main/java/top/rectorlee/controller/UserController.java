package top.rectorlee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.rectorlee.service.UserService;

/**
 * @author Lee
 * @description
 * @date 2023-02-22  14:14:03
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/{id}")
    public String findUserById(@PathVariable("id") String id) {
        return userService.findUserById(id);
    }
}
