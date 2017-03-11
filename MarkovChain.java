package MarkovExperiment;

import java.util.*;

/**
 * Created by TeeKay on 1/24/2017.
 */
public class MarkovChain<T>{

    public HashMap<Occurrences<T>, HashMap> probabilities = new HashMap<>();
    //Observer pattern intended to update the occurrences to irrelevant when adding new data.
    LinkedList<Occurrences<T>> occurrences = new LinkedList<>();
    private int wordsCounted;

    //how many previous words decide the next word. Usually we would use 3.
    public MarkovChain(int initialWordCount){
        wordsCounted = initialWordCount;
    }
    
    public void addToChain(List<T> source){
        for (Occurrences<T> item : occurrences){
            item.setRelevance(false);
        }
        HashMap mapReference = null;
        Iterator<T> it = source.iterator();
        Occurrences<T> instance = new Occurrences<T>(source.get(0));
        Occurrences<T>[] words = Occurrences.createArray(instance.getClass(), wordsCounted + 1);
        for (int i = 0; i < words.length - 1; i++){
            words[i] =new Occurrences<T>(it.next());
            occurrences.add(words[i]);

        }


        while (it.hasNext()){
            mapReference = probabilities;
            //need to create to occurrences objects so that updates don't clash.
            for (int i = 0; i < words.length - 2; i++){
                words[i] = new Occurrences<T>(words[i]);
                occurrences.add(words[i]);
            }
            words[words.length - 1] = new Occurrences<T>(it.next());
            occurrences.add(words[words.length - 1]);

            int mapNumber = 0;
            //find the map from which you start creating
            while (mapNumber < words.length - 1 && mapReference.containsKey(words[mapNumber])){

                mapReference = (HashMap) mapReference.get(words[mapNumber++]);
            }
            if (mapNumber == words.length - 1){
                mapReference.put(words[mapNumber],(Double) mapReference.getOrDefault(words[mapNumber], 0d) + 1d);
            }
            else {
                HashMap<Occurrences<T>, Double> value= new HashMap();
                value.put(words[words.length - 1], 1d);
                if (mapNumber == words.length - 2) mapReference.put(words[mapNumber], value);
                else {
                    //generate the maps
                    int mapGenerator = (words.length - 3 - mapNumber);
                    HashMap<Occurrences<T>, HashMap>[] arrayMap =new HashMap[mapGenerator + 1];
                    arrayMap[mapGenerator] = new HashMap();
                    arrayMap[mapGenerator].put(words[mapGenerator-- + mapNumber + 1], value);
                    while (mapGenerator >= 0){
                        arrayMap[mapGenerator] = new HashMap<>();
                        arrayMap[mapGenerator].put(words[mapGenerator + mapNumber + 1], arrayMap[mapGenerator + 1]);
                        mapGenerator--;
                    }
                    mapReference.put(words[mapNumber], arrayMap[0]);
                }
            }
            //move the words, so that we have the next wordsCount ammount
            for (int i = 0; i < words.length - 1 ; i++){
                words[i] = words[i + 1];
            }
        }
    }


    private double mapNumber(HashMap map){
        double accumulator = 0;
        Iterator it = map.entrySet().iterator();
        Map.Entry entry =(Map.Entry) it.next();
        //Check only once if it is final
        boolean finalMap = false;
        if (entry.getValue() instanceof Double){
            accumulator += (Double) entry.getValue();
            finalMap = true;
        }
        else accumulator += mapNumber((HashMap) entry.getValue());

        while (it.hasNext()){
            entry =(Map.Entry) it.next();
            if (finalMap){
                accumulator += (Double) entry.getValue();
            }
            else accumulator += mapNumber((HashMap) entry.getValue());
        }

        return accumulator;
    }
    //This allows you to search through the probability map using the `lastWordsNumber' number of words from the stream list.
    public HashMap<T, Double> nextWords(ArrayList<T> stream, int lastWordsNumber){
        if (lastWordsNumber > wordsCounted) return null;
        boolean finalMap = false;
        if (lastWordsNumber == wordsCounted){
            finalMap = true;
        }
        return nextWords(stream, lastWordsNumber,finalMap, probabilities);
    }



    private HashMap<T, Double> nextWords(ArrayList<T> stream, int lastWordsNumber,boolean finalMap, HashMap map){
        if (map == null) return null;
        if (lastWordsNumber > stream.size()) return null;
        if (lastWordsNumber == 0) {
            HashMap<T, Double> toRet = new HashMap();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                if (finalMap){
                    toRet.put(((Occurrences<T>) entry.getKey()).getInstance(), (Double) entry.getValue());
                }
                else{
                    Occurrences<T> instance = ((Occurrences<T>) entry.getKey());
                    if (instance.getOccurrences()== 0 || !instance.isRelevant()){
                        instance.setOccurrences(mapNumber((HashMap) entry.getValue()));
                        instance.setRelevance(true);
                    }
                    toRet.put(instance.getInstance(),instance.getOccurrences());
                }
            }
            return toRet;
        }
        else return nextWords(stream, lastWordsNumber - 1, finalMap, (HashMap) map.get(new Occurrences<T>(stream.get(stream.size() - lastWordsNumber))));
    }

}
