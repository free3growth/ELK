package com.touchair.eselk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bin.wang
 * @date: 2020/9/22 16:28
 */
@RestController
@Slf4j
public class elkController {

    @GetMapping("/elk/test")
    public boolean parse() {
        log.info("test");
        return true;
    }

    @GetMapping("/elk/test1")
    public boolean test1() {
        log.info("test1");
        return true;
    }

    @GetMapping("/elk/test2")
    public boolean test2() {
        log.info("test2");
        return true;
    }
}
