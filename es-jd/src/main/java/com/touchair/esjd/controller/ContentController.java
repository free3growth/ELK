package com.touchair.esjd.controller;

import com.touchair.esjd.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: bin.wang
 * @date: 2020/9/18 13:37
 */
@RestController
public class ContentController {

    @Resource
    private ContentService contentService;

    /**
     * 将数据存入ES中
     *
     * @param keywords
     * @return
     * @throws Exception
     */
    @GetMapping("/parse/{keywords}")
    public boolean parse(@PathVariable(value = "keywords") String keywords) throws Exception {
        return contentService.parseContent(keywords);
    }

    @GetMapping("/search/{keywords}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable(value = "keywords") String keyword,
                                           @PathVariable(value = "pageNo") int pageNo,
                                           @PathVariable(value = "pageSize") int pageSize) throws IOException {
        return contentService.searchPage(keyword, pageNo, pageSize);
    }

    @GetMapping("/searchHighLight/{keywords}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchHighLight(@PathVariable(value = "keywords") String keyword,
                                           @PathVariable(value = "pageNo") int pageNo,
                                           @PathVariable(value = "pageSize") int pageSize) throws IOException {
        return contentService.searchHighLight(keyword, pageNo, pageSize);
    }
}
