package retail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WordFrequency {
    Map<String, Map<String, Integer>> wordFreqMapOfProperties = new HashMap<>();
    Map<String, PropertyDetails> propertyMap = new HashMap<>();
    

    public WordFrequency(Map<String, PropertyDetails> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public void setWordFrequencies() {
        for (PropertyDetails prop : propertyMap.values()) {
            String[] words = prop.getPropertyPageWords();
            String propName = prop.getPropertyAddress();

            Map<String, Integer> wordFreqMap = new HashMap<>();
            for (String w : words) {
                String word = w.toLowerCase();
                if (wordFreqMap.containsKey(word)) {
                    // System.out.println("inside contains");
                	wordFreqMap.put(word, wordFreqMap.get(word) + 1);
                } else {
                	wordFreqMap.put(word, 1);
                }
            }

            wordFreqMapOfProperties.put(propName, wordFreqMap);
        }
    }

    public Map<String, Integer> calculateScores(String[] keywords, Set<String> propSet) {
        Map<String, Integer> documentScoreMap = new HashMap<>();
        for (String propName : propSet) {
            Map<String, Integer> wordFrequencyMap = wordFreqMapOfProperties.get(propName);
            int score = 0;
            for (String keyword : keywords) {
                if (wordFrequencyMap.containsKey(keyword)) {
                    score += wordFrequencyMap.get(keyword);
                }
            }
            documentScoreMap.put(propName, score);
        }
        return documentScoreMap;
    }
}
