package top.rectorlee.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lee
 * @description
 * @date 2023-02-22  14:06:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String id;

    private String name;

    private String phone;

    private String remark;
}
