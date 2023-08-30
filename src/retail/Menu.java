
package retail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public final class Menu {

	public String[] keywords;
	private static Trie locationTrie = new Trie();
	private static List<String> locationList = new ArrayList<>();
	private static SplayTree splayTree = new SplayTree();

	public static Trie getLocationTrie() {
		return locationTrie;
	}

	public static List<String> getLocationList() {
		return locationList;
	}

	private void startApplication() {
		int caseValue = 0;
		Scanner sc = new Scanner(System.in);
		while (caseValue != 3) {
			try {
				System.out.println("      MENU\n" + "-------------------------\n" + "1. Search based on Filters.\n"
						+ "2. Create Word Frequency and Search for a word.\n" + "3. Exit.\n" + "\n"
						+ "Select an option from above: ");
				String next = sc.next();
				caseValue = Integer.parseInt(next);
				switch (caseValue) {
				case 1:
					FilterDetails fd = new FilterDetails();
					provideOption(sc, fd);
					CrawlMultiWebsite cw = new CrawlMultiWebsite();
					cw.beginCrawling(sc, fd);
					filterAndShowResult(sc, fd);
					break;
				case 2:
					search(sc);
					break;
				case 3:
					System.out.println("Exiting...");
					System.exit(0);
					break;
				default:
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Invalid selection. Try again (Press Enter key to continue)");
				e.printStackTrace();
				sc.next();
			}
		}
	}

	private void provideOption(Scanner sc, FilterDetails fd) {

		boolean locationCheck = false;

		while (!locationCheck)
			locationCheck = fetchLocationDetails(sc, fd);
	}

	private boolean fetchLocationDetails(Scanner sc, FilterDetails filterDetails) {

		EditDistance ed = new EditDistance();
		String selectedCity;
		do {
			System.out.println("Enter a Location: ");
			selectedCity = sc.next();
			selectedCity = ed.wordSuggestionsSpellCheck(sc, selectedCity.toLowerCase());
			if (!selectedCity.equals("")) {
				break;
			}
		} while ("".equalsIgnoreCase(selectedCity));

		filterDetails.setCity(selectedCity.toLowerCase());
		String proviceByCityName = getProviceByCityName(selectedCity.toLowerCase());
		filterDetails.setProvince(proviceByCityName);
		System.out.println("City: " + selectedCity);
		System.out.println("province: " + proviceByCityName);

		if (!"".equalsIgnoreCase(selectedCity) && !"".equalsIgnoreCase(proviceByCityName))
			return true;
		else
			return false;

	}

	private String getProviceByCityName(String cityLocation) {

		ProvinceStateMap pm = new ProvinceStateMap();

		for (Map.Entry<String, List<String>> entry : pm.getProvinceStateMap().entrySet()) {
			String province = entry.getKey();
			List<String> cities = entry.getValue();
			for (String city : cities) {
				if (city.equalsIgnoreCase(cityLocation))
					return province;
			}
		}
		System.out.println("Entered city is not in the provice list, Please try with new city");
		return "";

	}

	void search(Scanner sc) {
		
		WordFrequency wf = new WordFrequency(RetailPropertyDetails.getRetailPropertyList());
		wf.setWordFrequencies();
		
		LocationInvertedIndex locationIndex = new LocationInvertedIndex(RetailPropertyDetails.getRetailPropertyList());
		locationIndex.createIndex();
		
		fetchFrequencyOfSearchWord();

		keywords = getSearchKeywords(sc);
		Set<String> documentSet = locationIndex.search(keywords);
		Map<String, Integer> scoreMap = wf.calculateScores(keywords, documentSet);

		performPageRanking(scoreMap);
	}

	private void performPageRanking(Map<String, Integer> scoreMap) {
		PageRank pagerank = new PageRank(scoreMap);
		pagerank.orderPagesByRank();
		List<String> propertiesNamesList = pagerank.getTopKHotels(10);
		if (!propertiesNamesList.isEmpty()) {
			System.out.println("\nSearched string was found in following pages:");
			for (String propertyName : propertiesNamesList) {
				System.out.println("  -- " + propertyName);
			}
		} else {
			System.out.println("Sorry! text you entered was not found.");
		}
		System.out.print("\n");
	}

	private void fetchFrequencyOfSearchWord() {
		WordSearchCount frequency = splayTree.root;
		if (Objects.nonNull(frequency))
			System.out.println("Recent search word is '" + frequency.getWord() + "'with a frequency of "+ frequency.getCount() + "\n");
	}

	private void filterAndShowResult(Scanner sc, FilterDetails fd) {
		new FilterAndShowResult().finalResult(fd);
	}

	String[] getSearchKeywords(Scanner sc) {
		System.out.print("Enter the Search query: ");
		String query = sc.next();

		saveInSplayTree(query);
		String[] words = query.toLowerCase().split("\\s+");
		System.out.println(Arrays.toString(words));
		return words;
	}

	private void saveInSplayTree(String query) {

		WordSearchCount queryFrequency = new WordSearchCount(query, 1);
		WordSearchCount searchedQueryFrequency = splayTree.search(queryFrequency);
		if (searchedQueryFrequency != null)
			searchedQueryFrequency.setCount(searchedQueryFrequency.getCount() + 1);
		else {
			splayTree.insert(queryFrequency);
		}
	}

	public static void main(String[] args) {
		
		locationList = LocationScraper.scraper();
		locationTrie.insertWords(locationList);

		Menu menu = new Menu();
		menu.startApplication();
	}
}
