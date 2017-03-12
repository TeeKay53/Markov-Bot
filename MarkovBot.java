package MarkovExperiment;



import java.util.*;

/**
 * Stores the text generated so far and the lastWordsNumber variable, which it decrements when it request the next words and receives a null value
 */
public class MarkovBot {
    private MarkovChain chain;
    private ArrayList<String> textSoFar = new ArrayList<>();
    private int lastWordsNumber;
    private static String punctuation = ".!?";

    public MarkovBot(MarkovChain info){
        chain = info;
        lastWordsNumber = 0;

    }

    /**
     * Picks randomly from the most frequent words.
     * @param randomness
     */
    public void next(int randomness){

        HashMap<String, Double> predictions = chain.nextWords(textSoFar, lastWordsNumber);

        while (predictions == null){
            predictions = chain.nextWords(textSoFar, --lastWordsNumber);
        }

        if (lastWordsNumber == 0){
            int randomRank =(int)(Math.random() * predictions.size());
            int rank = 1;
            for (Map.Entry entry : predictions.entrySet()){
                if (rank++ == randomRank){
                    String word = (String) entry.getKey();
                    if (!word.startsWith("http") && punctuation.indexOf(word.charAt(word.length() - 1)) == -1) {
                        textSoFar.add((String) entry.getKey());
                        lastWordsNumber = 1;
                    }
                    else next(randomness);
                }
            }
        }
        else {
            Map<String, Double> inOrder = MapUtil.sortByValue(predictions);
            //create a random integer so that we get the word with that rank.
            int random = (int) (Math.random() * Math.min(randomness, predictions.size()));
            int counter = 0;
            String likelyWord = "";
            for (Map.Entry entry : inOrder.entrySet()) {
                if (counter++ == random) {
                    likelyWord = (String) entry.getKey();
                }
            }

            textSoFar.add(likelyWord);
            lastWordsNumber++;

            if (punctuation.indexOf(likelyWord.charAt(likelyWord.length() - 1)) != -1) {
                lastWordsNumber = 0;
            }
        }
    }

    public String generateSentence(int randomness){
        next(randomness);
        while (lastWordsNumber != 0){
            next(randomness);
        }
        if (textSoFar.size() > 5 && textSoFar.size() < 20 ) {
            String sentence = process();
            textSoFar.clear();
            return sentence;
        }
        else {
            textSoFar.clear();
            return generateSentence(randomness);
        }
    }

    /**
     * Generates a distribution of the words and samples over it.
     */
    public void nextProbabilistic(){
        HashMap<String, Double> predictions = chain.nextWords(textSoFar, lastWordsNumber);

        while (predictions == null){
            predictions = chain.nextWords(textSoFar, --lastWordsNumber);
        }

        if (lastWordsNumber == 0){
            int randomRank =(int)(Math.random() * predictions.size());
            int rank = 1;
            for (Map.Entry entry : predictions.entrySet()){
                if (rank++ == randomRank){
                    String word = (String) entry.getKey();
                    if (!word.startsWith("http") && punctuation.indexOf(word.charAt(word.length() - 1)) == -1) {
                        textSoFar.add((String) entry.getKey());
                        lastWordsNumber = 1;
                    }
                    else nextProbabilistic();
                }
            }
        }else{
            //store them in order so that the biggest probabilistic ones are at the beginning.
            Map<String, Double> inOrder = MapUtil.sortByValue(predictions);
            int total = 0;
            for (Map.Entry<String, Double> entry : inOrder.entrySet()){
                total += entry.getValue();
            }
            double sumSoFar = 0;
            for (Map.Entry<String, Double> entry : inOrder.entrySet()){
                double thisProb = entry.getValue()/total;
                sumSoFar += thisProb;
                entry.setValue(sumSoFar);
            }
            String likelyWord = "";
            double random = Math.random();
            for (Map.Entry<String, Double> entry : inOrder.entrySet()){
                if (entry.getValue() > random){
                    likelyWord = entry.getKey();
                }
            }
            textSoFar.add(likelyWord);
            lastWordsNumber++;

            if (punctuation.indexOf(likelyWord.charAt(likelyWord.length() - 1)) != -1) {
                lastWordsNumber = 0;
            }
        }
    }

    public String generateSentenceProbabilistic(){
        nextProbabilistic();
        while (lastWordsNumber != 0 && textSoFar.size() < 20){
            nextProbabilistic();
        }
        if (textSoFar.size() > 5 && textSoFar.size() < 20 ) {
            String sentence = process();
            textSoFar.clear();
            return sentence;
        }
        else {
            textSoFar.clear();
            return generateSentenceProbabilistic();
        }
    }

    //process it to a sentence
    public String process() {
        boolean start = true;
        String sentence = "";
        for (String string : textSoFar) {
            if (start) {
                sentence += string.substring(0, 1).toUpperCase() + string.substring(1) + " ";
                start = false;
            } else {
                sentence += string + " ";
                if (string.equals(".")) {
                    start = true;
                }
            }
        }
        return sentence;
    }

    public void clear(){
        textSoFar.clear();
        lastWordsNumber = 0;
    }
}
