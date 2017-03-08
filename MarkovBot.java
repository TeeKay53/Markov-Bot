package MarkovExperiment;



import java.util.*;

/**
 * A bot that outputs sentences. I can be used to write paragraphs, but the sentences will not be relate one to the other.
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
    public void next(int randomness){

        HashMap<String, Double> predictions = chain.nextWords(textSoFar, lastWordsNumber);

        while (predictions == null){
            predictions = chain.nextWords(textSoFar, --lastWordsNumber);
        }
        System.out.println(predictions);
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
                    else next(3);
                }
            }
        }
        else {
            Map<String, Double> inOrder = MapUtil.sortByValue(predictions);
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
        String sentence = "";
        for (String word : textSoFar){
            sentence += word + " ";
        }
        if (textSoFar.size() > 5 && textSoFar.size() < 20) {
            print();
            textSoFar.clear();
            return sentence;
        }
        else {
            textSoFar.clear();
            return generateSentence(randomness);
        }
    }

    public void print() {
        boolean start = true;
        for (String string : textSoFar) {
            if (start) {
                System.out.print(string.substring(0, 1).toUpperCase() + string.substring(1) + " ");
                start = false;
            } else {
                System.out.print(string + " ");
                if (string.equals(".")) {
                    start = true;
                }
            }
        }
        System.out.println();
    }

    public void clear(){
        textSoFar.clear();
        lastWordsNumber = 0;
    }
}
