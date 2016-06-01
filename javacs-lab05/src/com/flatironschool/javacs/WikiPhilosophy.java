package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();
	static int parentheses; //keep track of the number of parentheses, make sure there is a correct amount
	static ArrayList<String> visited = new ArrayList<String>();

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
	 * 2. Ignoring external links, links to the current page, or red links
	 * 3. Stopping when reaching "Philosophy", a page with no links or a page
	 *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {


		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String urlPhilosophy = "https://en.wikipedia.org/wiki/Philosophy";
		boolean validURL = findURL(url, urlPhilosophy);

		if(validURL){ //keep going until we've reached the philosophy page
				System.out.println("We've reached the philosophy page.");
				return;
		}
		System.out.println("Unable to reach philosophy page.");

	}

	public static boolean findURL(String url, String target) throws IOException {
		Elements paragraphs = wf.fetchWikipedia(url);
		for(Element para: paragraphs){ //need to go through all paragraphs

			Iterable<Node> iter = new WikiNodeIterable(para);
			for (Node node: iter) {
				//node is either a TextNode or Element
				if (node instanceof TextNode) {
					String s = node.toString();
					if(s.contains("(")){
						parentheses++;
					}
					if(s.contains(")")){
						parentheses--;
					}
				}
				if (node instanceof Element) {
					Element link = (Element) node; //need to typecast it to access the tag
					if(link.tagName().equals("a") && link.parent().tagName().equals("p")){
						visited.add(link.absUrl("href"));
						if(link.absUrl("href").equals(target)){
							return true;
						} else {
							findURL(link.absUrl("href"), target);
							break;
						}
					}
				}
				if (visited.size() != 0){
					break;
				}
			}
		}
		if(parentheses != 0){
			return false;
		}
		return true;
	}
}