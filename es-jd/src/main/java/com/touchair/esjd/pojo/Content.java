package com.touchair.esjd.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: bin.wang
 * @date: 2020/9/18 13:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String title;
    private String price;
    private String img;
}
