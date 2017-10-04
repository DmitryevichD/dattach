package by.mercom.mail.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcResult {
    private final Map<String, Boolean> filePatternProc = new HashMap<>();
    private final List<String> errorList = new ArrayList();

    public void addFileName(String fileName, boolean procResult) {
        filePatternProc.put(fileName, procResult);
    }

    public void addError(String error) {
        errorList.add(error);
    }

    public boolean msgIsProcessed(){
        if(!errorList.isEmpty()) return false;
        if(filePatternProc.isEmpty()) return false;
        return !filePatternProc.values().contains(false);
    }

    public void printResult(){
        System.err.println("Errors:" + errorList.size());
        for (String s : errorList) {
            System.err.println(s);
        }
        for (Map.Entry<String, Boolean> files : filePatternProc.entrySet()) {
            System.out.println(files.getKey() + ":" + files.getValue());
        }
    }
}
