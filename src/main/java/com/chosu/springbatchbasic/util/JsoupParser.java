package com.chosu.springbatchbasic.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class JsoupParser {
	public static void main(String[] args) {
		String test_url = "https://www.dhlottery.co.kr/gameResult.do?method=byWin&wiselog=H_C_1_1";
		
		Document doc = JsoupParser.getDocHtml(test_url);
		
		//select를 이용하여 원하는 태그를 선택한다. select는 원하는 값을 가져오기 위한 중요한 기능이다.
		Elements element = getElBySelector(doc, ".num.win");
		
		List<String> list=  getStrListBySelector(element, "span.ball_645");
		for(int i = 0 ; i < list.size(); i++) {
			System.out.println(i +": "+list.get(i));
		}
		
		//System.out.println(getHTMLStrBySelector(element, ".ball2"));
		/*
		//Iterator을 사용하여 하나씩 값 가져오기
		Iterator<Element> ie1 = element.select("strong.rank").iterator();
		Iterator<Element> ie2 = element.select("strong.title").iterator();
		        
		while (ie1.hasNext()) {
			System.out.println(ie1.next().text()+"\t"+ie2.next().text());
		}*/
		
		System.out.println("============================================================");
	}
	
	public static Document getDocHtml(String url) {
		Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다

		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	public static Elements getElBySelector(Document doc, String selector) {
		return doc.select(selector);
	}
	
	public static Elements getNextElementSiblings(Elements elements, String selector) {
		return elements.next(selector);
	}
	
	public static List<Element> getElListBySelector(Elements element, String selector){
		List<Element> list = new ArrayList<Element>();
		Iterator<Element> ie1 = element.select(selector).iterator();
		while (ie1.hasNext()) {
			list.add(ie1.next());
		}
		return list;
	}
	
	public static List<String> getStrListBySelector(Elements element, String selector){
		List<String> list = new ArrayList<String>();
		Iterator<Element> ie1 = element.select(selector).iterator();
		while (ie1.hasNext()) {
			list.add(ie1.next().text());
		}
		return list;
	}
	
	public static String getHTMLStrBySelector(Elements element, String selector) {
		return element.select(selector).text();
	}
	
	public static String getHTMLStrBySelector(Element element, String selector) {
		return element.select(selector).text();
	}
	
	public static List<String> getHTMLListStrBySelector(Element element, String selector) {
		return element.select(selector).eachText();
	}
}
