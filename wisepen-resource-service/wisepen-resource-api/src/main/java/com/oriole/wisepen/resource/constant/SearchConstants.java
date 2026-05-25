package com.oriole.wisepen.resource.constant;

public interface SearchConstants {

    String RESOURCE_INDEX_NAME = "wisepen_resource_index";

    String ANALYZER_IK_MAX_WORD = "ik_max_word";
    String ANALYZER_IK_SMART = "ik_smart";

    String[] BOOSTED_SEARCH_FIELDS = {"resourceName^2", "content"};

    String HIGHLIGHT_PRE_TAG = "<em class=\"wp-highlight\">";
    String HIGHLIGHT_POST_TAG = "</em>";
    int HIGHLIGHT_FRAGMENT_SIZE = 100;
    int HIGHLIGHT_MAX_FRAGMENTS = 3;
    String HIGHLIGHT_FRAGMENT_SEPARATOR = "...";

    String ES_DATE_FORMAT_PATTERN =
            "yyyy-MM-dd'T'HH:mm:ss.SSS||yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd HH:mm:ss||epoch_millis";

    int MIN_PAGE_NUM = 1;
    int DEFAULT_PAGE_NUM = 1;
    int MIN_PAGE_SIZE = 1;
    int MAX_PAGE_SIZE = 100;
    int DEFAULT_PAGE_SIZE = 20;
}
