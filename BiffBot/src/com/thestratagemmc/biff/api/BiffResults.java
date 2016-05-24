package com.thestratagemmc.biff.api;

import java.util.*;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffResults { /* Queue for results of modules to sit until they have all been executed */

    public Map<UUID,List<BiffSolution>> solutions = new HashMap<>();

    public void addSolution(UUID id, BiffSolution solution){
        if (solutions.containsKey(id)){
            List<BiffSolution> s = solutions.get(id);
            s.add(solution);
            solutions.put(id, s);
        }
        else{
            List<BiffSolution> s = new ArrayList<>();
            s.add(solution);
            solutions.put(id, s);
        }
    }

    public List<BiffSolution> getSolutions(UUID id){
        if (!solutions.containsKey(id)) return new ArrayList<>();
        else return solutions.get(id);
    }

    public void clearSolutions(UUID id){
        if (solutions.containsKey(id)) {
            solutions.remove(id);
        }
    }

}
