package org.reactome.server.analysis.core.result.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class AnalysisReport {

    private static final Logger logger = LoggerFactory.getLogger("analysisReport");


    private enum AnalysisAction {
        EXEC                ("_ANALYSIS_ _EXEC_"),
        CACHE               ("_ANALYSIS_ _CACHE_"),
        DOWNLOAD_RESULT     ("_DOWNLOAD_ _RESULT_"),
        DOWNLOAD_MAPPING    ("_DOWNLOAD_ _MAPPING_"),
        DOWNLOAD_NOT_FOUND  ("_DOWNLOAD_ _NOTFOUND_");

        private String str;

        AnalysisAction(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public static void reportCachedAnalysis(ReportParameters parameters){
        report(AnalysisAction.CACHE, parameters);
    }

    public static void reportNewAnalysis(ReportParameters parameters){
        report(AnalysisAction.EXEC, parameters);
    }

    public static void reportResultDownload(ReportParameters parameters){
        report(AnalysisAction.DOWNLOAD_RESULT, parameters);
    }

    public static void reportMappingDownload(ReportParameters parameters){
        report(AnalysisAction.DOWNLOAD_MAPPING, parameters);
    }

    public static void reportNotFoundDownload(ReportParameters parameters){
        report(AnalysisAction.DOWNLOAD_NOT_FOUND, parameters);
    }

    private static void report(AnalysisAction action, ReportParameters parameters){
        logger.info(action.toString() + " " + parameters);
    }
}
