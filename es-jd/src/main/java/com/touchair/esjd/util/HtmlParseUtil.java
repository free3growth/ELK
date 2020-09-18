package com.touchair.esjd.util;

import cn.hutool.core.util.StrUtil;
import com.touchair.esjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析网页 获取数据
 *
 * @author: bin.wang
 * @date: 2020/9/17 16:03
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws Exception {
        new HtmlParseUtil().parseJD("vue").forEach(System.out::println);
    }

    public List<Content> parseJD(String keywords)throws Exception{
        //获得请求 https://search.jd.com/Search?keyword=java
        //前提需要联网
        String url = " https://search.jd.com/Search?keyword="+keywords;
        /**
         * document 就是浏览器中的document对象
         * 所有js中能用的方法，这里都能用
         */
        Document document = Jsoup.parse(new URL(url),30000);
        ArrayList<Content> goodList = new ArrayList<>();
        //获取所有的 <li></li>标签
        Elements elementLis = document.getElementsByTag("li");
        for (Element element : elementLis) {
            String img = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = element.getElementsByClass("p-price").eq(0).text();
            String title = element.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            if (StrUtil.isNotBlank(img) & StrUtil.isNotBlank(price) & StrUtil.isNotBlank(title)) {
                content.setImg(img);
                content.setPrice(price);
                content.setTitle(title);
                goodList.add(content);
            }
        }
        return goodList;
    }
}


