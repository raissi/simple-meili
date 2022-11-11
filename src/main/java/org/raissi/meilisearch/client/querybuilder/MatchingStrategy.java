package org.raissi.meilisearch.client.querybuilder;

public enum MatchingStrategy {
    LAST{
        @Override
        public String toString() {
            return "last";
        }
    },
    ALL{
        @Override
        public String toString() {
            return "all";
        }
    }
}
