package com.platformpoint.automation.tfs;

import java.util.Map;

/**
 * Created by chq-gurmits on 3/15/2016.
 */
public class TestPlan {

    public long testPlanID;

    public long testRunID;
    /**
     * This map will store the mapping of a manual tets case Id in TFS with an automated test case
     */
    public Map<String, String> automatedMap;

    /**
     * This map will store the mapping of a manual tets case Id in TFS with the result of an automated test case
     */
    public Map<String, String> resultMap;


    public TestPlan(long id, Map<String, String> _automatedMap, Map<String, String> _resultMap) {

        testPlanID = id;
        automatedMap = _automatedMap;
        resultMap = _resultMap;
    }


}
