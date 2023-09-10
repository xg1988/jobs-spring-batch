package com.chosu.springbatchbasic;

import com.chosu.springbatchbasic.util.DateUtil;
import com.chosu.springbatchbasic.util.JsoupParser;
import com.chosu.springbatchbasic.util.StringUtil;
import com.chosu.springbatchbasic.util.URLConst;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class NaverIPOService {



    public List<NaverIPODto> getNaverIPOList(){
        log.info("NaverIPOService getNaverIPOList start!!");
        List<NaverIPODto> list = new ArrayList<NaverIPODto>();

        Document doc            = JsoupParser.getDocHtml(URLConst.NAVER_IPO_REQ_URL);
        Elements element        = JsoupParser.getElBySelector(doc, "table.type_7");
        List<org.jsoup.nodes.Element> elList    = JsoupParser.getElListBySelector(element, "tr");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.info("[기준일자] :{}", today);

        for(int i =0; i < elList.size(); i++) {
            Element el = elList.get(i);
            String detailDepth1Url = el.getElementsByClass("lst")
                                        .attr("href");
            if(!"".equals(StringUtil.nvl(detailDepth1Url, ""))) {
                Document detailDoc = JsoupParser.getDocHtml(detailDepth1Url);
            }

            if(!"".equals(el.getElementsByClass("item_name").text())
                    && !"미정".equals(el.getElementsByClass("area_private").text().replaceAll("개인청약 ", ""))) {

                NaverIPODto naverIPODto = new NaverIPODto().builder()
                        .id(today + "_" + el.getElementsByClass("item_name").text().split(" ")[1].trim())
                        .gongmoComp(el.getElementsByClass("area_sup").text().replaceAll("주관사 ", ""))
                        .compCategory(el.getElementsByClass("area_type").text().replaceAll("업종 ", ""))
                        .compName(el.getElementsByClass("item_name").text().split(" ")[1])
                        .gongmoState(el.getElementsByClass("area_state").text().replaceAll("진행상태 ", "").split(" ")[0])
                        .gongmoPrice(el.getElementsByClass("area_price").text().replaceAll("공모가 ", ""))
                        .marketName(el.getElementsByClass("item_name").text().split(" ")[0])
                        .listingDate(el.getElementsByClass("area_list").text().replaceAll("상장일 ", ""))
                        .competition(el.getElementsByClass("area_competition").text().replaceAll("개인청약경쟁률 ", ""))
                        .requestTerm(el.getElementsByClass("area_private").text().replaceAll("개인청약 ", ""))
                        .registDate(today)
                        .registTime(LocalDateTime.now()).build();
                list.add(naverIPODto);
            }
        }
        return list;
    }
}
