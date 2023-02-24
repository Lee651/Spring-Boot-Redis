package top.rectorlee.mapper;

import org.springframework.web.bind.annotation.PathVariable;
import top.rectorlee.domain.User;

import java.util.List;

public interface UserMapper {
    List<String> findAllIds();
    User findUserById(@PathVariable("id") String id);
}
