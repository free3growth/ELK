package com.touchair.eselk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class EsElkApplicationTests {
    @Test
    void test(){
        log.info("java LogStash");
        log.info("1\n"+"2\n" );
//        try {
//            throw new RuntimeException("robot exception");
//        }catch (Exception e){
//            log.error("handleException", e);
//        }
    }


}
