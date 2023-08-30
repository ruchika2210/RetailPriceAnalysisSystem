package retail;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class PageRank {
    
    PriorityQueue<WebPageRankDetails> priorityQueue = new PriorityQueue<WebPageRankDetails>();
    Map<String, Integer> propertiesScoreMap;

    public PageRank(Map<String, Integer> propertyScoreMap) {
        this.propertiesScoreMap = propertyScoreMap;
    }

    public List<String> getTopKHotels(int k) {
        List<String> list = new LinkedList<>();
        for (int i=0;i<k;i++){
            if (priorityQueue.isEmpty()) {
                break;
            }
            list.add(priorityQueue.remove().propertyName);
        }
        return list;
    }
    
    public void orderPagesByRank() {
        for (String propertyName: propertiesScoreMap.keySet()) {
            int rank = propertiesScoreMap.get(propertyName);
            WebPageRankDetails webPage = new WebPageRankDetails(propertyName, rank);
            priorityQueue.add(webPage);
        }
    }
}