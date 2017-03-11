package MarkovExperiment;

import java.lang.reflect.Array;

/**
 * Created by TeeKay on 1/28/2017.
 */
public class Occurrences<T> implements Comparable<Occurrences<T>>{
    private T instance;
    private double occurences;
    //boolean in case more text has been added to the chain. In that case the occurrences field is not updated
    private boolean relevant;

    public Occurrences(T initialize){
        instance = initialize;
        relevant = false;
    }

    public Occurrences(Occurrences<T> source){
        instance = source.getInstance();
        relevant = false;
    }

    public double getOccurrences(){return occurences;}
    public void setOccurrences(double setTo){occurences = setTo;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Occurrences)) return false;

        Occurrences<?> that = (Occurrences<?>) o;

        return instance != null ? instance.equals(that.instance) : that.instance == null;
    }

    @Override
    public int hashCode() {
        return instance != null ? instance.hashCode() : 0;
    }
    public  T getInstance(){return instance;}

    public static <K> K[] createArray(Class<K> type, int size){
        return (K[])Array.newInstance(type, size);
    }

    public boolean isRelevant(){
        return relevant;
    }

    public void setRelevance(boolean setTo){
        relevant = setTo;
    }

    public String toString(){
        return instance.toString() + "->" +occurences + " ";
    }


    @Override
    public int compareTo(Occurrences<T> o) {
        return (int) Math.signum(occurences - o.occurences);
    }
}
